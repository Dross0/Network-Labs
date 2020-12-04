package ru.gaidamaka.config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gaidamaka.SnakesProto;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Properties;

public final class ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);

    private ConfigReader() {
    }

    public static SnakesProto.GameConfig readProtoConfig(@NotNull String configPath) {
        Objects.requireNonNull(configPath, "Config path cant be null");
        try (InputStream cfgStream = ConfigReader.class.getClassLoader().getResourceAsStream(configPath)) {
            Properties properties = new Properties();
            properties.load(cfgStream);
            return parseProtoConfig(properties);
        } catch (IOException e) {
            logger.error("Cant open config file={}", configPath, e);
            throw new IllegalStateException("Cant open file ={" + configPath + "}", e);
        }
    }

    private static SnakesProto.GameConfig parseProtoConfig(Properties properties) {
        return SnakesProto.GameConfig.newBuilder()
                .setDeadFoodProb(
                        (float) readDoubleProperty(properties, "food.deadProb").orElseThrow()
                )
                .setFoodPerPlayer(
                        (float) readDoubleProperty(properties, "food.perPlayer").orElseThrow()
                )
                .setHeight(
                        readIntegerProperty(properties, "field.height").orElseThrow()
                )
                .setWidth(
                        readIntegerProperty(properties, "field.width").orElseThrow()
                )
                .setFoodStatic(
                        readIntegerProperty(properties, "food.static").orElseThrow()
                )
                .setNodeTimeoutMs(
                        readIntegerProperty(properties, "node.timeout.ms").orElseThrow()
                )
                .setPingDelayMs(
                        readIntegerProperty(properties, "ping.delay.ms").orElseThrow()
                )
                .setStateDelayMs(
                        readIntegerProperty(properties, "state.delay.ms").orElseThrow()
                )
                .build();
    }

    private static OptionalDouble readDoubleProperty(Properties properties, String key) {
        try {
            return OptionalDouble.of(
                    Double.parseDouble(properties.getProperty(key))
            );
        } catch (NumberFormatException e) {
            logger.error("Cant read double property by key = {}", key, e);
            return OptionalDouble.empty();
        }
    }

    private static OptionalInt readIntegerProperty(Properties properties, String key) {
        try {
            return OptionalInt.of(
                    Integer.parseInt(properties.getProperty(key), 10)
            );
        } catch (NumberFormatException e) {
            logger.error("Cant read integer property by key = {}", key, e);
            return OptionalInt.empty();
        }
    }
}
