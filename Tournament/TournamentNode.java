package Tournament;
import java.io.*;
import java.net.*;

/*Metodi richiesti: create/delete, Open/close, Read,write ed eventualmente lock */
public class TournamentNode {

    private String client_test;

    private TournamentServer ts;
    private TournamentClient tc;


    //costruttore
    public TournamentNode(String ip_address, int port, String client_test, String local_path){

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

    /*Metodo per agire come server per la creazione di un file */
    public void serverFileOperation(){
        ts.serverFileOperation();
    }
}
