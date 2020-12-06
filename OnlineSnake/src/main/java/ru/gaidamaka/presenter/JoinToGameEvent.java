package ru.gaidamaka.presenter;

import org.jetbrains.annotations.NotNull;

public class JoinToGameEvent extends UserEvent {
    @NotNull
    private final String ownerName;

    public JoinToGameEvent(@NotNull String ownerName) {
        super(UserEventType.JOIN_GAME);
        this.ownerName = ownerName;
    }

    @NotNull
    public String getOwnerName() {
        return ownerName;
    }
}
