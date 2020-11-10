package message;

import chatnode.NetNode;
import org.jetbrains.annotations.NotNull;

public class ConnectMessage extends Message {
    public ConnectMessage(@NotNull NetNode receiverNode) {
        super("", MessageType.CONNECT, receiverNode);
    }
}
