package Test;
import Tournament.TournamentNode;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Test;

public class TestSet1{

    @Test
    public void testFileCreate(){
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");

        Thread serverCreateThread = new Thread(() -> {
            serverNode.serverFileOperation();
        });
        Thread clientCreateThread = new Thread(() -> {
            clientNode.clientFileOperation("file.txt","create");
        });
        //Test Creazione file remota
        try {
            serverCreateThread.start();
            clientCreateThread.start();

            serverCreateThread.join();
            clientCreateThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //verifica creazione file
        File file = new File("Node1/file.txt");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testDeleteCreate(){
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");

        File file = new File("Node1/file.txt");
        try{
            file.createNewFile();
        }catch(Exception e){
            e.printStackTrace();
            fail();
        }
        //Cancellazione file
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
        
        assertFalse(file.exists());
    }

    @Test
    public void testWriteAppend(){
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");
        String testString = "linea di test";

        File file = new File("Node1/file.txt");
        try{
            file.createNewFile();
        }catch(Exception e){
            e.printStackTrace();
            fail();
        }
        //Scrittura file
        Thread serverWriteThread = new Thread(() -> {
            serverNode.serverFileOperation();
        });
        Thread clientWriteThread = new Thread(() -> {
            clientNode.clientFileOperation("file.txt","write",testString);
        });
        //Creazione file remota 
        try {
            serverWriteThread.start();
            clientWriteThread.start();

            serverWriteThread.join();
            clientWriteThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //verifica che il file contenga la riga scritta 
        boolean check = false;
        try{
            BufferedReader bf = new BufferedReader(new FileReader("Node1/file.txt"));
            String line = bf.readLine();

            check = line.equals(testString);
            bf.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        file.delete();
        assertTrue(check);
    }
    
}
