package TournamentRMI;
import java.rmi.Remote;

public interface RMIinterface extends java.rmi.Remote {
    String overturn(String s) throws java.rmi.RemoteException;
}
