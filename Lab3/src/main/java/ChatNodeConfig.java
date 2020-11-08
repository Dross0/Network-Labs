import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

public class ChatNodeConfig implements Serializable {
    private static final int MIN_LOSS_PERCENTAGE = 0;
    private static final int MAX_LOSS_PERCENTAGE = 100;
    private final String name;
    private final int lossPercentage;
    private final int port;
    private final InetAddress neighborAddress;
    private final int neighborPort;

    public static class Builder {
        //Обязательные параеметры
        private final String name;
        private final int lossPercentage;
        private final int port;

        //Опциональные параметры
        private InetAddress neighborAddress = InetAddress.getLoopbackAddress();
        private int neighborPort = -1;

        public Builder(@NotNull String name, int lossPercentage, int port){
            this.name = Objects.requireNonNull(name, "Node name cant be null");
            validateLossPercentage(lossPercentage);
            this.lossPercentage = lossPercentage;
            validatePort(port);
            this.port = port;
        }

        public Builder neighborAddress(@NotNull InetAddress neighborAddress){
            this.neighborAddress = Objects.requireNonNull(neighborAddress, "Neighbor address cant be null");
            return this;
        }

        public Builder neighborPort(int neighborPort){
            validatePort(neighborPort);
            this.neighborPort = neighborPort;
            return this;
        }

        public ChatNodeConfig build(){
            return new ChatNodeConfig(this);
        }
    }

    private ChatNodeConfig(Builder builder){
        this.name = builder.name;
        this.lossPercentage = builder.lossPercentage;
        this.port = builder.port;
        this.neighborAddress = builder.neighborAddress;
        this.neighborPort = builder.neighborPort;
    }

    private static void validatePort(int port){
        if (port < 0){
            throw new IllegalArgumentException("Port must be positive, actual = {"
                    + port + "}");
        }
    }

    private static void validateLossPercentage(int lossPercentage){
        if (lossPercentage < MIN_LOSS_PERCENTAGE || lossPercentage > MAX_LOSS_PERCENTAGE){
            throw new IllegalArgumentException("Loss percentage not from valid interval = ["
                    + MIN_LOSS_PERCENTAGE + ", "
                    + MAX_LOSS_PERCENTAGE + "]");
        }
    }

    @NotNull
    public String getName() {
        return name;
    }

    public int getLossPercentage() {
        return lossPercentage;
    }

    public int getPort() {
        return port;
    }

    @NotNull
    public InetAddress getNeighborAddress() {
        return neighborAddress;
    }

    public int getNeighborPort() {
        return neighborPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatNodeConfig that = (ChatNodeConfig) o;
        return port == that.port &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, port);
    }
}
