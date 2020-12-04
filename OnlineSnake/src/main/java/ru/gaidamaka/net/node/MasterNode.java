package ru.gaidamaka.net.node;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;
import ru.gaidamaka.config.ProtoGameConfigAdapter;
import ru.gaidamaka.game.Direction;
import ru.gaidamaka.game.Game;
import ru.gaidamaka.game.player.Player;
import ru.gaidamaka.net.NetNode;
import ru.gaidamaka.net.messagehandler.AckMessageHandler;
import ru.gaidamaka.net.messagehandler.JoinMessageHandler;
import ru.gaidamaka.net.messagehandler.RoleChangeMessageHandler;
import ru.gaidamaka.net.messagehandler.SteerMessageHandler;

import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class MasterNode implements
        GameNode,
        AckMessageHandler,
        RoleChangeMessageHandler,
        JoinMessageHandler,
        SteerMessageHandler {
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
                playersMoves.clear();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, gameConfig.getStateDelayMs());
    }

    @Override
    public void handleMessage(@NotNull NetNode sender, SnakesProto.GameMessage message) {
        String messageType = message.getTypeCase().name();
        switch (messageType) {
            case "ack":
                handle(sender, message.getAck());
                break;
            case "role_change":
                handle(sender, message.getRoleChange());
                break;
            case "steer":
                handle(sender, message.getSteer());
                break;
            case "join":
                handle(sender, message.getJoin());
                break;
            default:
                throw new IllegalStateException("Cant handle this message type = " + messageType);
        }
    }

    @Override
    public void handle(@NotNull NetNode sender, SnakesProto.GameMessage.@NotNull AckMsg ackMsg) {

    }

    @Override
    public void handle(@NotNull NetNode sender, SnakesProto.GameMessage.@NotNull JoinMsg joinMsg) {

    }

    @Override
    public void handle(@NotNull NetNode sender, SnakesProto.GameMessage.@NotNull RoleChangeMsg roleChangeMsg) {

    }

    @Override
    public void handle(@NotNull NetNode sender, SnakesProto.GameMessage.@NotNull SteerMsg steerMsg) {

    }
}
