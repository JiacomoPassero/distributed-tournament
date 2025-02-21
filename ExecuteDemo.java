import Tournament.TournamentNode;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture; 

public class ExecuteDemo {
    public static void main(String[] args) {
        int num_nodes = 5;
        ArrayList<TournamentNode> nodes = new ArrayList<TournamentNode>();

        //creazione nodi
        for(int i =0; i<num_nodes; i++){
            nodes.add(new TournamentNode("Node"+i, "localhost", 3000+i, "Node"+i+"/"));
        }

        //creazione grafo completo
        for(int i=0; i<num_nodes; i++){
            for(int j=0; j<num_nodes; j++){
                if(i!=j){
                    nodes.get(i).addNeighbor("Node"+j, "localhost", 3000+j);
                }
            }
        }
        
        //start servers
        for(int i=0; i<num_nodes; i++){
            int il = i;
            CompletableFuture.runAsync(() -> { 
                // Handle the request 
                nodes.get(il).startNodeServer();
            });
        }
        //node 5 test join
        TournamentNode node5 = new TournamentNode("Node5", "localhost", 3005, "Node5/");
        //questo node join fallisce perchè non ci sono vicini da visitare
        System.out.println(node5.tournamentJoin());
        //aggiungo un vicino ad node2
        node5.addNeighbor("Node0", "localhost", 3000);
        
        //questo node join ha successo perchè si ha accesso ad un nodo nella rete
        System.out.println(node5.tournamentJoin());
        System.out.println("Vicini node 5");
        node5.printNeighbors();

        //test operazioni
        for(int i =0; i<num_nodes; i++){
            node5.createFile("file"+i+".txt", "Node"+i);
            node5.writeFile("file"+i+".txt", "testo di prova "+i);
            System.out.println(node5.readFile("file"+i+".txt", 0));
            node5.deleteFile("file"+i+".txt");
        }
    }    

}
