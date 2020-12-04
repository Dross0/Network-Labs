package ru.gaidamaka.net;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;
import ru.gaidamaka.config.GameConfig;

import java.util.Objects;

public class GameInfo {
    private final GameConfig config;
    private final boolean canJoin;
    private final SnakesProto.GamePlayers gamePlayers;

    public GameInfo(@NotNull GameConfig config, boolean canJoin, @NotNull SnakesProto.GamePlayers gamePlayers) {
        this.config = Objects.requireNonNull(config);
        this.canJoin = canJoin;
        this.gamePlayers = Objects.requireNonNull(gamePlayers);
    }

    @NotNull
    public GameConfig getConfig() {
        return config;
    }

    public boolean isCanJoin() {
        return canJoin;
    }

    @NotNull
    public SnakesProto.GamePlayers getGamePlayers() {
        return gamePlayers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameInfo gameInfo = (GameInfo) o;
        return canJoin == gameInfo.canJoin &&
                Objects.equals(config, gameInfo.config) &&
                Objects.equals(gamePlayers, gameInfo.gamePlayers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, canJoin, gamePlayers);
    }
}
