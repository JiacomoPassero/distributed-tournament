import Tournament.TournamentNode;

public class ExecuteDemo {
    public static void main(String[] args) {
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");

        Thread serverCreateThread = new Thread(() -> {
            serverNode.serverFileOperation();
        });
        Thread clientCreateThread = new Thread(() -> {
            clientNode.clientFileOperation("file.txt","create");
        });
        //Test Creazione remota
        try {
            serverCreateThread.start();
            clientCreateThread.start();

            serverCreateThread.join();
            clientCreateThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Test cancellazione remota
        Thread serverDeleteThread = new Thread(() -> {
            serverNode.serverFileOperation();
        });
        Thread clientDeleteThread = new Thread(() -> {
            clientNode.clientFileOperation("file.txt","delete");
        });
        try {
            serverDeleteThread.start();
            clientDeleteThread.start();

            serverDeleteThread.join();
            clientDeleteThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}
