package ru.gaidamaka.net.node;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;
import ru.gaidamaka.net.NetNode;

public interface GameNode {
    void handleMessage(@NotNull NetNode sender, @NotNull SnakesProto.GameMessage message);
}
