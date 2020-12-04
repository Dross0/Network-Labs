package ru.gaidamaka.gui;

import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.game.Direction;
import ru.gaidamaka.game.Game;
import ru.gaidamaka.game.GameObserver;
import ru.gaidamaka.game.GameState;
import ru.gaidamaka.game.cell.Point;
import ru.gaidamaka.game.player.Player;
import ru.gaidamaka.game.player.PlayerWithScore;
import ru.gaidamaka.game.snake.SnakeInfo;
import ru.gaidamaka.presenter.GamePresenter;
import ru.gaidamaka.presenter.MoveEvent;
import ru.gaidamaka.presenter.UserEvent;
import ru.gaidamaka.presenter.UserEventType;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MoveHandler implements GamePresenter, GameObserver {
    private final long movePeriodMs;
    private Player player;
    private final Map<Player, Direction> moves = new ConcurrentHashMap<>();
    private Game game;

    private GameState prevGameState;
    private View view;
    private Player p;

    @NotNull
    private final PlayerColorMapper colorMapper;

    public MoveHandler(long movePeriodMs) {
        this.movePeriodMs = movePeriodMs;
        this.colorMapper = new PlayerColorMapper();
    }

    public void setView(@NotNull View view) {
        this.view = view;
    }

    public void setGame(@NotNull Game game) {
        this.game = Objects.requireNonNull(game, "Game cant be null");
        this.game.addObserver(this);
    }

    public void start() {
        player = game.registrationNewPlayer("Dross");
        this.p = game.registrationNewPlayer("JJ");
        Player tmp = game.registrationNewPlayer("Bob");
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                game.removePlayer(p);
                Thread.sleep(3000);
                game.removePlayer(tmp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(movePeriodMs);
                } catch (InterruptedException e) {
                    return;
                }
                game.makeAllPlayersMove(moves);
            }
        }).start();
    }

    @Override
    public void fireEvent(@NotNull UserEvent userEvent) {
        Objects.requireNonNull(userEvent, "User event cant be null");
        if (userEvent.getType() == UserEventType.MOVE) {
            handleMoveEvent((MoveEvent) userEvent);
        }
    }

    @Override
    public void fireEvent(MoveEvent event, boolean arrowKey) {
        Objects.requireNonNull(event, "User event cant be null");
        if (arrowKey) {
            moves.put(player, event.getDirection());
        } else {
            moves.put(p, event.getDirection());
        }
    }

    private void handleMoveEvent(MoveEvent event) {
        moves.put(player, event.getDirection());
    }

    @Override
    public void update(@NotNull GameState gameState) {
        if (prevGameState != null) {
            clearPrevGameState(gameState);
        }
        updatePlayersColors(gameState.getActivePlayers());
        gameState.getFruits().forEach(view::drawFruit);
        updateSnakes(gameState);
        view.updateCurrentGameInfo(
                player.getName(),  //FIXME make real master name
                gameState.getGameConfig().getFieldHeight(),
                gameState.getGameConfig().getFieldWidth(),
                gameState.getFruits().size()
        );
        view.showUserListInfo(gameState.getActivePlayers());
        prevGameState = gameState;
    }

    private void updatePlayersColors(List<PlayerWithScore> activePlayers) {
        List<Player> players = activePlayers.stream()
                .map(PlayerWithScore::getPlayer)
                .collect(Collectors.toList());
        removeInactivePlayersFromColorMap(players);
        players.forEach(player -> {
            if (!colorMapper.isPlayerRegistered(player)) {
                colorMapper.addPlayer(player);
            }
        });
    }

    private void removeInactivePlayersFromColorMap(List<Player> players) {
        List<Player> inactiveRegisteredUsers = colorMapper.getRegisteredPlayers().stream()
                .filter(registeredPlayer -> !players.contains(registeredPlayer))
                .collect(Collectors.toList());
        inactiveRegisteredUsers.forEach(colorMapper::removePlayer);
    }

    private void updateSnakes(GameState gameState) {
        gameState.getSnakeInfos().forEach(this::drawSnakeBySnakeInfo);
    }

    private void drawSnakeBySnakeInfo(SnakeInfo snakeInfo) {
        if (snakeInfo.isZombieSnake()) {
            Color zombieSnakeColor = colorMapper.getZombieSnakeColor();
            snakeInfo.getSnakePoints().forEach(point -> view.drawSnakePoint(point, zombieSnakeColor));
            return;
        }
        Color playerColor = colorMapper
                .getColor(
                        snakeInfo.getPlayer().orElseThrow()
                )
                .orElseThrow(() -> new NoSuchElementException("Color map dont contain player"));
        view.drawSnakePoint(snakeInfo.getSnakeHead(), playerColor);
        view.drawSnakePoint(snakeInfo.getSnakeTail(), playerColor);
    }

    private void clearPrevGameState(GameState newGameState) {
        if (prevGameState.getActivePlayers().size() != newGameState.getActivePlayers().size()) {
            clearDeadSnakes(newGameState);
        }
        clearSnakesTails(newGameState);
    }

    private void clearSnakesTails(GameState newGameState) {
        List<Point> newSnakeTails = newGameState.getSnakeInfos().stream()
                .map(SnakeInfo::getSnakeTail).collect(Collectors.toList());
        prevGameState.getSnakeInfos().stream()
                .map(SnakeInfo::getSnakeTail)
                .filter(prevTail -> !newSnakeTails.contains(prevTail)
                )
                .forEach(view::drawEmptyCell);
    }

    private void clearDeadSnakes(GameState newGameState) {
        prevGameState.getSnakeInfos().stream()
                .filter(snakeInfo ->
                        isSnakeDead(snakeInfo, newGameState.getSnakeInfos()))
                .flatMap(snakeInfo ->
                        snakeInfo.getSnakePoints().stream())
                .forEach(view::drawEmptyCell);
    }

    private boolean isSnakeDead(SnakeInfo snake, List<SnakeInfo> snakeInfoList) {
        Point snakeHead = snake.getSnakeHead();
        return snakeInfoList.stream()
                .flatMap(snakeInfo ->
                        snakeInfo.getSnakePoints().stream())
                .noneMatch(point ->
                        point.equals(snakeHead)
                );
    }
}
