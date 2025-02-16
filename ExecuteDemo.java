public class ExecuteDemo {
    public static void main(String[] args) {
        TournamentNode serverNode = new TournamentNode("localhost", 3000);
        TournamentNode clientNode = new TournamentNode("localhost", 3001);

        Thread serverThread = new Thread(() -> {
            serverNode.serverCreateFile();
        });
        Thread clientThread = new Thread(() -> {
            clientNode.clientCreateFile("localhost", 3000, "file.txt");
        });

        serverThread.start();
        clientThread.start();
    }
    
}
