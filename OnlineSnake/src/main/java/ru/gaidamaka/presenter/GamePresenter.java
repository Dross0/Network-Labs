package ru.gaidamaka.presenter;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.presenter.event.UserEvent;

public interface GamePresenter {
    void fireEvent(@NotNull UserEvent userEvent);
}
