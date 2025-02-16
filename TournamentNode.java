import java.io.*;
import java.net.*;

public class TournamentNode {

    private String ip_address;
    private int port;

    //costruttore
    public TournamentNode(String ip_address, int port) {
        this.ip_address = ip_address;
        this.port = port;
    }   

    public void clientCreateFile(String path){ 
        try {
            Socket s = new Socket("localhost", 3000);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            //invio messaggio
            oos.writeObject("CIAO coestai?");
            oos.flush();
            //ricezione risposta
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            String result = (String)ois.readObject();
            System.out.println("Client ricevuto: "+result);

            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void serverCreateFile(String path){
        try {
            ServerSocket ss;
            Socket clientSocket;

            //stabilire connessione
            ss = new ServerSocket(3000);
            System.out.println("Server avviato e in attesa...");
            clientSocket = ss.accept();

            System.out.print("Server ha ricevuto una richiesta: ");
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            String par = (String)ois.readObject();
            System.out.println(par);
            StringBuilder rev = new StringBuilder(par).reverse();
            
            String result = rev.toString();
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

            System.out.println("Server invia " +result);
            oos.writeObject(result);
            oos.flush();
            System.out.println("Server fatto!");
            
            clientSocket.close();
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /*Metodi richiesti: create/delete, Open/close, Read,write ed eventualmente lock */

}
