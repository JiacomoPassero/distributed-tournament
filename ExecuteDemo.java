import Tournament.TournamentNode;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture; 

public class ExecuteDemo {
    public static void main(String[] args) {
        TournamentNode node0 = new TournamentNode("Node0", "localhost", 3000, "Node0/");
        TournamentNode node1 = new TournamentNode("Node1", "localhost", 3001, "Node1/");
        TournamentNode node2 = new TournamentNode("Node2", "localhost", 3002, "Node2/");
        TournamentNode node3 = new TournamentNode("Node3", "localhost", 3003, "Node3/");
        
        //vicinato nodo 0
        node0.addNeighbor("Node1", "localhost", 3001);
        node0.addNeighbor("Node2", "localhost", 3002);
        //vicinato nodo 1
        node1.addNeighbor("Node0", "localhost", 3000);
        node1.addNeighbor("Node2", "localhost", 3002);
        //vicinato nodo 2
        node2.addNeighbor("Node0", "localhost", 3000);
        node2.addNeighbor("Node1", "localhost", 3001);


        //start servers
        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            node0.startNodeServer();
        });
        //start server
        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            node1.startNodeServer();
        });
        //start server
        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            node2.startNodeServer();
        });
                //start server
        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            node3.startNodeServer();
        });
       
        System.out.println(node3.tournamentJoin());
        //aggiungo un vicino ad node2
        node3.addNeighbor("Node0", "localhost", 3000);
        System.out.println(node3.tournamentJoin());
       
    }    
}
