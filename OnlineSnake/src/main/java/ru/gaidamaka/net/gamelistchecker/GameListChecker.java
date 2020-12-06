package ru.gaidamaka.net.gamelistchecker;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gaidamaka.net.GameInfo;
import ru.gaidamaka.net.messages.AnnouncementMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameListChecker implements GameListObservable {
    private static final Logger logger = LoggerFactory.getLogger(GameListChecker.class);
    private static final int BUFFER_SIZE = 1024;

    @NotNull
    private final List<GameListObserver> observers = new CopyOnWriteArrayList<>();
    @NotNull
    private final Set<GameInfo> gameInfos = new LinkedHashSet<>();
    @NotNull
    private final InetAddress multicastForPublishGameList;
    private final int port;

    @NotNull
    private final Thread checkerThread;

    public GameListChecker(@NotNull InetAddress multicastForPublishGameList, int port) {
        validateAddress(Objects.requireNonNull(multicastForPublishGameList));
        this.multicastForPublishGameList = multicastForPublishGameList;
        this.port = port;
        this.checkerThread = new Thread(getCheckerRunnable());
    }

    private void validateAddress(InetAddress multicastAddress) {
        if (!multicastAddress.isMulticastAddress()) {
            throw new IllegalArgumentException(multicastAddress + " is not multicast");
        }
    }

    public void start() {
        checkerThread.start();
    }

    public void stop() {
        checkerThread.interrupt();
    }

    private Runnable getCheckerRunnable() {
        return () -> {
            try (MulticastSocket socket = new MulticastSocket(port)) {
                socket.joinGroup(multicastForPublishGameList);
                byte[] buffer = new byte[BUFFER_SIZE];
                while (!Thread.currentThread().isInterrupted()) {
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, BUFFER_SIZE);
                    socket.receive(datagramPacket);
                    deserializeAnnouncementMessage(buffer).
                            ifPresent(announcementMessage -> {
                                gameInfos.add(parseGameInfo(announcementMessage));
                                notifyObservers();
                            });
                }
            } catch (IOException e) {
                logger.error("Problem with multicast socket on port={}", port, e);
            }
        };
    }

    private Optional<AnnouncementMessage> deserializeAnnouncementMessage(byte[] messageBytes) {
        try {
            return Optional.of(SerializationUtils.deserialize(messageBytes));
        } catch (SerializationException e) {
            logger.error("Cant deserialize announcement message");
            return Optional.empty();
        }
    }

    @Override
    public void addObserver(@NotNull GameListObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(@NotNull GameListObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        Set<GameInfo> sharedGameInfos = Set.copyOf(gameInfos);
        gameInfos.clear();
        for (GameListObserver gameListObserver : observers) {
            gameListObserver.updateGameList(sharedGameInfos);
        }
    }

    private GameInfo parseGameInfo(@NotNull AnnouncementMessage announcementMsg) {
        return new GameInfo(
                announcementMsg.getConfig(),
                announcementMsg.getPlayersNumber(),
                announcementMsg.canJoin()
        );
    }


}
