package ru.gaidamaka.net.messagehandler;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.net.NetNode;

public interface PingMessageHandler {
    void handle(@NotNull NetNode sender, @NotNull PingMessageHandler pingMessageHandler);
}
