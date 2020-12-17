package ru.gaidamaka.game;

import org.jetbrains.annotations.NotNull;

public enum Direction {
    DOWN,
    UP,
    RIGHT,
    LEFT;

    @NotNull
    public Direction getReversed(){
        switch (this){
            case DOWN:
                return UP;
            case UP:
                return DOWN;
            case RIGHT:
                return LEFT;
            case LEFT:
                return RIGHT;
        }
        return this;
    }
}
