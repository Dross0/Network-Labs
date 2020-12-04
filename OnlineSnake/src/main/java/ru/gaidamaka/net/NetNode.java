package ru.gaidamaka.net;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

public class NetNode implements Serializable {
    @NotNull
    private final InetAddress address;
    private final int port;

    public NetNode(@NotNull InetAddress address, int port) {
        this.address = Objects.requireNonNull(address, "Node address cant be null");
        if (port <= 0) {
            throw new IllegalArgumentException("Port must be positive");
        }
        this.port = port;
    }

    @NotNull
    InetAddress getAddress() {
        return address;
    }

    int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetNode)) return false;
        NetNode netNode = (NetNode) o;
        return port == netNode.port &&
                address.equals(netNode.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }
}