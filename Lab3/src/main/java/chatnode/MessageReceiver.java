package chatnode;


import message.ConfirmationMessage;
import message.Message;
import message.ReplacementNodeShareMessage;
import message.TextMessage;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.LossPercentageValidator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MessageReceiver implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    private static final int PACKET_SIZE = 4096;
    private static final int DEFAULT_LOSS_PERCENTAGE = 0;
    private static final int RECEIVE_INTERVAL_MS = 100;

    private final MessageSender sender;
    private final List<Message> confirmedMessages;
    private final List<Neighbor> neighbors;
    private final DatagramSocket socket;
    private final int lossPercentage;
    private final Random randomLossPercentage;
    private NetNode replacementNode = Neighbor.nullNeighbor;

    public MessageReceiver(@NotNull DatagramSocket socket,
                           @NotNull MessageSender sender,
                           @NotNull List<Message> confirmedMessages,
                           @NotNull List<Neighbor> neighbors,
                           int lossPercentage) {
        this.socket = Objects.requireNonNull(socket, "Socket cant be null");
        this.sender = Objects.requireNonNull(sender, "Sender cant be null");
        this.confirmedMessages = Objects.requireNonNull(confirmedMessages, "Confirmed message list cant be null");
        this.neighbors = Objects.requireNonNull(neighbors, "Neighbors list cant be null");
        this.lossPercentage = processInitLossPercentage(lossPercentage);
        this.randomLossPercentage = new Random();
    }

    public void setReplacementNode(@NotNull NetNode newReplacementNode) {
        synchronized (neighbors) {
            replacementNode = newReplacementNode;
            neighbors.forEach(this::sendReplacementNodeShareMessage);
        }
    }

    private int processInitLossPercentage(int lossPercentageToCheck) {
        if (LossPercentageValidator.isValid(lossPercentageToCheck)) {
            return lossPercentageToCheck;
        }
        logger.warn("Loss percentage is not valid, actual = {}, loss percentage now has default value = {}",
                lossPercentageToCheck,
                DEFAULT_LOSS_PERCENTAGE
        );
        return DEFAULT_LOSS_PERCENTAGE;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            DatagramPacket packet = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
            try {
                socket.receive(packet);
                if (lossPercentage > randomLossPercentage.nextInt(100)) {
                    logger.debug("Packet lost");
                    continue;
                }
                Message message = SerializationUtils.deserialize(packet.getData());
                NetNode packageSenderNode = new NetNode(packet.getAddress(), packet.getPort());
                switch (message.getMessageType()) {
                    case TEXT:
                        TextMessage textMessage = (TextMessage) message;
                        printTextMessage(textMessage);
                        sendConfirmationMessage(packageSenderNode, textMessage);
                        resendTextMessageToNeighbors(packageSenderNode, textMessage);
                        break;
                    case ALIVE:
                        checkAndAddNeighbor(message, packageSenderNode);
                        updateAliveNeighborLastSeen(packageSenderNode);
                        break;
                    case CONFIRM:
                        ConfirmationMessage confirmationMessage = (ConfirmationMessage) message;
                        addToConfirmedMessage(confirmationMessage.getConfirmedMessage());
                        break;
                    case REPLACEMENT_NODE_SHARE:
                        ReplacementNodeShareMessage shareMessage = (ReplacementNodeShareMessage) message;
                        updateNeighborReplacementNode(packageSenderNode, shareMessage.getReplacementNode());
                        sendConfirmationMessage(packageSenderNode, shareMessage);
                        break;
                    default:
                        logger.error("Invalid message type = {}", message.getMessageType());
                        throw new IllegalStateException("Cant work with this message type = " + message.getMessageType());
                }
                Thread.sleep(RECEIVE_INTERVAL_MS);
            } catch (InterruptedException e) {
                logger.debug("Sleep interrupted", e);
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                logger.debug("Cant receive packet from socket", e);
                return;
            }
        }
    }

    private boolean checkAndAddNeighbor(Message message, NetNode packageSenderNode) {
        synchronized (neighbors) {
            if (!neighbors.contains(packageSenderNode)) {
                if (message.getReceiverNode().equals(packageSenderNode)) {
                    return true;
                }
                addNeighbor(packageSenderNode);
                sendReplacementNodeShareMessage(packageSenderNode);
                sendConfirmationMessage(packageSenderNode, message);
            }
        }
        return false;
    }

    private void sendReplacementNodeShareMessage(NetNode receiver) {
        sender.sendMessage(new ReplacementNodeShareMessage(
                receiver,
                replacementNode
        ));
    }

    private void sendConfirmationMessage(NetNode receiverNode, Message message) {
        sender.sendMessage(new ConfirmationMessage(
                receiverNode,
                message
        ));
    }

    private void updateNeighborReplacementNode(NetNode neighborNode, NetNode replacementNode) {
        synchronized (neighbors) {
            for (Neighbor neighbor : neighbors) {
                if (neighbor.equals(neighborNode)) {
                    neighbor.setReplacementNode(replacementNode);
                    return;
                }
            }
        }
    }

    private void addNeighbor(NetNode senderNode) {
        synchronized (neighbors) {
            Neighbor neighbor = new Neighbor(senderNode);
            if (neighbors.contains(neighbor)) {
                logger.warn("This neighbor already added");
                return;
            }
            neighbors.add(neighbor);
        }
    }

    private void addToConfirmedMessage(Message confirmedMessage){
        if (!confirmedMessage.getMessageType().isNeedsConfirmation()){
            return;
        }
        synchronized (confirmedMessages) {
            if (confirmedMessages.contains(confirmedMessage)) {
                logger.warn("Re-receiving confirmation of the message");
                return;
            }
            confirmedMessages.add(confirmedMessage);
        }
    }

    private void resendTextMessageToNeighbors(NetNode senderNode, TextMessage textMessage) {
        synchronized (neighbors){
            neighbors.forEach(neighbor -> {
                if (!neighbor.equals(senderNode)){
                    sender.sendMessage(new TextMessage(
                            textMessage.getText(),
                            neighbor,
                            textMessage.getSenderName())
                    );
                }
            });
        }
    }

    private void printTextMessage(TextMessage message){
        System.out.println(message.getSenderName() + ": " + message.getText());
    }

    private void updateAliveNeighborLastSeen(NetNode node) {
        synchronized (neighbors) {
            for (Neighbor neighbor : neighbors) {
                if (node.equals(neighbor)) {
                    neighbor.updateLastSeen();
                    return;
                }
            }
        }
        logger.warn("From neighbor = {}:{} was receive message, " +
                "but he was already removed from neighbors list", node.getAddress(), node.getPort());
    }
}
