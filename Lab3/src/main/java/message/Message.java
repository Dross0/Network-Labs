package message;

import chatnode.NetNode;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class Message implements Serializable {
    private final String text;
    private final UUID uuid;
    private final MessageType messageType;
    private final NetNode receiverNode;

    public Message(@NotNull String text,
                   @NotNull MessageType messageType,
                   @NotNull NetNode receiverNode) {
        this.text = Objects.requireNonNull(text, "Message cant be null");
        this.messageType = Objects.requireNonNull(messageType, "Message type cant be null");
        this.receiverNode = Objects.requireNonNull(receiverNode, "Receiver cant be null");
        this.uuid = UUID.randomUUID();
    }

    @NotNull
    public UUID getUUID() {
        return uuid;
    }

    @NotNull
    public String getText() {
        return text;
    }

    @NotNull
    public MessageType getMessageType() {
        return messageType;
    }

    @NotNull
    public NetNode getReceiverNode() {
        return receiverNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return uuid.equals(message.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @NotNull
    public String string() {
        return "Message{" +
                "text='" + text + '\'' +
                ", messageType=" + messageType +
                '}';
    }

    @Override
    public String toString() {
        return string();
    }
}
