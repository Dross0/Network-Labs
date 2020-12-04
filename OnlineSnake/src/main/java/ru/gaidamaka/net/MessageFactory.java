package ru.gaidamaka.net;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;

import java.util.Objects;

public final class MessageFactory {
    private MessageFactory() {
    }

    public static SnakesProto.GameMessage.JoinMsg generateJoinMessage(@NotNull String playerName,
                                                                      @NotNull SnakesProto.PlayerType playerType,
                                                                      boolean isOnlyView) {
        return SnakesProto.GameMessage.JoinMsg.newBuilder()
                .setName(Objects.requireNonNull(playerName))
                .setOnlyView(isOnlyView)
                .setPlayerType(Objects.requireNonNull(playerType))
                .build();
    }
}
