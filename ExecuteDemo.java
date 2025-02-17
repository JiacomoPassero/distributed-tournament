import Tournament.TournamentNode;

public class ExecuteDemo {
    public static void main(String[] args) {
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");

        System.out.println("Test 1: creazione e cancellazione file");
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

        //uno reverse card
        System.out.println("Test 2: creazione e cancellazione file invertendo i ruoli");
        Thread serverCreateThread_v2 = new Thread(() -> {
            clientNode.serverFileOperation();
        });
        Thread clientCreateThread_v2 = new Thread(() -> {
            serverNode.clientFileOperation("file.txt","create");
        });
        //Test Creazione remota
        try {
            serverCreateThread_v2.start();
            clientCreateThread_v2.start();

            serverCreateThread.join();
            clientCreateThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Test cancellazione remota
         Thread serverDeleteThread_v2 = new Thread(() -> {
            clientNode.serverFileOperation();
        });
        Thread clientDeleteThread_v2 = new Thread(() -> {
            serverNode.clientFileOperation("file.txt","delete");
        });
        try {
            serverDeleteThread_v2.start();
            clientDeleteThread_v2.start();

            serverDeleteThread.join();
            clientDeleteThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}
