package chatnode;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;


public class Neighbor extends NetNode{
    private Instant lastSeen;
    private NetNode replacementNode;

    public Neighbor(@NotNull InetAddress address, int port){
        super(address, port);
        this.lastSeen = Instant.now();
        this.replacementNode = null;
    }

    public void setReplacementNode(@NotNull NetNode replacementNode){
        this.replacementNode = Objects.requireNonNull(replacementNode, "Replacement node cant be null");
    }

    @NotNull
    public Optional<NetNode> getReplacementNode(){
        return Optional.ofNullable(replacementNode);
    }

    public Neighbor(@NotNull NetNode node){
        this(node.getAddress(), node.getPort());
    }

    @NotNull
    public Instant getLastSeen() {
        return lastSeen;
    }

    public void updateLastSeen(){
        lastSeen = Instant.now();
    }
}
