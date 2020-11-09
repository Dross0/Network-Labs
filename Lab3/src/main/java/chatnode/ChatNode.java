package chatnode;

import message.AliveMessage;
import message.ConnectMessage;
import message.Message;
import message.TextMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DurationUtils;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.Instant;
import java.util.*;

public class ChatNode implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(ChatNode.class);

    private static final long ALIVE_MESSAGES_SEND_INTERVAL_MS = 1000;
    private static final long NEIGHBOR_CLEAN_INTERVAL_MS = 2000;
    private static final int NEIGHBOR_ALIVE_TIME_S = 3;

    private final ChatNodeConfig initConfig;
    private final List<Message> confirmedMessages;
    private final List<Neighbor> neighbors;
    private final InputStream inputStream;
    private MessageSender messageSender;
    private MessageReceiver messageReceiver;
    private Neighbor replacementNode;
    private Timer aliveMessagesSendTimer;
    private Timer neighborsCleaner;
    private Thread textMessagesInput;
    private DatagramSocket socket;

    public ChatNode(@NotNull ChatNodeConfig nodeConfig, @NotNull InputStream inputStream) {
        this.initConfig = Objects.requireNonNull(nodeConfig, "Config cant be null");
        this.inputStream = Objects.requireNonNull(inputStream, "Input stream cant be null");
        this.confirmedMessages = new ArrayList<>();
        this.neighbors = new ArrayList<>();
        this.replacementNode = Neighbor.nullNeighbor;
        if (initConfig.hasNeighborInfo()) {
            Neighbor neighbor = new Neighbor(initConfig.getNeighborAddress().get(), initConfig.getNeighborPort());
            addNeighbor(neighbor);
        }
    }

    private void addNeighbor(@NotNull Neighbor neighbor) {
        synchronized (neighbors) {
            neighbors.add(Objects.requireNonNull(neighbor, "Neighbor cant be null"));
        }
    }

    private void removeNeighbor(@NotNull Neighbor neighbor) {
        synchronized (neighbors) {
            neighbors.remove(Objects.requireNonNull(neighbor, "Neighbor cant be null"));
        }
    }

    public void start() {
        try {
            socket = new DatagramSocket(initConfig.getPort());
            messageSender = new MessageSender(socket, confirmedMessages, neighbors);
            replacementNode = chooseReplacementNode();
            messageReceiver = new MessageReceiver(
                    socket,
                    messageSender,
                    confirmedMessages,
                    neighbors,
                    initConfig.getLossPercentage()
            );
            messageSender.start();
            messageReceiver.start();
            synchronized (neighbors) {
                neighbors.forEach(neighbor -> messageSender.sendMessage(new ConnectMessage(neighbor)));
            }
            startNeighborsCleaner();
            startSendingAliveMessages();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            startSendingTextMessages(bufferedReader);
        } catch (SocketException e) {
            logger.error("Chat node with name={" + initConfig.getName() + "} cant open socket", e);
        }
    }

    private Neighbor chooseReplacementNode(){
        synchronized (neighbors){
            if (neighbors.isEmpty()){
                return Neighbor.nullNeighbor;
            }
            return neighbors.get(0);
        }
    }

    private void setReplacementNode(Neighbor replacementNode) {
        this.replacementNode = replacementNode;
        if (messageReceiver != null) {
            messageReceiver.setReplacementNode(replacementNode);
        }
    }

    public void stop() {
        if (socket != null) {
            socket.close();
        }
        stopThread(messageSender);
        stopThread(messageReceiver);
        stopThread(textMessagesInput);
        stopTimer(aliveMessagesSendTimer);
        stopTimer(neighborsCleaner);
    }

    private void stopThread(Thread thread) {
        if (thread != null) {
            thread.interrupt();
        }
    }

    private void stopTimer(Timer timer) {
        if (timer != null) {
            timer.cancel();
        }
    }

    private boolean neighborIsDead(Neighbor neighbor) {
        Instant lastSeenTime = neighbor.getLastSeen();
        return DurationUtils.secondsBetweenTwoInstants(lastSeenTime, Instant.now()) >= NEIGHBOR_ALIVE_TIME_S;
    }

    private void startNeighborsCleaner() {
        neighborsCleaner = new Timer();
        TimerTask cleanTask = new TimerTask() {
            @Override
            public void run() {
                synchronized (neighbors) {
                    neighbors.forEach(neighbor -> {
                        if (neighborIsDead(neighbor)) {
                            neighbor.getReplacementNode().ifPresent(neighborReplacementNode -> {
                                        if (!neighbor.equals(neighborReplacementNode)) {
                                            messageSender.sendMessage(new ConnectMessage(neighborReplacementNode));
                                        }
                                    }
                            );
                        }
                    });
                    neighbors.removeIf(ChatNode.this::neighborIsDead);

                }
                if (replacementNode.isNull() || neighborIsDead(replacementNode)){
                    setReplacementNode(chooseReplacementNode());
                }
            }
        };
        neighborsCleaner.schedule(cleanTask, 0, NEIGHBOR_CLEAN_INTERVAL_MS);
    }

    private void startSendingTextMessages(BufferedReader bufferedReader) {
        textMessagesInput = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String messageText = bufferedReader.readLine();
                    synchronized (neighbors) {
                        neighbors.forEach(neighbor -> messageSender.sendMessage(new TextMessage(
                                messageText,
                                neighbor,
                                initConfig.getName()
                        )));
                    }
                } catch (IOException e) {
                    logger.error("Cant read new line", e);
                    Thread.currentThread().interrupt();
                }
            }
        });
        textMessagesInput.start();
    }

    private void startSendingAliveMessages() {
        aliveMessagesSendTimer = new Timer();
        TimerTask aliveMessagesSendTask = new TimerTask() {
            @Override
            public void run() {
                synchronized (neighbors) {
                    neighbors.forEach(neighbor -> {
                        Message aliveMessage = new AliveMessage(neighbor);
                        messageSender.sendMessage(aliveMessage);
                    });
                }
            }
        };
        aliveMessagesSendTimer.schedule(aliveMessagesSendTask, 0, ALIVE_MESSAGES_SEND_INTERVAL_MS);
    }

    @Override
    public void close() throws IOException {
        stop();
        inputStream.close();
    }
}
