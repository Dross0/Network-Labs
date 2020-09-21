import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static final int DEFAULT_MESSAGE_INTERVAL = 100;
    private static final int DEFAULT_TTL = 1000;

    public static void main(String[] args) throws IOException {
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
        }
        App app = new App(InetAddress.getByName("FF02:0:0:0:0:1:FF23:A050"), 1333, messageInterval, ttl); //230.0.0.0
        app.run();

    }
}
