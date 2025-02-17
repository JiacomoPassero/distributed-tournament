package TournamentRMI;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class TournamentServer extends UnicastRemoteObject implements RMIinterface {
    
    private static String Myserver = "TournamentServer";
    
    public TournamentServer() throws RemoteException {
        super();
    }

    public String overturn(String s) throws RemoteException {
        return new StringBuilder(s).reverse().toString();
    }

    public static void main(String[] args) {
        try {
            TournamentServer server = new TournamentServer();
            if (args.length > 0)
                Myserver = args[0] + Myserver;

            Naming.rebind(Myserver, server);
            Naming.rebind("TournamentServer", server);
            System.out.println("Server is ready.");
        } catch (Exception e) {
            System.out.println("Server failed: " + e);
        }
    }
    
}
