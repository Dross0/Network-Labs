package ru.gaidamaka.net.gamelistchecker;

import org.jetbrains.annotations.NotNull;

public interface GameListObservable {
    void addObserver(@NotNull GameListObserver observer);

    void removeObserver(@NotNull GameListObserver observer);

    void notifyObservers();
}
