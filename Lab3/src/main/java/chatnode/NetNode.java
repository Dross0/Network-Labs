package chatnode;

import org.jetbrains.annotations.NotNull;
import utils.PortValidator;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

public class NetNode implements Serializable {
    private final InetAddress address;
    private final int port;

    public NetNode(@NotNull InetAddress address, int port) {
        this.address = Objects.requireNonNull(address, "Node address cant be null");
        PortValidator.validate(port);
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
        if (o == null || getClass() != o.getClass()) return false;
        NetNode netNode = (NetNode) o;
        return port == netNode.port &&
                address.equals(netNode.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }
}
