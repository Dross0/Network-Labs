package ru.gaidamaka.net.messagehandler;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;
import ru.gaidamaka.net.NetNode;

public interface AckMessageHandler {
    void handle(@NotNull NetNode sender, @NotNull SnakesProto.GameMessage.AckMsg ackMsg);
}
