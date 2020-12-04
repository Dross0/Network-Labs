package ru.gaidamaka.net.messagehandler;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;
import ru.gaidamaka.net.NetNode;

public interface JoinMessageHandler {
    void handle(@NotNull NetNode sender, @NotNull SnakesProto.GameMessage.JoinMsg joinMsg);
}
