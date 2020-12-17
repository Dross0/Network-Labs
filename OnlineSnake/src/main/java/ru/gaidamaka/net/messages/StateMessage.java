package ru.gaidamaka.net.messages;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.game.GameState;
import ru.gaidamaka.game.player.Player;
import ru.gaidamaka.net.Neighbor;

import java.util.Map;
import java.util.Objects;

public class StateMessage extends Message {
    @NotNull
    private final GameState gameState;
    @NotNull
    private final Map<Neighbor, Player> playersNode;

    public StateMessage(@NotNull GameState gameState, @NotNull Map<Neighbor, Player> nodePlayerMap) {
        super(MessageType.STATE);
        this.gameState = Objects.requireNonNull(gameState, "Game state cant be null");
        this.playersNode = Objects.requireNonNull(nodePlayerMap, "Node-Players map cant be null");
    }

    @NotNull
    public GameState getGameState() {
        return gameState;
    }

    @NotNull
    public Map<Neighbor, Player> getPlayersNode() {
        return playersNode;
    }
}
