package ru.gaidamaka.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.config.GameConfig;
import ru.gaidamaka.game.Direction;
import ru.gaidamaka.game.cell.Point;
import ru.gaidamaka.game.player.PlayerWithScore;
import ru.gaidamaka.presenter.ExitEvent;
import ru.gaidamaka.presenter.GamePresenter;
import ru.gaidamaka.presenter.MoveEvent;
import ru.gaidamaka.presenter.NewGameEvent;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GameWindowController implements View {
    private static final Paint FRUIT_COLOR = Color.GREEN;
    private static final Paint EMPTY_CELL_COLOR = Color.WHITE;
    @FXML
    private TableColumn<PlayerWithScore, String> playerNameColumn;
    @FXML
    private TableColumn<PlayerWithScore, Integer> playerScoreColumn;
    @FXML
    private Label gameOwner;
    @FXML
    private Label foodAmount;
    @FXML
    private Label fieldSize;
    @FXML
    private TableView<PlayerWithScore> playersRankingTable;
    @FXML
    private Button exitButton;
    @FXML
    private Button newGameButton;
    @FXML
    private TableView gameListTable;
    @FXML
    private BorderPane gameFieldPane;

    private final ObservableList<PlayerWithScore> playersObservableList = FXCollections.observableArrayList();

    private Rectangle[][] fieldCells;


    private Stage stage;
    private GameConfig gameConfig;

    private GamePresenter gamePresenter;


    public void setGamePresenter(@NotNull GamePresenter presenter) {
        this.gamePresenter = Objects.requireNonNull(presenter, "Presenter cant be null");
    }

    public void setStage(@NotNull Stage stage) {
        this.stage = Objects.requireNonNull(stage, "Stage cant be null");
        this.stage.addEventHandler(KeyEvent.KEY_RELEASED, getMovementEventHandler());
        this.stage.setOnCloseRequest(event -> close());
        initPlayersInfoTable();
        setActionOnButtons();
    }

    private void setActionOnButtons() {
        exitButton.setOnAction(event -> close());
        newGameButton.setOnAction(event -> gamePresenter.fireEvent(new NewGameEvent()));
    }

    private void close() {
        if (stage == null) {
            throw new IllegalStateException("Cant close not initialized stage");
        }
        stage.close();
        gamePresenter.fireEvent(new ExitEvent());
    }

    private EventHandler<KeyEvent> getMovementEventHandler() {
        return event -> {
            if (gamePresenter == null) {
                throw new IllegalStateException("Cant move with undefined presenter");
            }
            getDirectionByKeyCode(event.getCode())
                    .ifPresent(direction ->
                            gamePresenter.fireEvent(new MoveEvent(direction), event.getCode().isArrowKey())
                    );
        };
    }

    private Optional<Direction> getDirectionByKeyCode(@NotNull KeyCode code) {
        switch (code) {
            case UP:
            case W:
                return Optional.of(Direction.UP);
            case DOWN:
            case S:
                return Optional.of(Direction.DOWN);
            case RIGHT:
            case D:
                return Optional.of(Direction.RIGHT);
            case LEFT:
            case A:
                return Optional.of(Direction.LEFT);
            default:
                return Optional.empty();
        }
    }

    private void builtField() {
        if (gameConfig == null) {
            throw new IllegalStateException("Cant create field without config");
        }
        final int gameFieldHeight = gameConfig.getFieldHeight();
        final int gameFieldWidth = gameConfig.getFieldWidth();
        int rectHeight = (int) (gameFieldPane.getPrefHeight() / gameFieldHeight);
        int rectWidth = (int) (gameFieldPane.getPrefWidth() / gameFieldWidth);
        GridPane gridPane = new GridPane();
        fieldCells = new Rectangle[gameFieldHeight][gameFieldWidth];
        for (int row = 0; row < gameFieldHeight; ++row) {
            for (int col = 0; col < gameFieldWidth; ++col) {
                Rectangle rectangle = new Rectangle(rectWidth, rectHeight, Color.WHITE);
                fieldCells[row][col] = rectangle;
                gridPane.add(rectangle, col, row);
            }
        }
        gridPane.setGridLinesVisible(true);
        gameFieldPane.setCenter(gridPane);
    }

    @Override
    public void drawFruit(@NotNull Point point) {
        paintPoint(point, FRUIT_COLOR);
    }

    @Override
    public void drawEmptyCell(@NotNull Point point) {
        paintPoint(point, EMPTY_CELL_COLOR);
    }

    @Override
    public void drawSnakePoint(@NotNull Point point, @NotNull Color playerSnakeColor) {
        paintPoint(point, playerSnakeColor);
    }

    private void paintPoint(@NotNull Point point, @NotNull Paint color) {
        Platform.runLater(() -> fieldCells[point.getY()][point.getX()].setFill(color));
    }

    @Override
    public void updateCurrentGameInfo(@NotNull String owner, int gameFieldHeight, int gameFieldWidth, int foodNumber) {
        Platform.runLater(() -> {
            foodAmount.setText(String.valueOf(foodNumber));
            fieldSize.setText(gameFieldHeight + "x" + gameFieldWidth);
            gameOwner.setText(owner);
        });
    }

    @Override
    public void showUserListInfo(@NotNull List<PlayerWithScore> playerWithScoreList) {
        playersObservableList.setAll(playerWithScoreList);
    }

    @Override
    public void setConfig(@NotNull GameConfig gameConfig) {
        this.gameConfig = Objects.requireNonNull(gameConfig, "Config cant be null");
        builtField();
    }

    private void initPlayersInfoTable() {
        playersRankingTable.setItems(playersObservableList);
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("player"));
        playerScoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
    }
}
