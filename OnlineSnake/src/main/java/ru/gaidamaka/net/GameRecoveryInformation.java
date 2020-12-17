package ru.gaidamaka.net;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.game.GameState;
import ru.gaidamaka.game.player.Player;

import java.util.Map;
import java.util.Objects;

public class GameRecoveryInformation {
    @NotNull
    private final GameState gameState;

    @NotNull
    private final Map<Neighbor, Player> registeredPlayers;

    public GameRecoveryInformation(@NotNull GameState gameState, @NotNull Map<Neighbor, Player> registeredPlayers) {
        this.gameState = Objects.requireNonNull(gameState, "Game state cant be null");
        this.registeredPlayers = Objects.requireNonNull(registeredPlayers, "Registered players cant be null");
    }

    @NotNull
    public GameState getGameState() {
        return gameState;
    }

    @NotNull
    public Map<Neighbor, Player> getRegisteredPlayers() {
        return registeredPlayers;
    }
}
