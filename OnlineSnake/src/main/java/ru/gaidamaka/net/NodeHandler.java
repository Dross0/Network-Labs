package ru.gaidamaka.net;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.game.GameState;
import ru.gaidamaka.net.messages.Message;

public interface NodeHandler {

    void changeNodeRole(@NotNull Role nodeRole);

    void sendMessage(@NotNull NetNode netNode, @NotNull Message message);

    void updateState(@NotNull GameState gameState);

    void showError(@NotNull String errorMessage);

    void setMaster(@NotNull NetNode sender);

    void lose();
}
