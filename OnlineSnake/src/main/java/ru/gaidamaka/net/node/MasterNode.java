package ru.gaidamaka.net.node;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gaidamaka.config.Config;
import ru.gaidamaka.game.Direction;
import ru.gaidamaka.game.Game;
import ru.gaidamaka.game.GameObserver;
import ru.gaidamaka.game.GameState;
import ru.gaidamaka.game.player.Player;
import ru.gaidamaka.net.NetNode;
import ru.gaidamaka.net.NodeHandler;
import ru.gaidamaka.net.Role;
import ru.gaidamaka.net.messagehandler.JoinMessageHandler;
import ru.gaidamaka.net.messagehandler.RoleChangeMessageHandler;
import ru.gaidamaka.net.messagehandler.SteerMessageHandler;
import ru.gaidamaka.net.messages.*;

import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class MasterNode implements
        GameNode,
        RoleChangeMessageHandler,
        JoinMessageHandler,
        SteerMessageHandler,
        GameObserver {
    private static final Logger logger = LoggerFactory.getLogger(MasterNode.class);

    private final Game game;
    private final @NotNull Config gameConfig;
    private final Map<Player, Direction> playersMoves = new ConcurrentHashMap<>();
    private final Map<NetNode, Player> registeredNodesAsPlayers = new ConcurrentHashMap<>();
    private NodeHandler nodeHandler;

    public MasterNode(@NotNull Config gameConfig) {
        this.gameConfig = Objects.requireNonNull(gameConfig);
        game = new Game(gameConfig);
        startGameUpdateTimer();
    }

    private void registerNewMove(@NotNull Player player, @NotNull Direction direction) {
        playersMoves.put(
                Objects.requireNonNull(player),
                Objects.requireNonNull(direction)
        );
    }

    private void startGameUpdateTimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                game.makeAllPlayersMove(Map.copyOf(playersMoves));
                playersMoves.clear();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, gameConfig.getStateDelayMs());
    }

    @Override
    public void update(@NotNull GameState gameState) {
        registeredNodesAsPlayers.keySet()
                .forEach(netNode ->
                        nodeHandler.sendMessage(
                                netNode,
                                new StateMessage(gameState)
                        )
                );
        nodeHandler.updateState(gameState);
    }


    @Override
    public void handleMessage(@NotNull NetNode sender, @NotNull Message message) {
        Objects.requireNonNull(message, "Message cant be null");
        Objects.requireNonNull(sender, "Sender cant be null");
        switch (message.getType()) {
            case ROLE_CHANGE:
                handle(sender, (RoleChangeMessage) message);
                break;
            case STEER:
                handle(sender, (SteerMessage) message);
                break;
            case JOIN:
                handle(sender, (JoinMessage) message);
                break;
            default:
                throw new IllegalStateException("Cant handle this message type = " + message.getType());
        }
    }

    @Override
    public void setNodeHandler(@NotNull NodeHandler nodeHandler) {
        this.nodeHandler = nodeHandler;
    }


    @Override
    public void handle(@NotNull NetNode sender, @NotNull JoinMessage joinMsg) {
        if (registeredNodesAsPlayers.containsKey(sender)) {
            logger.error("Node={} already registered as player={}", sender, registeredNodesAsPlayers.get(sender));
            throw new IllegalArgumentException("Node={" + sender + "} already registered");
        }
        Player player = registerNewPlayer(sender, joinMsg.getPlayerName());
        logger.debug("NetNode={} was successfully registered as player={}", sender, player);
    }


    @NotNull
    private Player registerNewPlayer(@NotNull NetNode sender, @NotNull String playerName) {
        Player player = Player.create(playerName);
        registeredNodesAsPlayers.put(sender, player);
        return player;
    }

    @Override
    public void handle(@NotNull NetNode sender, @NotNull RoleChangeMessage roleChangeMsg) {
        if (roleChangeMsg.getFromRole() == Role.VIEWER && roleChangeMsg.getToRole() == Role.MASTER) {
            removePlayer(sender);
        } else {
            logger.warn("Unsupported roles at role change message={} from={}", roleChangeMsg, sender);
            throw new IllegalArgumentException("Unsupported roles at role change message=" + roleChangeMsg + " from=" + sender);
        }
    }

    private void removePlayer(NetNode sender) {
        checkRegistration(sender);
        Player player = registeredNodesAsPlayers.get(sender);
        registeredNodesAsPlayers.remove(sender);
        playersMoves.remove(player);
        game.removePlayer(player);
    }

    private void checkRegistration(@NotNull NetNode sender) {
        if (!registeredNodesAsPlayers.containsKey(sender)) {
            logger.error("Node={} is not registered", sender);
            throw new IllegalArgumentException("Node={" + sender + "} is not registered");
        }
    }

    @Override
    public void handle(@NotNull NetNode sender, @NotNull SteerMessage steerMsg) {
        checkRegistration(sender);
        Player senderAsPlayer = registeredNodesAsPlayers.get(sender);
        registerNewMove(senderAsPlayer, steerMsg.getDirection());
        logger.debug("NetNode={} as player={} make move with direction={}", sender, senderAsPlayer, steerMsg.getDirection());
    }
}
