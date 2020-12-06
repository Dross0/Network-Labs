package ru.gaidamaka.net.messages;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class JoinMessage extends Message {
    @NotNull
    private final String playerName;

    public JoinMessage(@NotNull String playerName) {
        super(MessageType.JOIN);
        this.playerName = Objects.requireNonNull(playerName, "Player name cant be null");
    }

    @NotNull
    public String getPlayerName() {
        return playerName;
    }
}
