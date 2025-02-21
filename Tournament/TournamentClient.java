package Tournament;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TournamentClient {

    public TournamentClient(){

    }

    public String sendRequest(String message, String s_address, int s_port){
        String result = "";
        try {
            //apertura socket server
            Socket s = new Socket(s_address, s_port);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

            //invio messaggio
            oos.writeObject(message);
            oos.flush();

            //ricezione risposta e output risultato
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            result = (String)ois.readObject();
            //chiusura socket
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean clientFileSearch(String path, String s_address, int s_port){ 
        //nel caso avvenga un errore di default il file non viene trovato
        String result = "" + false;
        try {
            //composizione messaggio
            String message = "exist" + ":" + path;

            //apertura socket server
            Socket s = new Socket(s_address, s_port);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

            //invio messaggio
            oos.writeObject(message);
            oos.flush();

            //ricezione risposta e output risultato
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
             
            result = (String)ois.readObject();

            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return Boolean.parseBoolean(result);
    }

}
