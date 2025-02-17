package Tournament;
import java.io.*;
import java.net.*;

/*Metodi richiesti: create/delete, Open/close, Read,write ed eventualmente lock */
public class TournamentNode {

    private String ip_address;
    private int port;
    private String client_test;
    private String local_path;

    //costruttore
    public TournamentNode(String ip_address, int port, String client_test, String local_path){
        this.ip_address = ip_address;
        this.port = port;
        this.client_test = client_test;
        this.local_path = local_path;
    }  

    //Metodo per rintraggiare un file tra i vari nodi, prima controlla localmento, poi sulla rete
    public String locate(String filename) {
        //logica ricerca su rete
        return client_test;
    }

    /*Metodo per agire come client per la creazione di un file */
    public void clientFileOperation(String path, String operation){ 
        try {
            //locate file
            String location = this.locate(path);
            //parsing location
            String s_address = location.split(":")[0];
            int s_port = Integer.parseInt(location.split(":")[1]);
            //composizione messaggio le cui componenti sono divise da :
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

    /*Metodo per agire come server per la creazione di un file */
    public void serverFileOperation(){
        try {
            //Creazione socket
            ServerSocket ss;
            Socket clientSocket;

            //stabilire connessione
            ss = new ServerSocket(this.port);
            System.out.println("Attesa richiesta creazione file...");
            clientSocket = ss.accept();

            //parsing messaggio
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            String message = (String)ois.readObject();
            String operation = message.split(":")[0];
            String path = message.split(":")[1];
            String result="";

            switch (operation) {
                case "create":
                    System.out.print("Richiesta creazione file: " + path);
                    result = this.createFile(path);
                    break;
                case "delete":
                    System.out.print("Richiesta cancellazione file: " + path);
                    result = this.deleteFile(path);
                break;
                default:
                    System.out.println("Operazione non valida");
                    break;
            }
            
            //invio messaggio risultato
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
    //creazione file locale
    private String createFile(String file_path) {
        //creazione file
        String result = "";
        file_path = this.local_path + file_path;
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
        return result;
    }
    //cancellazione file locale
    private String deleteFile(String file_path){
        file_path = this.local_path + file_path;
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
        return result;
    }
}
