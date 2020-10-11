import exceptions.FileDoesNotExist;
import exceptions.UnknownResponseCode;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable {
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    private static final int BUFFER_SIZE = 1024;

    private final Path path;
    private final InetAddress serverAddress;
    private final int serverPort;

    public Client(String path, InetAddress serverAddress, int serverPort) throws FileDoesNotExist {
        this.path = getFilePath(path);
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    private Path getFilePath(String pathStr) throws FileDoesNotExist {
        Path tmpPath = Paths.get(pathStr);
        if (!Files.exists(tmpPath)){
            throw new FileDoesNotExist(pathStr + " does not exist");
        }
        return tmpPath;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(this.serverAddress, this.serverPort);
             InputStream fileReader = Files.newInputStream(this.path);
             DataOutputStream socketWriter = new DataOutputStream(socket.getOutputStream());
             DataInputStream socketReader = new DataInputStream(socket.getInputStream())){

            long fileSize = Files.size(this.path);
            String fileName = this.path.getFileName().toString();
            socketWriter.writeInt(fileName.length());
            socketWriter.writeUTF(fileName);
            ResponseCode filenameTransferResponse = ResponseCode.getResponseByCode(socketReader.readInt());
            if (filenameTransferResponse == ResponseCode.FAILURE_FILENAME_TRANSFER){
                logger.log(Level.SEVERE, "Failure file name transfer");
                return;
            }
            socketWriter.writeLong(fileSize);
            socketWriter.flush();
            byte[] buffer = new byte[BUFFER_SIZE];
            int lineSize;
            MessageDigest hashSum = MessageDigest.getInstance("MD5");
            while ((lineSize = fileReader.read(buffer, 0, BUFFER_SIZE)) > 0){
                socketWriter.write(buffer, 0, lineSize);
                socketWriter.flush();
                hashSum.update(buffer, 0, lineSize);
            }
            socketWriter.writeUTF(hashSumToString(hashSum));
            socketWriter.flush();
            ResponseCode fileTransferResponse = ResponseCode.getResponseByCode(socketReader.readInt());
            if (fileTransferResponse == ResponseCode.FAILURE_FILE_TRANSFER){
                logger.log(Level.SEVERE, "Failure file transfer");
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnknownResponseCode unknownResponseCode) {
            logger.log(Level.SEVERE, "Get unknown code from server");
        }
    }

    private String hashSumToString(MessageDigest md){
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
