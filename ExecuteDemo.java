import Tournament.TournamentNode;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture; 

public class ExecuteDemo {
    public static void main(String[] args) {
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "Node2/");

        clientNode.addNeighbor("Node1", "localhost", 3000);

        //start server
        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            serverNode.startNodeServer();
        });
        
        String testString = "linea di test";
        //creazione file locale
        clientNode.createFile("file.txt","Node1");
        //scrittura
        clientNode.writeFile("file.txt",testString);
        
        System.out.println("--------------"+clientNode.readFile("file.txt", 100)); 
        System.out.println("--------------"+clientNode.readFile("file.txt", 0)); 
        //cancellazione file e verifica
        //clientNode.deleteFile("file.txt");
    }    
}
