package message;

import chatnode.NetNode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ConfirmationMessage extends Message {
    private final Message confirmedMessage;

    public ConfirmationMessage(@NotNull NetNode senderNode,
                               @NotNull NetNode receiverNode,
                               @NotNull Message confirmedMessage) {
        super("", MessageType.CONFIRM, senderNode, receiverNode);
        this.confirmedMessage = Objects.requireNonNull(confirmedMessage, "Confirmed message cant be null");
    }

    @NotNull
    public Message getConfirmedMessage() {
        return confirmedMessage;
    }
}
