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

public class MessageReceiver extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    private static final int PACKET_SIZE = 1024;
    private static final int DEFAULT_LOSS_PERCENTAGE = 0;
    private static final int RECEIVE_INTERVAL_MS = 100;

    private final MessageSender sender;
    private final List<Message> confirmedMessages;
    private final List<Neighbor> neighbors;
    private final DatagramSocket socket;
    private final int lossPercentage;
    private NetNode replacementNode;
    private final NetNode nodeInfo;

    public MessageReceiver(@NotNull DatagramSocket socket,
                           @NotNull MessageSender sender,
                           @NotNull List<Message> confirmedMessages,
                           @NotNull List<Neighbor> neighbors,
                           @NotNull NetNode replacementNode,
                           int lossPercentage) {
        this.socket = Objects.requireNonNull(socket, "Socket cant be null");
        this.sender = Objects.requireNonNull(sender, "Sender cant be null");
        this.confirmedMessages = Objects.requireNonNull(confirmedMessages, "Confirmed message list cant be null");
        this.neighbors = Objects.requireNonNull(neighbors, "Neighbors list cant be null");
        this.replacementNode = Objects.requireNonNull(replacementNode, "Replacement node cant be null");
        this.lossPercentage = processInitLossPercentage(lossPercentage);
        this.nodeInfo = new NetNode(socket.getLocalAddress(), socket.getLocalPort());
    }

    public void setReplacementNode(@NotNull NetNode newReplacementNode){
        synchronized (neighbors) {
            replacementNode = newReplacementNode;
            neighbors.forEach(this::sendReplacementNodeShareMessage);
        }
    }

    private int processInitLossPercentage(int lossPercentageToCheck){
        if (LossPercentageValidator.isValid(lossPercentageToCheck)){
            logger.warn("Loss percentage is not valid, actual = "
                    + lossPercentageToCheck + ", loss percentage now has default value = " + DEFAULT_LOSS_PERCENTAGE);
            return DEFAULT_LOSS_PERCENTAGE;
        }
        return lossPercentageToCheck;
    }

    @Override
    public void run() {
        Random randomLossPercentage = new Random();
        while (!isInterrupted()){
            DatagramPacket packet = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
            try {
                socket.receive(packet);
                if (lossPercentage > randomLossPercentage.nextInt(100)){
                    logger.debug("Packet lost");
                    continue;
                }
                Message message = SerializationUtils.deserialize(packet.getData());
                switch (message.getMessageType()){
                    case TEXT:
                        TextMessage textMessage = (TextMessage) message;
                        printTextMessage(textMessage);
                        sendConfirmationMessage(textMessage);
                        resendTextMessageToNeighbors(textMessage);
                        break;
                    case ALIVE:
                        updateAliveNeighborLastSeen(message.getSenderNode());
                        break;
                    case CONFIRM:
                        ConfirmationMessage confirmationMessage = (ConfirmationMessage) message;
                        addToConfirmedMessage(confirmationMessage.getConfirmedMessage());
                        break;
                    case CONNECT:
                        addNeighbor(message.getSenderNode());
                        sendReplacementNodeShareMessage(message.getSenderNode());
                        sendConfirmationMessage(message);
                        break;
                    case REPLACEMENT_NODE_SHARE:
                        ReplacementNodeShareMessage shareMessage = (ReplacementNodeShareMessage) message;
                        updateNeighborReplacementNode(shareMessage.getSenderNode(), shareMessage.getReplacementNode());
                        sendConfirmationMessage(shareMessage);
                        break;
                }
                sleep(RECEIVE_INTERVAL_MS);
            } catch (IOException e) {
                logger.error("Cant receive message", e);
                interrupt();
            } catch (InterruptedException e) {
                logger.error("Sleep interrupted", e);
                break;
            }
        }
    }

    private void sendReplacementNodeShareMessage(NetNode receiver){
        sender.sendMessage(new ReplacementNodeShareMessage(
                nodeInfo,
                receiver,
                replacementNode
        ));
    }

    private void sendConfirmationMessage(Message message){
        sender.sendMessage(new ConfirmationMessage(
                nodeInfo,
                message.getSenderNode(),
                message
        ));
    }

    private void updateNeighborReplacementNode(NetNode neighborNode, NetNode replacementNode){
        synchronized (neighbors) {
            for (Neighbor neighbor : neighbors) {
                if (neighbor.equals(neighborNode)) {
                    neighbor.setReplacementNode(replacementNode);
                    return;
                }
            }
        }
        logger.warn("No such neighbor, cant update replacement node");
    }

    private void addNeighbor(NetNode senderNode) {
        Neighbor neighbor = new Neighbor(senderNode);
        if (neighbors.contains(neighbor)){
            logger.warn("This neighbor already added");
            return;
        }
        neighbors.add(neighbor);
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

    private void resendTextMessageToNeighbors(TextMessage textMessage) {
        synchronized (neighbors){
            neighbors.forEach(neighbor -> {
                if (!neighbor.equals(textMessage.getSenderNode())){
                    sender.sendMessage(new TextMessage(
                            textMessage.getText(),
                            nodeInfo,
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

    private void updateAliveNeighborLastSeen(NetNode node){
        synchronized (neighbors) {
            for (Neighbor neighbor : neighbors) {
                if (neighbor.equals(node)) {
                    neighbor.updateLastSeen();
                    return;
                }
            }
        }
        logger.warn("From neighbor = {" + node.getAddress() + ": " + node.getPort() + "} was receive message, " +
                    "but he was already removed from neighbors list");
    }
}
