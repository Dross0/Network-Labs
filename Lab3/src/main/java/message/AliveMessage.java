package message;

import chatnode.NetNode;
import org.jetbrains.annotations.NotNull;

public class AliveMessage extends Message {
    public AliveMessage(@NotNull NetNode receiverNode) {
        super("", MessageType.ALIVE, receiverNode);
    }
}
