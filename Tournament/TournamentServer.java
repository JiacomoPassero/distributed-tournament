package Tournament;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TournamentServer {
    private String ip_address;
    private int port;
    private String local_path;

    public TournamentServer(String ip_address, int port, String local_path){
        this.ip_address = ip_address;
        this.port = port;
        this.local_path = local_path;
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
                    System.out.println("Richiesta cancellazione file: " + path);
                    result = this.deleteFile(path);
                break;
                case "write":
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
    //lettura riga da un file locale
    private String readFileLine(String file_path, int line){
        String result = "";
        file_path = this.local_path + file_path;
        try{
            File file = new File(file_path);
            if(file.exists()){
                //usare un buffer per leggere il file cosi da evitare di leggerlo tutto
                BufferedReader br = Files.newBufferedReader(Paths.get(file_path));
                for(int i=0; i<line; i++){
                    br.readLine();
                }
                //Assegnazioe riga desiderata
                result = br.readLine();
            }else{
                //il file non esiste
                result = "File non esistente";
            }
        }catch(IOException e){
            e.printStackTrace();
            result = "Errore nella lettura del file";
        }
        return result;
    }
    //lettura riga da un file locale
    private String writeFileLine(String file_path, String new_line){
        String result = "";
        file_path = this.local_path + file_path;
        try{
            File file = new File(file_path);
            if(file.exists()){
                //scrittura file in append tramite buffer writer
                FileWriter fw = new FileWriter(file_path, true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(new_line);
                bw.close();
                //Messaggio successo
                result = "Riga aggiunta con successo";
            }else{
                //il file non esiste
                result = "File non esistente";
            }
        }catch(IOException e){
            e.printStackTrace();
            result = "Errore nella scrittura del file";
        }
        return result;
    }
}
