package ru.gaidamaka.net.messages;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ErrorMessage extends Message {
    @NotNull
    private final String errorMessage;

    public ErrorMessage(@NotNull String errorMessage) {
        super(MessageType.ERROR);
        this.errorMessage = Objects.requireNonNull(errorMessage, "Error message cant be null");
    }

    @NotNull
    public String getErrorMessage() {
        return errorMessage;
    }
}
