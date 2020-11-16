package ru.gaidamaka.game;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class Player {
    @NotNull
    private final String name;

    @NotNull
    private final UUID uuid;

    private Player(@NotNull String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

    public static Player create(@NotNull String name){
        return new Player(Objects.requireNonNull(name));
    }

    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public String
    toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", uuid=" + uuid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equals(player.name) &&
                uuid.equals(player.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uuid);
    }
}
