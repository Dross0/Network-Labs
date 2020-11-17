package ru.gaidamaka.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import ru.gaidamaka.config.GameConfig;
import ru.gaidamaka.game.cell.Point;
import ru.gaidamaka.game.player.PlayerWithScore;
import ru.gaidamaka.game.snake.SnakeInfo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameState {
    @Unmodifiable
    @NotNull
    private final List<Point> fruits;

    @Unmodifiable
    @NotNull
    private final List<PlayerWithScore> activePlayers;

    @Unmodifiable
    @NotNull
    private final List<SnakeInfo> snakeInfos;

    @NotNull
    private final GameConfig gameConfig;

    private final int stateID;


    public GameState(@NotNull List<Point> fruits,
                     @NotNull List<PlayerWithScore> activePlayers,
                     @NotNull List<SnakeInfo> snakeInfos,
                     @NotNull GameConfig gameConfig,
                     int stateID) {
        this.fruits = Collections.unmodifiableList(
                Objects.requireNonNull(fruits, "Fruits list cant be null")
        );
        this.activePlayers = Collections.unmodifiableList(
                Objects.requireNonNull(activePlayers, "Active players list cant be null")
        );
        this.snakeInfos = Collections.unmodifiableList(
                Objects.requireNonNull(snakeInfos, "Snake infos list cant be null")
        );
        this.gameConfig = Objects.requireNonNull(gameConfig, "Game config cant be null");
        this.stateID = stateID;
    }

    public int getStateID() {
        return stateID;
    }

    @Unmodifiable
    @NotNull
    public List<Point> getFruits() {
        return fruits;
    }

    @Unmodifiable
    @NotNull
    public List<PlayerWithScore> getActivePlayers() {
        return activePlayers;
    }

    @Unmodifiable
    @NotNull
    public List<SnakeInfo> getSnakeInfos() {
        return snakeInfos;
    }

    @NotNull
    public GameConfig getGameConfig() {
        return gameConfig;
    }
}
