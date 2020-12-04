package ru.gaidamaka.net.messagehandler;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;
import ru.gaidamaka.net.NetNode;

public interface RoleChangeMessageHandler {
    void handle(@NotNull NetNode sender, @NotNull SnakesProto.GameMessage.RoleChangeMsg roleChangeMsg);
}
