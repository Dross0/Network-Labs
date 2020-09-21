import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) throws IOException {
        App app = new App(InetAddress.getByName("FF02:0:0:0:0:1:FF23:A050"), 1333); //230.0.0.0
        app.run();

    }
}
