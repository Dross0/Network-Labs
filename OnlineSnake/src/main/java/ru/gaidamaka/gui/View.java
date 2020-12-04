package ru.gaidamaka.gui;

import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.game.cell.Point;
import ru.gaidamaka.game.player.PlayerWithScore;

import java.util.List;

public interface View {
    void drawFruit(@NotNull Point point);

    void drawEmptyCell(@NotNull Point point);

    void drawSnakePoint(@NotNull Point point, @NotNull Color playerSnakeColor);

    void updateCurrentGameInfo(@NotNull String owner, int gameFieldHeight, int gameFieldWidth, int foodNumber);

    void showUserListInfo(@NotNull List<PlayerWithScore> playerWithScoreList);
}
