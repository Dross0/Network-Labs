package ru.gaidamaka.net;


import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gaidamaka.SnakesProto;
import ru.gaidamaka.net.node.GameNode;
import ru.gaidamaka.net.node.NodeFactory;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class NodeHandler {
    private static final Logger logger = LoggerFactory.getLogger(NodeHandler.class);
    private static final long DEFAULT_PING_DELAY = 1000;
    private final DatagramSocket socket;
    private final Sender sender;
    private final Receiver receiver;
    private final Thread senderThread;
    private final Thread receiverThread;

    private SnakesProto.NodeRole nodeRole;
    private GameNode gameNode;
    private final MessageStorage messageStorage;
    private final ScheduledExecutorService scheduledExecutorService;

    public NodeHandler(@NotNull SnakesProto.NodeRole nodeRole, int port) {
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            logger.error("Cant create socket", e);
            throw new IllegalStateException("Cant create socket", e);
        }
        messageStorage = new MessageStorage();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        sender = new Sender(messageStorage, socket);
        senderThread = new Thread(sender);
        receiver = new Receiver(messageStorage, socket);
        receiverThread = new Thread(receiver);
        changeNodeRole(nodeRole);
    }

    public void joinToGame(@NotNull NetNode gameOwner,
                           @NotNull String playerName,
                           @NotNull SnakesProto.PlayerType playerType,
                           boolean isOnlyView) {
        SnakesProto.GameMessage.JoinMsg joinMsg = MessageFactory.generateJoinMessage(playerName, playerType, isOnlyView);
        SnakesProto.GameMessage gameMessage = SnakesProto.GameMessage.newBuilder().setJoin(joinMsg).build();  //FIXME Add seqNum
        sender.addMessageToSend(gameOwner, gameMessage);
    }

    private Runnable getPingSendTask() {
        return () -> {

        };
    }

    public void changeNodeRole(@NotNull SnakesProto.NodeRole nodeRole) {
        this.nodeRole = nodeRole;
        gameNode = NodeFactory.createNode(
                Objects.requireNonNull(nodeRole)
        );
    }


}
