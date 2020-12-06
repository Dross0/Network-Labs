package ru.gaidamaka.game.player;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Player implements Serializable {
    @NotNull
    private final String name;

    @NotNull
    private final UUID uuid;

    private Player(@NotNull String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

    public static Player create(@NotNull String name) {
        return new Player(Objects.requireNonNull(name));
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
