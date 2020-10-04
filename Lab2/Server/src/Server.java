import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable{
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private static final int DEFAULT_BACKLOG = 3;
    private final InetAddress address;
    private final int port;
    private final int backlog;

    public Server(InetAddress address, int port, int backlog){
        this.address = address;
        this.port = port;
        this.backlog = backlog;
    }

    public Server(InetAddress address, int port) {
        this(address, port, DEFAULT_BACKLOG);
    }


    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(this.port, this.backlog)){
            //serverSocket.bind(new InetSocketAddress(this.address, this.port));
            logger.info("Server start on address - " + this.address + ":" + this.port);
            while (true){
                Socket socket = serverSocket.accept();
                logger.info("Client connected - " + socket.getInetAddress() + ":" + socket.getPort());
                Thread clientHandler = new Thread(new ClientRequestHandler(socket, this));
                clientHandler.start();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Cant open server socket on port: " + this.port);
        }
    }
}
