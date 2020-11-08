import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Message implements Serializable {
    private final ChatNodeConfig senderNode;
    private final String message;
    private final UUID uuid;

    public Message(@NotNull String message,
                   @NotNull ChatNodeConfig senderNode) {
        this.senderNode = Objects.requireNonNull(senderNode, "Sender cant be null");
        this.message = Objects.requireNonNull(message, "Message cant be null");
        this.uuid = UUID.randomUUID();
    }

    @NotNull
    public UUID getUUID() {
        return uuid;
    }

    @NotNull
    public ChatNodeConfig getSenderNode() {
        return senderNode;
    }

    @NotNull
    public String getMessage() {
        return message;
    }
}
