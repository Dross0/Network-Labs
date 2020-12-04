package ru.gaidamaka.net.node;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;
import ru.gaidamaka.net.NetNode;

public class ViewerNode implements GameNode {
    @Override
    public void handleMessage(@NotNull NetNode sender, SnakesProto.GameMessage message) {

    }
}
