package chatnode;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.time.Instant;


public class Neighbor extends NetNode{
    private Instant lastSeen;

    public Neighbor(@NotNull InetAddress address, int port){
        super(address, port);
        this.lastSeen = Instant.now();
    }

    @NotNull
    public Instant getLastSeen() {
        return lastSeen;
    }

    public void updateLastSeen(){
        lastSeen = Instant.now();
    }
}
