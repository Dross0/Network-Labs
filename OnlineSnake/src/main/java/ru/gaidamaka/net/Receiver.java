package ru.gaidamaka.net;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gaidamaka.SnakesProto;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Objects;

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
                SnakesProto.GameMessage gameMessage = parseMessage(packet);
                storage.addReceivedMessage(sender, gameMessage);
            } catch (InvalidProtocolBufferException e) {
                logger.error("Cant parse GameMessage from packet", e);
            } catch (IOException e) {
                logger.error("Cant receive packet", e);
            }
        }
    }

    private SnakesProto.GameMessage parseMessage(DatagramPacket packet) throws InvalidProtocolBufferException {
        return SnakesProto.GameMessage.parseFrom(packet.getData());
    }

    private NetNode parseSender(DatagramPacket packet) {
        return new NetNode(packet.getAddress(), packet.getPort());
    }
}
