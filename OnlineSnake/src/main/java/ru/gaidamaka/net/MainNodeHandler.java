package ru.gaidamaka.net;


import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gaidamaka.config.Config;
import ru.gaidamaka.game.GameObservable;
import ru.gaidamaka.game.GameObserver;
import ru.gaidamaka.game.GameState;
import ru.gaidamaka.net.messages.JoinMessage;
import ru.gaidamaka.net.messages.Message;
import ru.gaidamaka.net.messages.PingMessage;
import ru.gaidamaka.net.node.GameNode;
import ru.gaidamaka.net.node.NodeFactory;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

public class MainNodeHandler implements NodeHandler, GameObservable {
    private static final Logger logger = LoggerFactory.getLogger(MainNodeHandler.class);
    private final List<GameObserver> gameStateObservers = new ArrayList<>();
    private final DatagramSocket socket;
    private final Sender sender;
    private final Receiver receiver;
    private final Thread senderThread;
    private final Thread receiverThread;

    private NetNode master;

    private Timer pingSendTimer;

    private Role nodeRole;
    private GameNode gameNode;
    private final MessageStorage messageStorage;
    private Config config;
    private GameState gameState;

    public MainNodeHandler(@NotNull Role nodeRole, int port) {
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            logger.error("Cant create socket", e);
            throw new IllegalStateException("Cant create socket", e);
        }
        messageStorage = new MessageStorage();
        sender = new Sender(messageStorage, socket, config.getNodeTimeoutMs());
        senderThread = new Thread(sender);
        receiver = new Receiver(messageStorage, socket);
        receiverThread = new Thread(receiver);
        changeNodeRole(nodeRole);
        startSendPingMessages();
    }

    public void startSendPingMessages() {
        if (pingSendTimer == null) {
            pingSendTimer = new Timer();
        }
        pingSendTimer.cancel();
        pingSendTimer.schedule(getPingTimerTask(), 0, config.getPingDelayMs());
    }

    @NotNull
    private TimerTask getPingTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                messageStorage.addMessageToSend(master, new PingMessage());
            }
        };
    }

    public void setConfig(@NotNull Config config) {
        this.config = Objects.requireNonNull(config, "Config cant be null");
    }

    public void joinToGame(@NotNull NetNode gameOwner,
                           @NotNull String playerName) {
        sender.addMessageToSend(
                gameOwner,
                new JoinMessage(playerName)
        );
    }

    @Override
    public void changeNodeRole(@NotNull Role nodeRole) {
        if (config == null) {
            logger.error("Cant change role={} to {} without config", this.nodeRole, nodeRole);
            throw new IllegalStateException("Cant change role without config");
        }
        this.nodeRole = nodeRole;
        gameNode = NodeFactory.createNode(
                Objects.requireNonNull(nodeRole),
                config
        );
        gameNode.setNodeHandler(this);
    }


    @Override
    public void sendMessage(@NotNull NetNode receiver, @NotNull Message message) {
        messageStorage.addMessageToSend(receiver, message);
    }

    @Override
    public void updateState(@NotNull GameState gameState) {
        this.gameState = gameState;
        notifyObservers();
    }

    @Override
    public void showError(@NotNull String errorMessage) {

    }

    @Override
    public void setMaster(@NotNull NetNode newMasterNode) {
        this.master = Objects.requireNonNull(newMasterNode, "Master node cant be null");
    }

    @Override
    public void lose() {

    }

    @Override
    public void addObserver(@NotNull GameObserver gameObserver) {
        gameStateObservers.add(Objects.requireNonNull(gameObserver));
    }

    @Override
    public void removeObserver(@NotNull GameObserver gameObserver) {
        gameStateObservers.remove(gameObserver);
    }

    @Override
    public void notifyObservers() {
        GameState state = this.gameState;
        gameStateObservers.forEach(gameObserver -> gameObserver.update(state));
    }
}
