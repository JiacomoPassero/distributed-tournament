package TournamentRMI;

import java.rmi.*;

public class TournamentClient {
    public static void main(String args[]) {
        String ServerStr = "overturner";
        String message = "ciao";

        if (args.length > 0)
            ServerStr = "//" + args[0] + "/" + ServerStr;
            System.out.println("ServerStr: " + ServerStr);

        try {
            TournamentServer server = (TournamentServer) Naming.lookup(ServerStr);
            message = server.overturn(message);
            System.out.println("Message Received: " + message);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
