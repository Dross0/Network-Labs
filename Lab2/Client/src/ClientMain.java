import exceptions.FileDoesNotExist;
import exceptions.WrongArguments;


import java.io.IOException;
import java.net.InetAddress;


public class ClientMain {
    public static void main(String[] args) throws IOException, FileDoesNotExist, WrongArguments {
        if (args.length != 3){
            throw new WrongArguments("Expected arguments amount = 3, actual = " + args.length);
        }
        String fileName = args[0];
        String hostName = args[1];
        int port;
        try{
            port = Integer.parseInt(args[2], 10);
        }
        catch (NumberFormatException ex){
            throw new WrongArguments("Cant parse port: " + args[2]);
        }
        Client client = new Client(fileName, InetAddress.getByName(hostName), port);
        client.run();
    }
}
