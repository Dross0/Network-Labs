package ru.gaidamaka.net;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.game.GameState;
import ru.gaidamaka.net.messages.Message;
import ru.gaidamaka.net.node.Role;

import java.net.InetSocketAddress;

public interface NodeHandler {

    void changeNodeRole(@NotNull Role nodeRole);

    void changeNodeRole(@NotNull Role nodeRole, GameRecoveryInformation gameState);

    void sendMessage(@NotNull NetNode netNode, @NotNull Message message);

    void updateState(@NotNull GameState gameState);

    void showError(@NotNull String errorMessage);

    void setMaster(@NotNull NetNode sender);

    void lose();

    @NotNull
    NetNode getMaster();

    @NotNull
    InetSocketAddress getMulticastInfo();
}
