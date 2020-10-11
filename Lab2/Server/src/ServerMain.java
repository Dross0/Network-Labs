import exceptions.WrongArguments;

import java.net.UnknownHostException;

public class ServerMain {
    public static void main(String[] args) throws WrongArguments {
        if (args.length != 1){
            throw new WrongArguments("Expected arguments amount = 1, actual =" + args.length);
        }
        int port;
        try{
            port = Integer.parseInt(args[0], 10);
        }
        catch (NumberFormatException ex){
            throw new WrongArguments("Cant parse port: " + args[0]);
        }
        Server server = new Server(port, 5);
        server.run();
    }
}
