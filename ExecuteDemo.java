public class ExecuteDemo {
    public static void main(String[] args) {
        TournamentNode serverNode = new TournamentNode("localhost", 3000);
        TournamentNode clientNode = new TournamentNode("localhost", 3001);

        Thread serverCreateThread = new Thread(() -> {
            serverNode.serverCreateFile();
        });
        Thread clientCreateThread = new Thread(() -> {
            clientNode.clientCreateFile("localhost", 3000, "file.txt");
        });
        Thread serverDeleteThread = new Thread(() -> {
            serverNode.serverDeleteFile();
        });
        Thread clientDeleteThread = new Thread(() -> {
            clientNode.clientDeleteFile("localhost", 3000, "file.txt");
        });

        //synchronization barrier
        try {
            serverCreateThread.start();
            clientCreateThread.start();

            serverCreateThread.join();
            clientCreateThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
