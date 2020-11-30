package message;

import chatnode.NetNode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TextMessage extends Message {
    private final String senderName;

    public TextMessage(@NotNull String text,
                       @NotNull NetNode receiverNode,
                       @NotNull String senderName) {
        super(text, MessageType.TEXT, receiverNode);
        this.senderName = Objects.requireNonNull(senderName, "Sender name cant be null");
    }

    @NotNull
    public String getSenderName() {
        return senderName;
    }
}
