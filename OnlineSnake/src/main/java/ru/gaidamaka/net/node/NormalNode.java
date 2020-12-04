package ru.gaidamaka.net.node;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;
import ru.gaidamaka.net.NetNode;
import ru.gaidamaka.net.messagehandler.AnnouncementMessageHandler;
import ru.gaidamaka.net.messagehandler.ErrorMessageHandler;
import ru.gaidamaka.net.messagehandler.StateMessageHandler;

public class NormalNode implements
        GameNode,
        ErrorMessageHandler,
        AnnouncementMessageHandler,
        StateMessageHandler {
    private MasterNode masterNode;


    @Override
    public void handleMessage(@NotNull NetNode sender, SnakesProto.GameMessage message) {

    }

    @Override
    public void handle(@NotNull NetNode sender, @NotNull SnakesProto.GameMessage.AnnouncementMsg announcementMsg) {

    }

    @Override
    public void handle(@NotNull NetNode sender, @NotNull SnakesProto.GameMessage.ErrorMsg errorMsg) {

    }

    @Override
    public void handle(@NotNull NetNode sender, @NotNull SnakesProto.GameMessage.StateMsg stateMsg) {

    }
}
