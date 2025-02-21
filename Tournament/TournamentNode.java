package Tournament;
import java.util.HashMap;

/*Metodi richiesti: create/delete, Read,write ed eventualmente lock */
public class TournamentNode{

    private TournamentServer ts;
    private TournamentClient tc;
    protected HashMap<String,TournamentNeighbor> tn;

    //costruttore
    public TournamentNode (String name, String ip_address, int port, String local_path){
        tn = new HashMap<String,TournamentNeighbor>();
        ts = new TournamentServer(name, ip_address, port, local_path, this.tn);
        tc = new TournamentClient();
        //valore di test in attesa di implementazione metodo locate
    }

    /*Metodo per agire come server */
    public void startNodeServer(){
        ts.serverStart();
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

    //aggiungere un un vicino al nodo che invoca il metodo (non crea il link perchè manca dall'altra parte)
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

    //Metodo inserire il nodo in una rete, invia una richiesta ad un vicino che conosce
    //vengono trasferiti tutti i vicini nel nodo richiedente 
    //Si invia una richiesta ad ogni nuovo nodo vicino per richiedere di essere aggiunti 
    public String tournamentJoin(){
        String result = "";

        if(this.tn.isEmpty()){
            return "Nessun vicino a cui chiedere l'ingresso";
        }
        //se c'è almeno un vicino lo estraggo per richiedere l'invio della lista dei suoi vicini
        //estraggo la prima chiave, essendo su un grafo completo non importa quale
        String join_point = this.tn.keySet().iterator().next();

        //costruzione messaggio richiesta
        String s_addres = this.tn.get(join_point).getAddress();
        int s_port = this.tn.get(join_point).getPort();
        //il messaggio contine un parametro in più
        String message = "join_request"+":"+this.ts.getName()+":"+this.ts.getAddress()+":"+this.ts.getPort();

        result = this.tc.sendRequest(message, s_addres, s_port);
        
        //il nome del nodo deve essere unico nella rete
        if(result == "nome esistente")
            return "Operazione fallita: "+result;
        
        //se il nome è univoco ho ricevuto la lista degli ID in formato stringa nel formato
        //nome:indirizzo:porta::nome:indirizzo:porta....
        //Trasformo la stringa in un array di stringhe
        String[] nodes = result.split("::");

        //inserisco i nuovi nodi
        String new_name, new_address, message_add_me;
        int new_port;
        String nodi_non_inseriti="";
        for(String node : nodes){
            new_name = node.split(":")[0];
            new_address = node.split(":")[1];
            new_port = Integer.parseInt(node.split(":")[2]);
            
            this.tn.put(new_name, new TournamentNeighbor(new_address, new_port));
            //Invio anche una richiesta di essere aggiunto come vicino
            message_add_me = "add_me"+":"+this.ts.getName()+":"+this.ts.getAddress()+":"+this.ts.getPort();
            //System.out.println();
            result = this.tc.sendRequest(message_add_me, new_address, new_port);

            if(result.equals("nodo esistente")){
                if(!nodi_non_inseriti.isEmpty()){
                     nodi_non_inseriti += "::";
                }
                nodi_non_inseriti += new_name;
            }
    
        }

        if(!nodi_non_inseriti.isEmpty())
            return "Node join completato. Errore nell'inseriento dei seguenti nodi:" + nodi_non_inseriti;

        else{
            
        }
        return "Node join completato";
    }

    public void printNeighbors(){
        for (String key : tn.keySet()) {
            System.out.println(key+":"+ this.tn.get(key).getAddress()+":"+this.tn.get(key).getPort());
        }
    }

}
