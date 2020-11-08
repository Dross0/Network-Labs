package message;

import chatnode.ChatNodeConfig;
import chatnode.NetNode;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class Message implements Serializable {
    private final NetNode senderNode;
    private final String message;
    private final UUID uuid;
    private final MessageType messageType;
    private final NetNode receiverNode;

    public Message(@NotNull String message,
                   @NotNull MessageType messageType,
                   @NotNull NetNode senderNode,
                   @NotNull NetNode receiverNode) {
        this.senderNode = Objects.requireNonNull(senderNode, "Sender cant be null");
        this.message = Objects.requireNonNull(message, "Message cant be null");
        this.messageType = Objects.requireNonNull(messageType, "Message type cant be null");
        this.receiverNode = Objects.requireNonNull(receiverNode, "Receiver cant be null");
        this.uuid = UUID.randomUUID();
    }

    @NotNull
    public UUID getUUID() {
        return uuid;
    }

    @NotNull
    public NetNode getSenderNode() {
        return senderNode;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @NotNull
    public MessageType getMessageType() {
        return messageType;
    }

    @NotNull
    public NetNode getReceiverNode() {
        return receiverNode;
    }
}
