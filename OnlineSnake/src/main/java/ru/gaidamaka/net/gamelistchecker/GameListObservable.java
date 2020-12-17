package ru.gaidamaka.net.gamelistchecker;

import org.jetbrains.annotations.NotNull;

public interface GameListObservable {
    void addGameListObserver(@NotNull GameListObserver observer);

    void removeGameListObserver(@NotNull GameListObserver observer);

    void notifyObservers();
}
