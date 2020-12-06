package ru.gaidamaka.net.messages;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.game.GameState;

import java.util.Objects;

public class StateMessage extends Message {
    @NotNull
    private final GameState gameState;

    public StateMessage(@NotNull GameState gameState) {
        super(MessageType.STATE);
        this.gameState = Objects.requireNonNull(gameState, "Game state cant be null");
    }

    @NotNull
    public GameState getGameState() {
        return gameState;
    }
}
