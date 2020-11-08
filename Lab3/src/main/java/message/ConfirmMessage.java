package message;

import chatnode.NetNode;
import org.jetbrains.annotations.NotNull;

public class ConfirmMessage extends Message {
    public ConfirmMessage(@NotNull NetNode senderNode, @NotNull NetNode receiverNode) {
        super("", MessageType.CONFIRM, senderNode, receiverNode);
    }
}
