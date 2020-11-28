package ru.gaidamaka.node;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;
import ru.gaidamaka.config.ProtoGameConfigAdapter;
import ru.gaidamaka.game.Direction;
import ru.gaidamaka.game.Game;
import ru.gaidamaka.game.player.Player;

import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class MasterNode {
    private final Game game;
    private final SnakesProto.GameConfig gameConfig;
    private final Map<Player, Direction> playersMoves = new ConcurrentHashMap<>();

    public MasterNode(@NotNull SnakesProto.GameConfig gameConfig) {
        this.gameConfig = Objects.requireNonNull(gameConfig);
        game = new Game(new ProtoGameConfigAdapter(gameConfig));
        startGameUpdateTimer();
    }

    void registerNewMove(@NotNull Player player, @NotNull Direction direction) {
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
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, gameConfig.getStateDelayMs());
    }

}
