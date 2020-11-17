package ru.gaidamaka.config;

import org.jetbrains.annotations.NotNull;
import ru.gaidamaka.SnakesProto;

import java.util.Objects;

public class ProtoGameConfigAdapter implements GameConfig {
    private final SnakesProto.GameConfig protoGameConfig;

    public ProtoGameConfigAdapter(@NotNull SnakesProto.GameConfig protoGameConfig) {
        this.protoGameConfig = Objects.requireNonNull(protoGameConfig, "Proto game config cant be null");
    }

    @Override
    public int getFieldWidth() {
        return protoGameConfig.getWidth();
    }

    @Override
    public int getFieldHeight() {
        return protoGameConfig.getHeight();
    }

    @Override
    public int getFoodStaticNumber() {
        return protoGameConfig.getFoodStatic();
    }

    @Override
    public int getFoodPerPlayer() {
        return (int) protoGameConfig.getFoodPerPlayer();
    }

    @Override
    public double getProbabilityOfDeadSnakeCellsToFood() {
        return protoGameConfig.getDeadFoodProb();
    }
}
