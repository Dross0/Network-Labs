import java.io.IOException;
import java.net.*;
import java.util.*;

public class App {
    private InetAddress address;
    private int port;
    private HashMap<String, Long> lastMessages = new HashMap<>();
    private final int MESSAGES_INTERVAL = 100;
    private final int TTL = 1000;

    public App(InetAddress address, int port){
        this.address = address;
        this.port = port;
    }

    private void sendMessage(DatagramSocket socket, String message) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.address, this.port);
        socket.send(packet);
    }

    private String getIdByAddressAndPort(InetAddress address, int port){
        return address + ":" + port;
    }

    public void run() throws IOException {
        MulticastSocket recvSocket = new MulticastSocket(port);
        DatagramSocket sendSocket = new DatagramSocket();
        Timer timer = new Timer();
        TimerTask sendTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    sendMessage(sendSocket, "CHECK_COPY");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(sendTask, 0, MESSAGES_INTERVAL);
        byte [] buffer = new byte[1024];
        recvSocket.setSoTimeout(100);
        recvSocket.joinGroup(this.address);
        while (true){
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                recvSocket.receive(packet);
            }
            catch (SocketTimeoutException ex){
                //sendMessage(sendSocket, "CHECK_COPY");
                continue;
            }
            String id = getIdByAddressAndPort(packet.getAddress(), packet.getPort());
            if (!lastMessages.containsKey(id)){
                System.out.println("App with id = " + id + " was registered");
            }
            removeUnavailable(System.currentTimeMillis());
            lastMessages.put(id, System.currentTimeMillis());
        }
    }

    private void removeUnavailable(long currentTimeMillis) {
        for (Iterator<Map.Entry<String, Long>> it = this.lastMessages.entrySet().iterator(); it.hasNext();){
            Map.Entry<String, Long> entry = it.next();
            if (currentTimeMillis - entry.getValue() > TTL){
                System.out.println("App with id = " + entry.getKey() + " was unconnected");
                it.remove();
            }
        }
    }

}
