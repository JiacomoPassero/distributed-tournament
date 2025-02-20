import Tournament.TournamentNode;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture; 

public class ExecuteDemo {
    public static void main(String[] args) {
        //creo una lista di nodi
        TournamentNode clientNode = new TournamentNode("localhost", 3005, "localhost:3000", "Node5/");
        ArrayList<TournamentNode> nodes = new ArrayList<TournamentNode>();

        for(int i = 0 ; i < 5; i++){
            nodes.add(new TournamentNode("localhost", 3000+i, "localhost:3000", "Node" + i + "/"));
        }

        //Collego il client ai nodi
        for(int i = 0 ; i < 5; i++){
            clientNode.addNeighbor("Node" + i, "localhost", 3000+i);
        }
        //Avvio i server dei nodi
        for(int i = 0 ; i < 5; i++){
            int ii = i;
            CompletableFuture.runAsync(() -> { 
                nodes.get(ii).startNodeServer();
            });
        }

        //test creazione file
        for(int i = 0 ; i < 5; i++){
            clientNode.clientFileOperation("file"+i+".txt", "create");
        }

/*         CompletableFuture.runAsync(() -> { 
            // Handle the request 
            serverNode.startNodeServer();
        });

        //creazione file
        clientNode.clientFileOperation("file.txt","create");
        //eliminazione file
        clientNode.clientFileOperation("file.txt","delete"); */

    }    
}
