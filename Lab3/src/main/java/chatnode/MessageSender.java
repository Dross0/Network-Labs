package chatnode;


import message.Message;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DurationUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.Instant;
import java.util.*;

public class MessageSender extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);

    private static final int SEND_INTERVAL_MS = 100;
    private static final int RESEND_UNCONFIRMED_MESSAGES_INTERVAL_MS = 300;

    private final List<Message> confirmedMessages;
    private final Map<Message, Instant> sentMessages;
    private final List<Message> messagesToSend;
    private final List<Neighbor> neighbors;
    private final DatagramSocket socket;


    public MessageSender(@NotNull DatagramSocket socket,
                         @NotNull List<Message>  confirmedMessages,
                         @NotNull List<Neighbor> neighbors) {
        this.confirmedMessages = Objects.requireNonNull(confirmedMessages, "Confirmed messages list cant be null");
        this.neighbors = Objects.requireNonNull(neighbors, "Neighbors list cant be null");
        this.socket = Objects.requireNonNull(socket, "Socket cant be null");
        this.messagesToSend = new ArrayList<>();
        this.sentMessages = new HashMap<>();
    }

    @Override
    public void run() {
        while (!isInterrupted()){
            sendAllNewMessages();
            resendUnconfirmedMessages();
            try {
                sleep(SEND_INTERVAL_MS);
            } catch (InterruptedException e) {
                logger.debug("Sleep interrupt", e);
                break;
            }
        }
    }

    public void sendMessage(@NotNull Message message){
        Objects.requireNonNull(message, "Message cant be null");
        synchronized (messagesToSend){
            messagesToSend.add(message);
        }
    }

    private void sendAllNewMessages(){
        synchronized (messagesToSend){
            messagesToSend.forEach(this::sendMessageToNode);
            messagesToSend.removeIf(message -> !message.getMessageType().isNeedsConfirmation());
            addNewSentMessages(messagesToSend);
            messagesToSend.clear();
        }
    }

    private void addNewSentMessages(List<Message> newSentMessages) {
        synchronized (sentMessages) {
            Instant now = Instant.now();
            for (Message message : newSentMessages) {
                sentMessages.put(message, now);
            }
        }
    }

    private void resendUnconfirmedMessages(){
        synchronized (sentMessages){
            sentMessages.keySet().removeIf(this::checkDeliveryConfirmation);
            sentMessages.keySet().removeIf(this::isMessageToNotNeighbor);
            Instant now = Instant.now();
            sentMessages.forEach((message, instant) -> {
                if (DurationUtils.milliSecondsBetweenTwoInstants(instant, now) >= RESEND_UNCONFIRMED_MESSAGES_INTERVAL_MS){
                    sendMessageToNode(message);
                }
            });
        }
    }

    private void sendMessageToNode(Message message) {
        byte[] data = SerializationUtils.serialize(message);
        NetNode receiver = message.getReceiverNode();
        try {
            socket.send(new DatagramPacket(
                    data,
                    data.length,
                    receiver.getAddress(),
                    receiver.getPort())
            );
        } catch (IOException e) {
            logger.warn("Cant send message to neighbor {"
                    + receiver.getAddress() + ":"
                    + receiver.getPort() + "}", e);
        }
    }

    private boolean isMessageToNotNeighbor(Message message) {
        synchronized (neighbors) {
            for (Neighbor neighbor : neighbors) {
                if (neighbor.equals(message.getReceiverNode())) {
                    return false;
                }
            }
            return true;
        }
    }

    private boolean checkDeliveryConfirmation(Message message){
        synchronized (confirmedMessages){
            boolean isConfirmedMessage = confirmedMessages.contains(message);
            if (isConfirmedMessage){
                confirmedMessages.remove(message);
            }
            return isConfirmedMessage;
        }
    }
}
