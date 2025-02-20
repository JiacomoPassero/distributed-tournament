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
                    return tn.get(key).getAddress() + ":" + tn.get(key).getPort();
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

    /*Metodo per agire come client per la creazione di un file */
    public void clientFileOperation(String path, String operation){ 
        //locate file
        String location = this.locate(path);
        if(location.equals("not found")){
            System.out.println("File non trovato");
            return;
        }
        //parsing location
        String s_address = location.split(":")[0];
        int s_port = Integer.parseInt(location.split(":")[1]);

        tc.clientFileOperation(path, operation, s_address, s_port);
    }

    //overloding del metodo da invocare in caso di write che a si che venga aggiunto un parametro in più alla stringa da inviare
    public void clientFileOperation(String path, String operation, String new_line){ 
        //locate file
        String location = this.locate(path);
        if(location.equals("not found")){
            System.out.println("File non trovato");
            return;
        }
        //parsing location
        String s_address = location.split(":")[0];
        int s_port = Integer.parseInt(location.split(":")[1]);

        //aggiungo la nuova linea da scrivere nel file in modo che finisca in coda al comando
        path = path + ":" + new_line;

        tc.clientFileOperation(path, operation, s_address, s_port);
    }

    //versione del metodo per invocare una read che deve quindi restituire un valore letto
    public String clientFileReadline(String path, String operation, int offset){ 
        //locate file
        String location = this.locate(path);
        if(location.equals("not found")){
            System.out.println("File non trovato");
            return "";
        }
        //parsing location
        String s_address = location.split(":")[0];
        int s_port = Integer.parseInt(location.split(":")[1]);
        String result;

        result = tc.clientFileReadline(path, operation, offset, s_address, s_port);

        return result;
    }

    /*Metodo per agire come server */
    public void startNodeServer(){
        ts.serverStart();
    }
}
