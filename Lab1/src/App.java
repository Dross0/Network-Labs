import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;


public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    private final int messageInterval;
    private final int ttl;
    private InetAddress address;
    private int port;
    private HashMap<String, Long> lastMessages = new HashMap<>();
    private final String DEFAULT_MESSAGE = "";

    public App(InetAddress address, int port, int messageInterval, int ttl){
        this.address = address;
        this.port = port;
        this.messageInterval = messageInterval;
        this.ttl = ttl;
    }

    private void sendMessage(DatagramSocket socket, String message) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.address, this.port);
        socket.send(packet);
    }

    private String getIdByAddressAndPort(InetAddress address, int port){
        return address + ":" + port;
    }

    private long getCurrentTime(){
        return System.currentTimeMillis();
    }

    public void run() throws IOException {
        MulticastSocket recvSocket = new MulticastSocket(port);
        DatagramSocket sendSocket = new DatagramSocket();
//        Timer timer = new Timer();
//        TimerTask sendTask = new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    sendMessage(sendSocket, DEFAULT_MESSAGE);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        timer.schedule(sendTask, 0, this.messageInterval);
        byte [] buffer = new byte[1024];
        recvSocket.setSoTimeout(this.messageInterval);
        recvSocket.joinGroup(this.address);
        while (true){
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                recvSocket.receive(packet);
            }
            catch (SocketTimeoutException ex){
                sendMessage(sendSocket, DEFAULT_MESSAGE);
                continue;
            }
            catch (IOException ex){
                recvSocket.close();
                sendSocket.close();
                return;
            }

            String id = getIdByAddressAndPort(packet.getAddress(), packet.getPort());
            if (!lastMessages.containsKey(id)){
                logger.info("App with id = " + id + " was registered");
            }
            removeUnavailable(getCurrentTime());
            lastMessages.put(id, getCurrentTime());
        }
    }

    private void removeUnavailable(long currentTimeMillis) {
        for (Iterator<Map.Entry<String, Long>> it = this.lastMessages.entrySet().iterator(); it.hasNext();){
            Map.Entry<String, Long> entry = it.next();
            if (currentTimeMillis - entry.getValue() > this.ttl){
                logger.info("App with id = " + entry.getKey() + " was unconnected");
                it.remove();
            }
        }
    }

}