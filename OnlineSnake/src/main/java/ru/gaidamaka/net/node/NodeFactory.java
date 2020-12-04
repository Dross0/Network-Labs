package ru.gaidamaka.net.node;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;

import java.util.Objects;

public final class NodeFactory {
    private NodeFactory() {
    }

    @NotNull
    public static GameNode createNode(@NotNull SnakesProto.NodeRole role) {
        Objects.requireNonNull(role);
        switch (role) {
            case MASTER:
                return new MasterNode(null);
            case DEPUTY:
                return new DeputyNode();
            case NORMAL:
                return new NormalNode();
            case VIEWER:
                return new ViewerNode();
            default:
                throw new IllegalArgumentException("Unknown role");
        }
    }
}
