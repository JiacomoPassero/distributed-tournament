package Tournament;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TournamentClient {
    //private String client_test;

    public TournamentClient(String client_test){
        //this.client_test = client_test;
    }

    /*Metodo per agire come client per la creazione di un file */
    public void clientFileOperation(String path, String operation, String s_address, int s_port){ 
        try {
            String message = operation + ":" + path;

            //apertura socket server
            Socket s = new Socket(s_address, s_port);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

            //invio messaggio
            oos.writeObject(message);
            oos.flush();

            //ricezione risposta e output risultato
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            String result = (String)ois.readObject();
            System.out.println("Risultato operazione: " + result);

            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String clientFileReadline(String path, String operation, int offset,String s_address, int s_port){ 
        String result = "";
        try {
            //composizione messaggio
            String message = operation + ":" + path + ":" + offset;

            //apertura socket server
            Socket s = new Socket(s_address, s_port);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

            //invio messaggio
            oos.writeObject(message);
            oos.flush();

            //ricezione risposta e output risultato
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
             
            result = (String)ois.readObject();
            System.out.println("Risultato operazione: " + result);

            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

}
