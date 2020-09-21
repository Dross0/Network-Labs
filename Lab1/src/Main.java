import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static final int DEFAULT_MESSAGE_INTERVAL = 100;
    private static final int DEFAULT_TTL = 1000;
    private static final int DEFAULT_PORT = 1333;

    public static void main(String[] args) throws IOException {
        if (args.length == 0){
            logger.log(Level.SEVERE, "Wrong arguments amount");
            System.exit(2);
        }
        Properties config = new Properties();
        int messageInterval = DEFAULT_MESSAGE_INTERVAL;
        int ttl = DEFAULT_TTL;
        InputStream fis = Main.class.getResourceAsStream("config.properties");
        if (fis != null) {
            config.load(fis);
            messageInterval = Integer.parseInt(config.getProperty("messageInterval", String.valueOf(DEFAULT_MESSAGE_INTERVAL)), 10);
            ttl = Integer.parseInt(config.getProperty("ttl", String.valueOf(DEFAULT_TTL)), 10);
        }
        else {
            logger.warning("Problem with config file");
            logger.info("Message interval has default value = " + DEFAULT_MESSAGE_INTERVAL);
            logger.info("TTL has default value = " + DEFAULT_TTL);
        }
        int port = DEFAULT_PORT;
        try {
            if (args.length >= 2) {
                port = Integer.parseInt(args[1], 10);
            }
        }
        catch (NumberFormatException ex){
            logger.warning("Wrong port format");
            logger.info("Port has default value = " + DEFAULT_PORT);
        }
        String multicastIP = args[0];
        InetAddress address = InetAddress.getByName(multicastIP);
        if (!address.isMulticastAddress()){
            logger.log(Level.SEVERE, "IP = " + multicastIP + " is from multicast range");
            System.exit(1);
        }
        App app = new App(InetAddress.getByName(multicastIP), port, messageInterval, ttl);
        app.run();
    }
}
