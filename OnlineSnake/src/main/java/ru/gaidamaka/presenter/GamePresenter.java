package ru.gaidamaka.presenter;

import org.jetbrains.annotations.NotNull;

public interface GamePresenter {
    void fireEvent(@NotNull UserEvent userEvent);

    void fireEvent(MoveEvent event, boolean arrowKey);
}
