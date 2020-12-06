package ru.gaidamaka.net.node;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gaidamaka.config.Config;
import ru.gaidamaka.game.GameState;
import ru.gaidamaka.net.NetNode;
import ru.gaidamaka.net.NodeHandler;
import ru.gaidamaka.net.Role;
import ru.gaidamaka.net.messagehandler.ErrorMessageHandler;
import ru.gaidamaka.net.messagehandler.RoleChangeMessageHandler;
import ru.gaidamaka.net.messagehandler.StateMessageHandler;
import ru.gaidamaka.net.messages.ErrorMessage;
import ru.gaidamaka.net.messages.Message;
import ru.gaidamaka.net.messages.RoleChangeMessage;
import ru.gaidamaka.net.messages.StateMessage;

import java.util.Objects;

public class DeputyNode implements
        GameNode,
        StateMessageHandler,
        RoleChangeMessageHandler,
        ErrorMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(DeputyNode.class);

    private NodeHandler nodeHandler;
    private GameState lastGameState;

    public DeputyNode(@NotNull Config config) {
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
    }

    @Override
    public void handle(@NotNull NetNode sender, @NotNull StateMessage stateMsg) {
        GameState gameState = stateMsg.getGameState();
        if (lastGameState.getStateID() >= gameState.getStateID()) {
            logger.warn("Received state with id={} less then last game state id={}",
                    gameState.getStateID(),
                    lastGameState.getStateID()
            );
            return;
        }
        lastGameState = gameState;
        nodeHandler.updateState(gameState);
    }

    @Override
    public void handle(@NotNull NetNode sender, @NotNull RoleChangeMessage roleChangeMsg) {
        if (roleChangeMsg.getFromRole() == Role.MASTER && roleChangeMsg.getToRole() == Role.MASTER) {
            nodeHandler.changeNodeRole(Role.MASTER);
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
}
