package ru.gaidamaka.net.node;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gaidamaka.config.Config;
import ru.gaidamaka.game.Direction;
import ru.gaidamaka.game.GameState;
import ru.gaidamaka.game.player.Player;
import ru.gaidamaka.net.GameRecoveryInformation;
import ru.gaidamaka.net.Neighbor;
import ru.gaidamaka.net.NetNode;
import ru.gaidamaka.net.NodeHandler;
import ru.gaidamaka.net.messagehandler.ErrorMessageHandler;
import ru.gaidamaka.net.messagehandler.RoleChangeMessageHandler;
import ru.gaidamaka.net.messagehandler.StateMessageHandler;
import ru.gaidamaka.net.messages.*;
import ru.gaidamaka.utils.DurationUtils;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class DeputyNode implements
        GameNode,
        StateMessageHandler,
        RoleChangeMessageHandler,
        ErrorMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(DeputyNode.class);
    @NotNull
    private final Config config;

    private NodeHandler nodeHandler;
    private GameState lastGameState;
    private Neighbor master;
    private final Timer masterCheckTimer;
    private Map<Neighbor, Player> lastPlayers;

    public DeputyNode(@NotNull Config config) {
        this.config = Objects.requireNonNull(config, "Config cant be null");
        this.masterCheckTimer = new Timer();
        startMasterCheck();
    }

    private void startMasterCheck() {
        masterCheckTimer.schedule(getMasterCheckTask(), 0, config.getNodeTimeoutMs() / 2);
    }

    private TimerTask getMasterCheckTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if (master != null
                        && DurationUtils.betweenInMs(master.getLastSeenTime(), Instant.now()) > (config.getStateDelayMs() * 2)) {
                    swapToMaster();
                }
            }
        };
    }


    @Override
    public void handleMessage(@NotNull NetNode sender, @NotNull Message message) {
        Objects.requireNonNull(message, "Message cant be null");
        Objects.requireNonNull(sender, "Sender cant be null");
        switch (message.getType()) {
            case ERROR:
                handle(sender, (ErrorMessage) message);
                break;
            case ROLE_CHANGE:
                handle(sender, (RoleChangeMessage) message);
                break;
            case STATE:
                handle(sender, (StateMessage) message);
                break;
            default:
                throw new IllegalStateException("Cant handle this message type = " + message.getType());
        }
    }

    @Override
    public void setNodeHandler(@NotNull NodeHandler nodeHandler) {
        this.nodeHandler = nodeHandler;
        this.master = new Neighbor(nodeHandler.getMaster());
    }

    @Override
    public void handle(@NotNull NetNode sender, @NotNull StateMessage stateMsg) {
        GameState gameState = stateMsg.getGameState();
        if (lastGameState != null && lastGameState.getStateID() >= gameState.getStateID()) {
            logger.warn("Received state with id={} less then last game state id={}",
                    gameState.getStateID(),
                    lastGameState.getStateID()
            );
            return;
        }
        lastGameState = gameState;
        lastPlayers = stateMsg.getPlayersNode();
        master.updateLastSeenTime();
        nodeHandler.updateState(gameState);
    }

    @Override
    public void handle(@NotNull NetNode sender, @NotNull RoleChangeMessage roleChangeMsg) {
        if (roleChangeMsg.getFromRole() == Role.MASTER && roleChangeMsg.getToRole() == Role.MASTER) {
            swapToMaster();
        } else if (roleChangeMsg.getFromRole() == Role.MASTER && roleChangeMsg.getToRole() == Role.VIEWER) {
            nodeHandler.lose();
        } else {
            logger.warn("Unsupported roles at role change message={} from={}", roleChangeMsg, sender);
            throw new IllegalArgumentException("Unsupported roles at role change message=" + roleChangeMsg + " from=" + sender);
        }

    }

    @Override
    public void handle(@NotNull NetNode sender, @NotNull ErrorMessage errorMsg) {
        nodeHandler.showError(errorMsg.getErrorMessage());
    }

    @Override
    public void makeMove(@NotNull Direction direction) {
        nodeHandler.sendMessage(
                nodeHandler.getMaster(),
                new SteerMessage(direction)
        );
    }

    private void swapToMaster() {
        nodeHandler.changeNodeRole(Role.MASTER, new GameRecoveryInformation(lastGameState, lastPlayers));
        lastPlayers.keySet().forEach(neighbor ->
                nodeHandler.sendMessage(
                        neighbor,
                        new RoleChangeMessage(Role.DEPUTY, Role.NORMAL)
                )
        );
        stop();
    }

    @Override
    public void stop() {
        masterCheckTimer.cancel();
    }
}
