import Tournament.TournamentNode;
import java.util.concurrent.CompletableFuture; 

public class ExecuteDemo {
    public static void main(String[] args) {
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");

        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            serverNode.startNodeServer();
        });

        //creazione file
        clientNode.clientFileOperation("file.txt","create");
        //eliminazione file
        clientNode.clientFileOperation("file.txt","delete");

    }    
}
