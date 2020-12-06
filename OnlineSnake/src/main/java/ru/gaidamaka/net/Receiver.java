package ru.gaidamaka.net;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gaidamaka.net.messages.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Objects;
import java.util.Optional;

public class Receiver implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
    private static final int PACKET_SIZE = 1024;
    @NotNull
    private final MessageStorage storage;

    @NotNull
    private final DatagramSocket socket;

    public Receiver(@NotNull MessageStorage storage, @NotNull DatagramSocket socket) {
        this.storage = Objects.requireNonNull(storage, "Storage cant be null");
        this.socket = Objects.requireNonNull(socket, "Socket cant be null");
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            DatagramPacket packet = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
            try {
                socket.receive(packet);
                NetNode sender = parseSender(packet);
                parseMessage(packet)
                        .ifPresent(message ->
                                storage.addReceivedMessage(sender, message)
                        );
            } catch (InvalidProtocolBufferException e) {
                logger.error("Cant parse GameMessage from packet", e);
            } catch (IOException e) {
                logger.error("Cant receive packet", e);
            }
        }
    }

    @NotNull
    private Optional<Message> parseMessage(@NotNull DatagramPacket packet) {
        try {
            return Optional.of(SerializationUtils.deserialize(packet.getData()));
        } catch (SerializationException e) {
            logger.error("Cant deserialize message", e);
            return Optional.empty();
        }
    }

    @NotNull
    private NetNode parseSender(@NotNull DatagramPacket packet) {
        return new NetNode(packet.getAddress(), packet.getPort());
    }
}
