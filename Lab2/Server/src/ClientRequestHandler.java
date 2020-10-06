
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientRequestHandler implements Runnable {
    private final static Logger logger = Logger.getLogger(ClientRequestHandler.class.getName());
    private final long SPEED_TEST_INTERVAL = 100;
    private final int BUFFER_SIZE = 1024;

    private final Socket socket;

    public ClientRequestHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream clientReader = new DataInputStream(socket.getInputStream());
             DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
             socket){
            int fileNameSize = clientReader.readInt();
            String fileName = clientReader.readUTF();
            if (fileNameSize != fileName.length()){
                logger.log(Level.SEVERE, "Wrong file name size");
                sendFeedBack(writer, ResponseCode.FAILURE_FILENAME_TRANSFER);
                return;
            }
            sendFeedBack(writer, ResponseCode.SUCCESS_FILENAME_TRANSFER);
            long fileSize = clientReader.readLong();
            Path path = createFile(fileName);
            try (OutputStream fileWriter = Files.newOutputStream(path)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                long allReadBytes = 0;
                long initTime = System.currentTimeMillis();
                long lastTime = initTime;
                boolean clientActiveLessSpeedTestInterval = true;
                MessageDigest md = MessageDigest.getInstance("MD5");
                while (allReadBytes < fileSize) {
                    int readBytes;
                    if ((readBytes = clientReader.read(buffer, 0, BUFFER_SIZE)) >= 0) {
                        fileWriter.write(buffer, 0, readBytes);
                        md.update(buffer, 0, readBytes);
                    }
                    allReadBytes += readBytes;
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime > SPEED_TEST_INTERVAL){
                        long currentSpeed = readBytes * 1000 / (currentTime - lastTime);
                        long avgSpeed = allReadBytes * 1000 / (currentTime - initTime);
                        logger.info("File - {" + path.getFileName() +"} has current speed = " + currentSpeed + ", avg speed = " + avgSpeed);
                        lastTime = currentTime;
                        clientActiveLessSpeedTestInterval = false;
                    }
                }
                if (clientActiveLessSpeedTestInterval){
                    long speed = allReadBytes * 1000 / (System.currentTimeMillis() - lastTime);
                    logger.info("File - {" + path.getFileName() +"} has speed = "+ speed);
                }

                String hashSum =  clientReader.readUTF();
                ResponseCode fileCheckCode = ResponseCode.SUCCESS_FILE_TRANSFER;
                if (!hashSum.equals(hashSumToString(md))){
                    fileCheckCode = ResponseCode.FAILURE_FILE_TRANSFER;
                }
                sendFeedBack(writer, fileCheckCode);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private Path createFile(String filename) throws IOException {
        Path dirPath = Paths.get("uploads");
        if (!Files.exists(dirPath)) {
            Files.createDirectory(dirPath);
        }
        Path path = Paths.get(dirPath + "/" + filename);
        if (Files.exists(path)){
            path = Paths.get(dirPath + "/" + generateRandomFileName(filename));
        }
        Files.createFile(path);
        return path;
    }

    private String hashSumToString(MessageDigest md){
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }


    private String generateRandomFileName(String fileName){
        Random random = new Random();
        return Math.abs(random.nextInt()) + "_" + fileName;
    }


    private void sendFeedBack(DataOutputStream writer, ResponseCode response) throws IOException {
        writer.writeInt(response.getCode());
        writer.flush();
    }
}
