import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerMain {
    public static void main(String[] args) throws UnknownHostException {
        Server server = new Server(InetAddress.getByName("localhost"), 3323);
        server.run();
    }
}
