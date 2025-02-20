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
import java.util.stream.Stream;

public class TournamentServer {
    private String ip_address;
    private int port;
    private String local_path;

    public TournamentServer(String ip_address, int port, String local_path){
        this.ip_address = ip_address;
        this.port = port;
        this.local_path = local_path;
    }

    //metodo per avviare il server in modo che resti in attesa di una richiesta, la esegua e passi ad attendere la successiva:
    public void serverStart(){
        //variabile per mantenere il server in esecuzione
        boolean terminate = true;
        String message, result;
        //Socket per la connessione
        ServerSocket ss;
        Socket clientSocket;
        //Stream per connessione
        ObjectInputStream ois;
        ObjectOutputStream oos;

        //ciclo di attività del server
        try{
            ss = new ServerSocket(this.port);
            while(terminate){
                //apertura del server socket
                
                //tentativo stabilire connessione
                System.out.println("Attesa richiesta creazione file...");
                clientSocket = ss.accept();

                //estrazione del messaggio
                ois = new ObjectInputStream(clientSocket.getInputStream());
                message = (String)ois.readObject();

                //soddisfazione richiesta
                result = this.serverFileOperation(message);

                //invio risposta col risultato o il messaggio di errore
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                oos.writeObject(result);
                oos.flush();
                System.out.println("Operazione completata");

                //chiudo il client socket
                clientSocket.close();
                
            }
            //terminata l'esecuzione chiudo il server socket
            ss.close();
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Errore nell'avvio del server");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
            System.out.println("Errore nella lettura del messaggio");
        }
    }

    /*Metodo per agire come server per la creazione di un file */
    public String serverFileOperation(String message){
        String result="";
        try {
            //parsing messaggio
            String operation = message.split(":")[0];
            String path = message.split(":")[1];            

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
                    //in caso l'opzione sia write il messaggio contiene un ulteriore campo
                    String new_line = message.split(":")[2];
                    result = this.writeFileLine(path, new_line);
                break;
                case "read":
                    //in caso di read (PER ORA) viene passato un offset che indica la riga da leggere
                    int offset = Integer.parseInt(message.split(":")[2]);
                    result = this.readFileLine(path, offset);
                break;
                case "exist":
                    //controlla se il file esiste localmente
                    result += this.existLocalFile(path);
                break;
                default:
                    System.out.println("Operazione non valida");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
    private String readFileLine(String file_path, int offset){
        String result = "";
        file_path = this.local_path + file_path;
        try{
            File file = new File(file_path);
            if(file.exists()){
                //controllo che l'offset sia valido (non è molto bello aprire u filestream solo per questo, potrebbe valer la pena
                //riscrivere la lettura)
                Stream<String> fileStream = Files.lines(Paths.get(file_path));
                int lines_number = (int) fileStream.count();
                fileStream.close();
                if(offset >= lines_number || offset < 0){
                    return "Offset non valido";
                }
                
                //Se l'offset è valido procedo a leggere la riga del file richiesto
                //usare un buffer per leggere il file cosi da evitare di leggerlo tutto
                BufferedReader br = Files.newBufferedReader(Paths.get(file_path));
                for(int i=0; i<offset; i++){
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

    //scrittura riga da un file locale
    private String writeFileLine(String file_path, String new_line){
        String result = "";
        file_path = this.local_path + file_path;
        try{
            File file = new File(file_path);
            if(file.exists()){
                //scrittura file in append tramite buffer writer
                FileWriter fw = new FileWriter(file_path, true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(new_line + "\n");
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

    //controlla se un file locale passato esiste
    public boolean existLocalFile(String file_path){
        file_path = this.local_path + file_path;
        File file = new File(file_path);
        return file.exists();
    }
}
