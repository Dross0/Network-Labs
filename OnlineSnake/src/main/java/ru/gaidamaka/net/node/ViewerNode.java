package ru.gaidamaka.net.node;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.config.Config;
import ru.gaidamaka.net.NetNode;
import ru.gaidamaka.net.NodeHandler;
import ru.gaidamaka.net.messages.Message;

public class ViewerNode implements GameNode {
    public ViewerNode(@NotNull Config config) {
    }

    @Override
    public void handleMessage(@NotNull NetNode sender, @NotNull Message message) {

    }

    @Override
    public void setNodeHandler(@NotNull NodeHandler nodeHandler) {

    }
}
