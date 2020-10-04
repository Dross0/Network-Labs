import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientRequestHandler implements Runnable {
    private Socket socket;
    private Server server;

    public ClientRequestHandler(Socket socket, Server server){
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            int fileNameSize = Integer.parseInt(clientReader.readLine(), 10);
            String fileName = clientReader.readLine();
            if (fileNameSize != fileName.length()){
                System.out.println("Wrong file size");
            }
            long fileSize = Integer.parseInt(clientReader.readLine(), 10);
            System.out.println(fileSize);
            while (true){
                String s = clientReader.readLine();
                if (s != null) {
                    System.out.println(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
