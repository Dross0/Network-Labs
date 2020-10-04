import exceptions.FileDoesNotExist;

import java.io.IOException;
import java.net.InetAddress;


public class ClientMain {
    public static void main(String[] args) throws IOException, FileDoesNotExist {
        Client client = new Client("res.txt", InetAddress.getByName("localhost"), 3323);
        client.run();
    }
}
