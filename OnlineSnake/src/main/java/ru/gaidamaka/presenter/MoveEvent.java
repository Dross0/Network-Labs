package ru.gaidamaka.presenter;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.game.Direction;

import java.util.Objects;


public class MoveEvent extends UserEvent {
    @NotNull
    private final Direction direction;

    public MoveEvent(@NotNull Direction direction) {
        super(UserEventType.MOVE);
        this.direction = Objects.requireNonNull(direction, "Direction cant be null");
    }

    @NotNull
    public Direction getDirection() {
        return direction;
    }
}
