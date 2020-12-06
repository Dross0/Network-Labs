package ru.gaidamaka.net;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.config.GameConfig;

import java.util.Objects;

public class GameInfo {
    private final GameConfig config;
    private final boolean canJoin;
    private final int playersNumber;

    public GameInfo(@NotNull GameConfig config, int playersNumber, boolean canJoin) {
        this.config = Objects.requireNonNull(config);
        this.canJoin = canJoin;
        this.playersNumber = playersNumber;
    }

    @NotNull
    public GameConfig getConfig() {
        return config;
    }

    public boolean isCanJoin() {
        return canJoin;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameInfo gameInfo = (GameInfo) o;
        return canJoin == gameInfo.canJoin &&
                playersNumber == gameInfo.playersNumber &&
                Objects.equals(config, gameInfo.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, canJoin, playersNumber);
    }
}
