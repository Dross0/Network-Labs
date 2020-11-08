package message;

import chatnode.NetNode;
import org.jetbrains.annotations.NotNull;

public class TextMessage extends Message {
    public TextMessage(@NotNull String message,
                       @NotNull NetNode senderNode,
                       @NotNull NetNode receiverNode) {
        super(message, MessageType.TEXT, senderNode, receiverNode);
    }
}
