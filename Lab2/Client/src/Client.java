import exceptions.FileDoesNotExist;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client implements Runnable {
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
             BufferedReader fileReader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8);
             BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
             ){

            long fileSize = Files.size(this.path);
            String fileName = this.path.getFileName().toString();
            socketWriter.write(fileName.length() + "\n");
            socketWriter.write(fileName + "\n");
            socketWriter.write(fileSize + "\n");
            socketWriter.flush();
            char[] buffer = new char[BUFFER_SIZE];
            int lineSize;
            while ((lineSize = fileReader.read(buffer, 0, BUFFER_SIZE)) > 0){
                socketWriter.write(buffer, 0, lineSize);
                socketWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
