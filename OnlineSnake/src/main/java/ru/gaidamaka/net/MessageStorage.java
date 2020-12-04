package ru.gaidamaka.net;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MessageStorage {
    private final Map<SnakesProto.GameMessage, NetNode> receivedMessages = new ConcurrentHashMap<>();
    private final Map<SnakesProto.GameMessage, NetNode> sentMessages = new ConcurrentHashMap<>();
    private final Map<SnakesProto.GameMessage, NetNode> messagesToSend = new ConcurrentHashMap<>();

    public void removeConfirmedMessages() {
        sentMessages.keySet().removeIf(this::isConfirmedMessage);
        removeAckReceivedMessages();
    }

    public void removeAllReceivedMessages() {
        receivedMessages.clear();
    }

    public void addMessageToSend(@NotNull NetNode receiver, @NotNull SnakesProto.GameMessage gameMessage) {
        messagesToSend.put(
                Objects.requireNonNull(gameMessage),
                Objects.requireNonNull(receiver)
        );
    }

    public void addReceivedMessage(@NotNull NetNode sender, @NotNull SnakesProto.GameMessage gameMessage) {
        receivedMessages.put(
                Objects.requireNonNull(gameMessage),
                Objects.requireNonNull(sender)
        );
    }

    @NotNull
    public Map<SnakesProto.GameMessage, NetNode> getMessagesToSend() {
        Map<SnakesProto.GameMessage, NetNode> messages = Map.copyOf(messagesToSend);
        messagesToSend.clear();
        return messages;
    }

    public void resendUnconfirmedMessages() {
        messagesToSend.putAll(sentMessages);
        sentMessages.clear();
    }

    private void removeAckReceivedMessages() {
        receivedMessages.keySet().removeIf(SnakesProto.GameMessage::hasAck);//TODO check correct
    }

    private boolean isConfirmedMessage(SnakesProto.GameMessage message) {
        return receivedMessages.keySet().stream()
                .anyMatch(receivedMessage ->
                        message.getMsgSeq() == receivedMessage.getMsgSeq()
                );
    }
}
