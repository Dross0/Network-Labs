package ru.gaidamaka.net.node;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;
import ru.gaidamaka.net.NetNode;
import ru.gaidamaka.net.messagehandler.AckMessageHandler;
import ru.gaidamaka.net.messagehandler.AnnouncementMessageHandler;
import ru.gaidamaka.net.messagehandler.StateMessageHandler;

public class DeputyNode implements
        GameNode,
        StateMessageHandler,
        AckMessageHandler,
        AnnouncementMessageHandler {
    @Override
    public void handleMessage(@NotNull NetNode sender, SnakesProto.GameMessage message) {

    }

    @Override
    public void handle(@NotNull NetNode sender, @NotNull SnakesProto.GameMessage.AckMsg ackMsg) {

    }

    @Override
    public void handle(@NotNull NetNode sender, @NotNull SnakesProto.GameMessage.AnnouncementMsg announcementMsg) {

    }

    @Override
    public void handle(@NotNull NetNode sender, @NotNull SnakesProto.GameMessage.StateMsg stateMsg) {

    }
}
