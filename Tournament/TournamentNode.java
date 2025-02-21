package Tournament;

import java.util.HashMap;

/*Metodi richiesti: create/delete, Read,write ed eventualmente lock */
public class TournamentNode{
    private String client_test;

    private TournamentServer ts;
    private TournamentClient tc;
    private HashMap<String,TournamentNeighbor> tn;


    //costruttore
    public TournamentNode (String ip_address, int port, String client_test, String local_path){
        ts = new TournamentServer(ip_address, port, local_path);
        tc = new TournamentClient(client_test);
        tn = new HashMap<String,TournamentNeighbor>();
        //valore di test in attesa di implementazione metodo locate
        this.client_test = client_test;
    }  

    /*Metodo per rintraggiare un file tra i vari nodi 
     * 1 - controllo se il file esiste localmente
     * 2 - controllo se il file esiste in un nodo vicino
    */
    public String locate(String file) {
        //se esiste localmente restituisco local
        String result = "not found";
        if(ts.existLocalFile(file)){
            return "local";
        }
        else{
            //altrimenti per ogni vicino controllo se esiste, restituendo nel caso l'indirizzo del nodo in formato address:port
            for (String key : tn.keySet()) {

                if(tc.clientFileSearch(file, tn.get(key).getAddress(), tn.get(key).getPort())){
                    //System.out.println("File trovato in: " + tn.get(key).getAddress() + ":" + tn.get(key).getPort());
                    return key;
                }
                

            }
        }
        return result;
    }

    //aggiungere un vicino
    public void addNeighbor(String name, String address, int port){
        TournamentNeighbor n = new TournamentNeighbor(address, port);
        tn.put(name, n);
    }

    //rimuovere un vicino
    public void removeNeighbor(String name){
        tn.remove(name);
    }

    //Metodo per la creazione di un file, è necessario specificare su quale nodo si desidera salvare il file
    public String createFile(String path, String location){
        //controllo se il nodo è presente localmente
        if(location.equals("local")){
            //sfrutto le operazioni del server che gestisce anche il path relativo
            return ts.createFile(path);
        }
        //se la creazione non è locale l'indirizzo e la porta del server del nodo vengono presi dalla lista dei vicini
        //prima controllo esistenza del nodo
        if(!this.tn.containsKey(location)){
            return "Nodo non presente";
        }

        String s_addres = this.tn.get(location).getAddress();
        int s_port = this.tn.get(location).getPort();
        String message = "create"+":"+path;

        return this.tc.sendRequest(message, s_addres, s_port);
    }

     //Metodo per la cancellazione di un file di un file, è necessario specificare su quale nodo si desidera salvare il file
     public String deleteFile(String path){
        //locate del nodo
        String location = this.locate(path);
        //se il nodo non è presente lo segnalo
        if(location.equals("not found")){
            return location;
        }

        //se localce cancello tramite ts
        if(location.equals("local")){
            return ts.deleteFile(path);
        }

        //il nodo è presente in un altro nodo del sistema
        String s_addres = this.tn.get(location).getAddress();
        int s_port = this.tn.get(location).getPort();
        String message = "delete"+":"+path;

        return this.tc.sendRequest(message, s_addres, s_port);
    }

    public String writeFile(String path, String new_line){
        //locate del nodo
        String location = this.locate(path);
        //se il nodo non è presente lo segnalo
        if(location.equals("not found")){
            return location;
        }

        //se localce cancello tramite ts
        if(location.equals("local")){
            return ts.writeFileLine(path, new_line);
        }

        //il nodo è presente in un altro nodo del sistema
        String s_addres = this.tn.get(location).getAddress();
        int s_port = this.tn.get(location).getPort();
        //il messaggio contine un parametro in più
        String message = "write"+":"+path+":"+new_line;

        return this.tc.sendRequest(message, s_addres, s_port);
    } 

    public String readFile(String path, int line){
        //locate del nodo
        String location = this.locate(path);
        //se il nodo non è presente lo segnalo
        if(location.equals("not found")){
            return location;
        }

        //se localce cancello tramite ts
        if(location.equals("local")){
            return ts.readFileLine(path, line);
        }

        //il nodo è presente in un altro nodo del sistema
        String s_addres = this.tn.get(location).getAddress();
        int s_port = this.tn.get(location).getPort();
        //il messaggio contine un parametro in più
        String message = "read"+":"+path+":"+line;

        return this.tc.sendRequest(message, s_addres, s_port);
    }

    /*Metodo per agire come server */
    public void startNodeServer(){
        ts.serverStart();
    }
}
