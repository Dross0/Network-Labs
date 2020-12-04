package ru.gaidamaka.net;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gaidamaka.SnakesProto;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;
import java.util.Objects;

public class Sender implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    private static final int SEND_PERIOD_MS = 100;
    @NotNull
    private final MessageStorage storage;

    @NotNull
    private final DatagramSocket socket;

    public Sender(@NotNull MessageStorage storage, @NotNull DatagramSocket socket) {
        this.storage = Objects.requireNonNull(storage, "Storage cant be null");
        this.socket = Objects.requireNonNull(socket, "Socket cant be null");
    }


    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            storage.resendUnconfirmedMessages();
            sendMessages();
            try {
                Thread.sleep(SEND_PERIOD_MS);
            } catch (InterruptedException e) {
                logger.error("Sender was interrupted while sleep", e);
                return;
            }
        }
    }

    public void addMessageToSend(@NotNull NetNode receiver, @NotNull SnakesProto.GameMessage message) {
        storage.addMessageToSend(receiver, message);
    }

    private void sendMessages() {
        Map<SnakesProto.GameMessage, NetNode> messagesToSend = storage.getMessagesToSend();
        messagesToSend.forEach((gameMessage, netNode) -> sendMessage(netNode, gameMessage));
    }

    private void sendMessage(@NotNull NetNode receiver, @NotNull SnakesProto.GameMessage message) {
        byte[] messageBytes = message.toByteArray();
        DatagramPacket packet = new DatagramPacket(
                messageBytes,
                messageBytes.length,
                receiver.getAddress(),
                receiver.getPort()
        );
        try {
            socket.send(packet);
        } catch (IOException e) {
            logger.error("Cant send message={} to={}", message, receiver, e);
        }
    }
}
