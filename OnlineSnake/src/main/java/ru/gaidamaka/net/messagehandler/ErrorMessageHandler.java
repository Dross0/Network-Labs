package ru.gaidamaka.net.messagehandler;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;
import ru.gaidamaka.net.NetNode;

public interface ErrorMessageHandler {
    void handle(@NotNull NetNode sender, @NotNull SnakesProto.GameMessage.ErrorMsg errorMsg);
}
