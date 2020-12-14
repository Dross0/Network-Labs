package ru.gaidamaka.attachment;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gaidamaka.SOCKSErrorCode;
import ru.gaidamaka.utils.SelectionKeyUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public class ServerHandler extends Attachment implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    private static final int BUFFER_SIZE = 1024;

    private boolean isEndOfStream = false;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    private final SelectionKey serverKey;
    private final SelectionKey clientKey;
    private final SocketChannel channel;
    private boolean isConnected;

    public ServerHandler(@NotNull SelectionKey serverKey, @NotNull SelectionKey clientKey) {
        super(AttachmentType.SERVER_HANDLER);
        this.clientKey = Objects.requireNonNull(clientKey, "Client key cant be null");
        this.serverKey = Objects.requireNonNull(serverKey, "Server key cant be null");
        channel = (SocketChannel) serverKey.channel();
        isConnected = false;
    }

    public void sendDataToServer() {
        ClientHandler clientHandler = getClientHandler();
        try {
            channel.write(clientHandler.getBuffer());
        } catch (IOException e) {
            logger.error("Error while write", e);
            closeWithoutException();
            throw new IllegalStateException("Error while write", e);
        }
        if (!clientHandler.getBuffer().hasRemaining()) {
            SelectionKeyUtils.turnOffWriteOption(serverKey);
            SelectionKeyUtils.turnOnReadOption(clientKey);
        }
    }

    @NotNull
    private ClientHandler getClientHandler() {
        Attachment attachment = (Attachment) clientKey.attachment();
        if (attachment.getType() != AttachmentType.CLIENT_HANDLER) {
            logger.error("Client key attachment is not client handler and has type={}", attachment.getType());
            throw new IllegalStateException("Client key attachment is not client handler and has type=" + attachment.getType());
        }
        return (ClientHandler) attachment;
    }

    public void getDataFromServer() {
        buffer.clear();
        int readBytes = -1;
        try {
            readBytes = channel.read(buffer);
        } catch (IOException e) {
            logger.error("Error while reading");
            throw new IllegalStateException("Error while reading");
        }
        if (readBytes == -1) {
            handleEndOfStream();
            return;
        }
        buffer.flip();
        SelectionKeyUtils.turnOffReadOption(serverKey);
        SelectionKeyUtils.turnOnWriteOption(clientKey);
    }

    private void handleEndOfStream() {
        isEndOfStream = true;
        SelectionKeyUtils.turnOffReadOption(serverKey);
        ClientHandler clientHandler = getClientHandler();
        clientHandler.shutdownOutput();
        if (clientHandler.isEndOfStream()) {
            closeWithoutException();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void connect() {
        if (isConnected) {
            throw new IllegalStateException("Already connected");
        }
        if (serverKey.isConnectable()) {
            checkFinishConnect();
            ClientHandler clientHandler = getClientHandler();
            clientHandler.createResponse(SOCKSErrorCode.SUCCESS);
            serverKey.interestOps(SelectionKey.OP_READ);
            buffer.clear();
            buffer.flip();
            isConnected = true;
        }
    }

    private void checkFinishConnect() {
        try {
            if (!channel.finishConnect()) {
                throw new IllegalStateException("Cant connect");
            }
        } catch (IOException e) {
            logger.error("Error while finish connect", e);
            throw new IllegalStateException("Error while finish connect", e);
        }
    }

    @NotNull
    public ByteBuffer getBuffer() {
        return buffer;
    }


    public boolean isEndOfStream() {
        return isEndOfStream;
    }

    public void shutdownOutput() {
        try {
            channel.shutdownOutput();
        } catch (IOException e) {
            logger.error("Error while shutdown output", e);
            throw new UncheckedIOException("Error while shutdown output", e);
        }
    }

    private void closeWithoutException() {
        try {
            channel.close();
            clientKey.channel().close();
        } catch (IOException e) {
            logger.error("Error while closing", e);
        }
    }

    @Override
    public void close() throws IOException {
        channel.close();
        clientKey.channel().close();
    }

    public boolean isReadable() {
        return serverKey.isReadable();
    }

    public boolean isWritable() {
        return serverKey.isWritable();
    }
}
