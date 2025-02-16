public class ExecuteDemo {
    public static void main(String[] args) {
        TournamentNode clientNode = new TournamentNode("localhost", 3000);
        TournamentNode serverNode = new TournamentNode("localhost", 3000);

        Thread serverThread = new Thread(() -> {
            serverNode.serverCreateFile(null);
        });
        Thread clientThread = new Thread(() -> {
            clientNode.clientCreateFile(null);
        });

        serverThread.start();
        clientThread.start();
    }
    
}
