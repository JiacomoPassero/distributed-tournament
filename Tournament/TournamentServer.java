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
import java.util.HashMap;
import java.util.stream.Stream;

public class TournamentServer {
    private String name;
    private String ip_address;
    private int port;
    private String local_path;
    protected HashMap<String,TournamentNeighbor> tn;

    public TournamentServer(String name, String ip_address, int port, String local_path, HashMap<String,TournamentNeighbor> tn){
        this.name = name;
        this.ip_address = ip_address;
        this.port = port;
        this.local_path = local_path;
        this.tn = tn;
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
            //apertura server socket
            ss = new ServerSocket(this.port);
            while(terminate){                
                //tentativo stabilire connessione
                //System.out.println("Attesa richiesta...");
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
                    System.out.println("Richiesta creazione file: " + path);
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
                case "join_request":
                    //in questo caso il path è il NOME del nuovo nodo.
                    String request_address = message.split(":")[2];
                    int  request_port = Integer.parseInt(message.split(":")[3]);
                    result = this.insertNewNode(path, request_address, request_port);
                break;
                case "add_me":
                    result = this.addNeighbor(message);
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
    protected String createFile(String file_path) {
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
    protected String deleteFile(String file_path){
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
    protected String readFileLine(String file_path, int offset){
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
                br.close();
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
    protected String writeFileLine(String file_path, String new_line){
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

    private String insertNewNode(String new_node, String new_address, int new_port){
        //controllo che il nome del nodo non sia dublicato
        if(tn.containsKey(new_node)){
            return "nome esistente";
        }

        //per ogni vicino mi serve sapere il nome, l'indirizzo e la porta
        String lista_vicini= "";
        //il separatore tra più nodi è dato da ::
        //inserisco tutti i vicini
        for(String key : tn.keySet()){
            if(!lista_vicini.isEmpty()){
                lista_vicini+="::";
            }
            lista_vicini+=key+":"+tn.get(key).getAddress()+":"+tn.get(key).getPort();
            
        }

        //prima di inviare il risultato inserisco il vicino attuale
        TournamentNeighbor new_n = new TournamentNeighbor(new_address, new_port);
        tn.put(new_node, new_n);

        return lista_vicini;
    }

    //metodo per aggiungere un nuovo nodo alla lista dei vicini
    public String addNeighbor(String message){
        String new_name, new_address;
        int new_port;
        //skip di un indice poichè viene passato il messaggio non soggetto a parsing
        new_name = message.split(":")[1];
        new_address = message.split(":")[2];
        new_port = Integer.parseInt(message.split(":")[3]);
        //prima di inserire verifico l'esistenza del nome
        if(tn.containsKey(new_name))
            return "nodo esistente";

        tn.put(new_name, new TournamentNeighbor(new_address, new_port));
        return "Vicino aggiunto";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return ip_address;
    }

    public void setAddress(String ip_address) {
        this.ip_address = ip_address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
