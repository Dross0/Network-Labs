package ru.gaidamaka.net.node;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.config.Config;

import java.util.Objects;

public final class NodeFactory {
    private NodeFactory() {
    }

    @NotNull
    public static GameNode createNode(@NotNull Role role, @NotNull Config config) {
        Objects.requireNonNull(role, "Role cant be null");
        Objects.requireNonNull(config, "Config cant be null");
        switch (role) {
            case MASTER:
                return new MasterNode(config);
            case DEPUTY:
                return new DeputyNode(config);
            case NORMAL:
                return new NormalNode(config);
            default:
                throw new IllegalArgumentException("Unknown role");
        }
    }
}
