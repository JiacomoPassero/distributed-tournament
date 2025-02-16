import java.io.*;
import java.net.*;

/*Metodi richiesti: create/delete, Open/close, Read,write ed eventualmente lock */
public class TournamentNode {

    private String ip_address;
    private int port;

    //costruttore
    public TournamentNode(String ip_address, int port) {
        this.ip_address = ip_address;
        this.port = port;
    }   
    /*Metodo per agire come client per la creazione di un file */
    public void clientCreateFile(String s_address, int s_port, String path){ 
        try {
            Socket s = new Socket(s_address, s_port);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            //invio messaggio
            oos.writeObject(path);
            oos.flush();
            //ricezione risposta
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

    /*Metodo per agire come server per la creazione di un file */
    public void serverCreateFile(){
        try {
            ServerSocket ss;
            Socket clientSocket;

            //stabilire connessione
            ss = new ServerSocket(this.port);
            System.out.println("Attesa richiesta creazione file...");
            clientSocket = ss.accept();

            System.out.print("Richiesta creazione file: ");
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            String file_path = (String)ois.readObject();
            System.out.println(file_path);

            //creazione file
            String result = "";
            try{
                File file = new File(file_path);
                if(file.createNewFile()){
                    result = "File creato con successo";
                }else{
                    result = "File già esistente";
                }
            }catch(IOException e){
                e.printStackTrace();
                result = "Errore nella creazione del file";
            }

            //invio messaggio stringa contenente il risultato
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.writeObject(result);
            oos.flush();
            System.out.println("Operazione completata");
            
            // chiusura socket
            clientSocket.close();
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
     /*Metodo per agire come client per la creazione di un file */
     public void clientDeleteFile(String s_address, int s_port, String path){ 
        try {
            Socket s = new Socket(s_address, s_port);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            //invio messaggio
            oos.writeObject(path);
            oos.flush();
            //ricezione risposta
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

    /*Metodo per agire come server per la creazione di un file */
    public void serverDeleteFile(){
        try {
            ServerSocket ss;
            Socket clientSocket;

            //stabilire connessione
            ss = new ServerSocket(this.port);
            System.out.println("Attesa richiesta creazione file...");
            clientSocket = ss.accept();

            System.out.print("Richiesta creazione file: ");
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            String file_path = (String)ois.readObject();
            System.out.println(file_path);

            //creazione file
            String result = "";
            try{
                File file = new File(file_path);

                if(file.exists()){ 
                    if(file.delete()){
                        result = "File cancellato con successo";
                    }else{
                        result = "File non cancellato";
                    }
                }else{
                    result = "File non esistente";
                }
            }catch(Exception e){
                e.printStackTrace();
                result = "Errore nella cancellazione del file";
            }

            //invio messaggio stringa contenente il risultato
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.writeObject(result);
            oos.flush();
            System.out.println("Operazione completata");
            
            // chiusura socket
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
