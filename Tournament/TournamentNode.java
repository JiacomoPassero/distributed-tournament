package Tournament;

/*Metodi richiesti: create/delete, Read,write ed eventualmente lock */
public class TournamentNode{
    private String client_test;

    private TournamentServer ts;
    private TournamentClient tc;


    //costruttore
    public TournamentNode (String ip_address, int port, String client_test, String local_path){

        ts = new TournamentServer(ip_address, port, local_path);
        tc = new TournamentClient(client_test);
        //valore di test in attesa di implementazione metodo locate
        this.client_test = client_test;
    }  

    //Metodo per rintraggiare un file tra i vari nodi, prima controlla localmento, poi sulla rete
    public String locate(String filename) {
        //logica ricerca su rete
        return client_test;
    }

    /*Metodo per agire come client per la creazione di un file */
    public void clientFileOperation(String path, String operation){ 
        //locate file
        String location = this.locate(path);
        //parsing location
        String s_address = location.split(":")[0];
        int s_port = Integer.parseInt(location.split(":")[1]);

        tc.clientFileOperation(path, operation, s_address, s_port);
    }

    //overloding del metodo da invocare in caso di write che da si che venga aggiunto un parametro in più alla stringa da inviare
    public void clientFileOperation(String path, String operation, String new_line){ 
        //locate file
        String location = this.locate(path);
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
        //parsing location
        String s_address = location.split(":")[0];
        int s_port = Integer.parseInt(location.split(":")[1]);
        String result;

        result = tc.clientFileReadline(path, operation, offset, s_address, s_port);

        return result;
    }

    /*Metodo per agire come server TODO: renderlo più di un one shot approach*/
    public void startNodeServer(){
        ts.serverStart();
    }
}
