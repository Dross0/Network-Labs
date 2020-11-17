package ru.gaidamaka.game.snake;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gaidamaka.game.Direction;
import ru.gaidamaka.game.cell.Point;
import ru.gaidamaka.game.player.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SnakeInfo {
    @NotNull
    private final List<Point> snakePoints;

    @NotNull
    private final Direction direction;

    @Nullable
    private Player player;

    public SnakeInfo(@NotNull Snake snake) {
        player = null;
        snakePoints = Collections.unmodifiableList(snake.getSnakePoints());
        direction = snake.getCurrentDirection();
    }

    public void setPlayer(@NotNull Player player) {
        this.player = Objects.requireNonNull(player, "Player cant be null");
    }

    public @NotNull List<Point> getSnakePoints() {
        return snakePoints;
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(player);
    }

    public boolean isAliveSnake() {
        return player != null;
    }

    public boolean isZombieSnake() {
        return !isAliveSnake();
    }

    public @NotNull Direction getDirection() {
        return direction;
    }
}
