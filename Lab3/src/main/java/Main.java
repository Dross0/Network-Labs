import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final int NODE_NAME_ARG_INDEX = 0;
    private static final int LOSS_PERCENTAGE_ARG_INDEX = 1;
    private static final int PORT_ARG_INDEX = 2;
    private static final int NEIGHBOR_IP_ARG_INDEX = 3;
    private static final int NEIGHBOR_PORT_ARG_INDEX = 4;

    private static final int ARGUMENTS_NUMBER_WITHOUT_NEIGHBOR = 3;
    private static final int ARGUMENTS_NUMBER_WITH_NEIGHBOR = 5;

    public static void main(String[] args) {
        if (args.length != ARGUMENTS_NUMBER_WITHOUT_NEIGHBOR && args.length != ARGUMENTS_NUMBER_WITH_NEIGHBOR) {
            logger.error("Arguments number must be: "
                    + ARGUMENTS_NUMBER_WITHOUT_NEIGHBOR + " or "
                    + ARGUMENTS_NUMBER_WITH_NEIGHBOR);
            return;
        }
        String nodeName = args[NODE_NAME_ARG_INDEX];
        Optional<Integer> lossPercent = parseInteger(args[LOSS_PERCENTAGE_ARG_INDEX]);
        if (lossPercent.isEmpty()) {
            logger.error("Loss percentage must be a integer, actual = {"
                    + args[LOSS_PERCENTAGE_ARG_INDEX] + "}");
            return;
        }
        Optional<Integer> port = parseInteger(args[PORT_ARG_INDEX]);
        if (port.isEmpty()) {
            logger.error("Node port must be a integer, actual = {"
                    + args[PORT_ARG_INDEX] + "}");
            return;
        }

        try {
            ChatNodeConfig.Builder configBuilder = new ChatNodeConfig.Builder(nodeName, lossPercent.get(), port.get());
            if (args.length == ARGUMENTS_NUMBER_WITH_NEIGHBOR) {
                Optional<InetAddress> neighborAddress = parseInetAddress(args[NEIGHBOR_IP_ARG_INDEX]);
                if (neighborAddress.isEmpty()) {
                    logger.error("Cant parse neighbor ip address from: {"
                            + args[NEIGHBOR_IP_ARG_INDEX] + "}");
                    return;
                }
                Optional<Integer> neighborPort = parseInteger(args[NEIGHBOR_PORT_ARG_INDEX]);
                if (neighborPort.isEmpty()) {
                    logger.error("Neighbor port must be a integer, actual = {"
                            + args[NEIGHBOR_PORT_ARG_INDEX] + "}");
                    return;
                }

                configBuilder
                        .neighborAddress(neighborAddress.get())
                        .neighborPort(neighborPort.get());
            }
            ChatNodeConfig nodeConfig = configBuilder.build();
            ChatNode node = new ChatNode(nodeConfig);
        } catch (IllegalArgumentException e){
            logger.error("Port or loss percentage is not valid", e);
        }
    }

    private static Optional<InetAddress> parseInetAddress(@NotNull String address) {
        Objects.requireNonNull(address, "Address string cant be null");
        try {
            return Optional.of(InetAddress.getByName(address));
        } catch (UnknownHostException e) {
            return Optional.empty();
        }
    }

    @NotNull
    private static Optional<Integer> parseInteger(@NotNull String number) {
        Objects.requireNonNull(number, "Number string cant be null");
        try {
            return Optional.of(Integer.parseInt(number));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
