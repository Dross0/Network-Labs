package chatnode;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;


public class Neighbor extends NetNode {
    public static final Neighbor nullNeighbor = new Neighbor(InetAddress.getLoopbackAddress(), 0, true);

    private final boolean isNullObject;
    private Instant lastSeen;
    private NetNode replacementNode;

    private Neighbor(@NotNull InetAddress address, int port, boolean isNullObject) {
        super(address, port);
        this.isNullObject = isNullObject;
        this.lastSeen = Instant.now();
        this.replacementNode = null;
    }

    public Neighbor(@NotNull InetAddress address, int port) {
        this(address, port, false);
    }

    public void setReplacementNode(@NotNull NetNode replacementNode) {
        if (isNull()) {
            throw new UnsupportedOperationException("Cant set replacement node of null neighbor");
        }
        this.replacementNode = Objects.requireNonNull(replacementNode, "Replacement node cant be null");
    }

    public boolean isNull() {
        return isNullObject;
    }

    @NotNull
    public Optional<NetNode> getReplacementNode() {
        return Optional.ofNullable(replacementNode);
    }

    public Neighbor(@NotNull NetNode node) {
        this(node.getAddress(), node.getPort());
    }

    @NotNull
    public Instant getLastSeen() {
        return lastSeen;
    }

    public void updateLastSeen() {
        if (isNull()) {
            throw new UnsupportedOperationException("Cant update last seen of null neighbor");
        }
        lastSeen = Instant.now();
    }
}
