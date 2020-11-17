package ru.gaidamaka.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gaidamaka.config.GameConfig;
import ru.gaidamaka.game.cell.Cell;
import ru.gaidamaka.game.cell.CellType;
import ru.gaidamaka.game.cell.Point;
import ru.gaidamaka.game.player.Player;
import ru.gaidamaka.game.player.PlayerWithScore;
import ru.gaidamaka.game.snake.Snake;
import ru.gaidamaka.game.snake.SnakeInfo;

import java.util.*;
import java.util.stream.Collectors;

public class Game implements Observable {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    private static final int SIZE_OF_EMPTY_SQUARE_FOR_SNAKE = 5;
    public static final String UNKNOWN_PLAYER_ERROR_MESSAGE = "Unknown player";

    private final Map<Player, Snake> playersWithSnakes;
    private final Map<Player, Integer> playersScores;
    private final List<Snake> zombieSnakes;
    private final GameConfig config;
    private final List<Cell> fruits;
    private final GameField field;
    private final ArrayList<Observer> observers;
    private final Random random = new Random();
    private int stateID;

    public Game(@NotNull GameConfig config) {
        this.config = Objects.requireNonNull(config, "Config cant be null");
        field = new GameField(config.getFieldWidth(), config.getFieldHeight());
        playersWithSnakes = new HashMap<>();
        playersScores = new HashMap<>();
        observers = new ArrayList<>();
        zombieSnakes = new ArrayList<>();
        stateID = 0;
        fruits = new ArrayList<>(config.getFoodStaticNumber());
        generateFruits();
    }

    public Game(@NotNull GameState state) {
        config = state.getGameConfig();
        field = new GameField(config.getFieldWidth(), config.getFieldHeight());
        stateID = state.getStateID();
        observers = new ArrayList<>();
        zombieSnakes = new ArrayList<>();
        playersWithSnakes = new HashMap<>();
        List<SnakeInfo> snakeInfos = state.getSnakeInfos();
        snakeInfos.forEach(snakeInfo -> {
            Snake snake = createSnakeFromSnakeInfo(snakeInfo);
            markSnakeOnField(snake);
            if (snakeInfo.isZombieSnake()) {
                zombieSnakes.add(snake);
            } else {
                Player snakeOwner = snakeInfo.getPlayer()
                        .orElseThrow(
                                () -> new IllegalStateException("Cant get player from alive snake")
                        );
                playersWithSnakes.put(snakeOwner, snake);
            }
        });
        playersScores = new HashMap<>();
        state.getActivePlayers().forEach(
                playerWithScore -> playersScores.put(
                        playerWithScore.getPlayer(),
                        playerWithScore.getScore()
                )
        );
        fruits = new ArrayList<>();
        state.getFruits().forEach(fruit -> {
            field.set(fruit, CellType.FRUIT);
            fruits.add(new Cell(fruit, CellType.FRUIT));
        });

    }

    private void markSnakeOnField(Snake snake) {
        for (Point snakePoint : snake) {
            field.set(snakePoint, CellType.SNAKE);
        }
    }

    @NotNull
    private Snake createSnakeFromSnakeInfo(SnakeInfo snakeInfo) {
        return new Snake(
                snakeInfo.getSnakePoints(),
                snakeInfo.getDirection(),
                config.getFieldWidth(),
                config.getFieldHeight()
        );
    }


    public void registrationNewPlayer(@NotNull String playerName) {
        Player player = Player.create(playerName);
        List<Cell> headAndTailOfNewSnake = getNewSnakeHeadAndTail();
        if (headAndTailOfNewSnake.isEmpty()) {
            throw new IllegalStateException("Cant add new player because no space on field");
        }
        Snake playerSnake = new Snake(
                headAndTailOfNewSnake.get(0).asPoint(),
                headAndTailOfNewSnake.get(1).asPoint(),
                field.getWidth(),
                field.getHeight()
        );
        headAndTailOfNewSnake.forEach(cell -> field.set(cell.getY(), cell.getX(), CellType.SNAKE));
        playersWithSnakes.put(player, playerSnake);
        playersScores.put(player, 0);
    }

    private List<Cell> getNewSnakeHeadAndTail() {
        Optional<Cell> centerOfEmptySquareOnField = field.findCenterOfSquareWithOutSnakeSquare(SIZE_OF_EMPTY_SQUARE_FOR_SNAKE);
        if (centerOfEmptySquareOnField.isEmpty()) {
            return Collections.emptyList();
        }
        Cell snakeHead = centerOfEmptySquareOnField.get();
        Optional<Cell> snakeTail = findTailWithoutFruit(snakeHead);
        if (snakeTail.isEmpty()) {
            return Collections.emptyList();
        }
        return List.of(snakeHead, snakeTail.get());
    }

    private Optional<Cell> findTailWithoutFruit(Cell head) {
        List<Cell> supposedTails = new ArrayList<>(List.of(
                field.get(head.getY() - 1, head.getX()),
                field.get(head.getY() + 1, head.getX()),
                field.get(head.getY(), head.getX() - 1),
                field.get(head.getY(), head.getX() + 1)
        ));
        for (Cell cell : supposedTails) {
            if (cell.getType() == CellType.EMPTY) {
                return Optional.of(cell);
            }
        }
        return Optional.empty();
    }

    public void removePlayer(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cant be null");
        if (!playersWithSnakes.containsKey(player)) {
            throw new IllegalArgumentException(UNKNOWN_PLAYER_ERROR_MESSAGE);
        }
        zombieSnakes.add(playersWithSnakes.get(player));
        markPlayerInactive(player);
    }

    private void markPlayerInactive(@NotNull Player player) {
        playersWithSnakes.remove(player);
        playersScores.remove(player);
    }

    private void makeMove(@NotNull Player player, @Nullable Direction direction) {
        Objects.requireNonNull(player, "Player cant be null");
        if (!playersWithSnakes.containsKey(player)) {
            throw new IllegalArgumentException(UNKNOWN_PLAYER_ERROR_MESSAGE);
        }
        Snake snake = playersWithSnakes.get(player);
        if (direction == null) {
            snake.makeMove();
        } else {
            snake.makeMove(direction);
        }
        if (isPlayerLost(player)) {
            handlePlayerLose(player, snake);
            return;
        }
        if (isSnakeAteFruit(snake)) {
            incrementScore(player);
            removeFruit(snake.getHead());
        } else {
            field.set(snake.getTail(), CellType.EMPTY);
            snake.removeTail();
        }
        field.set(snake.getHead(), CellType.SNAKE);
    }

    private void removeFruit(Point fruitForRemove) {
        fruits.removeIf(fruit -> fruitForRemove.equals(fruit.asPoint()));
    }

    private void handlePlayerLose(Player player, Snake playerSnake) {
        makeFruitsFromSnakeWithProbability(playerSnake);
        markPlayerInactive(player);
    }

    public GameConfig getConfig() {
        return config;
    }

    public void makeAllPlayersMove(@NotNull Map<Player, Direction> playersMoves) {
        int fruitsBeforeMoves = fruits.size();
        playersWithSnakes
                .keySet()
                .forEach(
                        player -> makeMove(player, playersMoves.getOrDefault(player, null))
                );
        zombieSnakes.forEach(Snake::makeMove);
        if (fruitsBeforeMoves != fruits.size()) {
            generateFruits();
        }
        notifyObservers();
    }

    private void generateFruits() {
        int aliveSnakesCount = playersWithSnakes.size();
        int requiredFruitsNumber = config.getFoodStaticNumber() + config.getFoodPerPlayer() * aliveSnakesCount;
        if (field.getEmptyCellsNumber() < requiredFruitsNumber) {
            logger.debug("Cant generate required number of fruits={}, empty cells number={}",
                    requiredFruitsNumber,
                    field.getEmptyCellsNumber()
            );
            return;
        }
        while (fruits.size() < requiredFruitsNumber) {
            Cell randomEmptyCell = field.findRandomEmptyCell()
                    .orElseThrow(() -> new IllegalStateException("Cant find empty cell"));
            field.set(randomEmptyCell.asPoint(), CellType.FRUIT);
            fruits.add(randomEmptyCell);
        }
    }

    private void incrementScore(Player player) {
        if (playersScores.containsKey(player)) {
            int prevScore = playersScores.get(player);
            playersScores.put(player, prevScore + 1);
        }
        throw new IllegalArgumentException(UNKNOWN_PLAYER_ERROR_MESSAGE);
    }

    private boolean isSnakeAteFruit(Snake snake) {
        Point snakeHead = snake.getHead();
        for (Cell fruit : fruits) {
            if (snakeHead.equals(fruit.asPoint())) {
                return true;
            }
        }
        return false;
    }

    private void makeFruitsFromSnakeWithProbability(Snake snake) {
        for (Point p : snake) {
            if (random.nextDouble() < config.getProbabilityOfDeadSnakeCellsToFood()) {
                field.set(p, CellType.FRUIT);
            } else {
                field.set(p, CellType.EMPTY);
            }
        }
    }

    private boolean isPlayerLost(Player player) {
        Snake playerSnake = playersWithSnakes.get(player);
        for (Map.Entry<Player, Snake> playerWithSnake : playersWithSnakes.entrySet()) {
            Snake otherSnake = playerWithSnake.getValue();
            if (otherSnake.isSnake(playerSnake.getHead())) {
                if (!otherSnake.equals(playerSnake)) {
                    incrementScore(playerWithSnake.getKey());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }


    @Override
    public void notifyObservers() {
        GameState gameState = generateGameState();
        for (Observer observer : observers) {
            observer.update(gameState);
        }
    }

    private GameState generateGameState() {
        int currentStateID = this.stateID++;
        return new GameState(
                getFruitsAsPointsList(),
                generatePlayersWithTheirScoresList(),
                generateSnakeInfosList(),
                config,
                currentStateID
        );
    }

    @NotNull
    private List<Point> getFruitsAsPointsList() {
        return fruits.stream()
                .map(Cell::asPoint)
                .collect(Collectors.toList()
                );
    }

    @NotNull
    private List<SnakeInfo> generateSnakeInfosList() {
        List<SnakeInfo> snakeInfos = new ArrayList<>(playersWithSnakes.size());
        playersWithSnakes.values().forEach(snake -> snakeInfos.add(new SnakeInfo(snake)));
        zombieSnakes.forEach(snake -> snakeInfos.add(new SnakeInfo(snake)));
        return snakeInfos;
    }

    @NotNull
    private List<PlayerWithScore> generatePlayersWithTheirScoresList() {
        List<PlayerWithScore> playerWithScores = new ArrayList<>(playersScores.size());
        playersScores.forEach((player, score) -> playerWithScores.add(new PlayerWithScore(player, score)));
        return playerWithScores;
    }


    public int getFieldWidth() {
        return field.getWidth();
    }

    public int getFieldHeight() {
        return field.getHeight();
    }
}
