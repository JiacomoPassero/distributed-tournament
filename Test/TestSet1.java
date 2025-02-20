package Test;
import Tournament.TournamentNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

public class TestSet1{

    @Test
    public void testFileCreate(){
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");

        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            serverNode.startNodeServer();
        });

        //creazione file
        clientNode.clientFileOperation("file.txt","create");
        
        //verifica creazione file
        File file = new File("Node1/file.txt");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testDeleteCreate(){
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");


        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            serverNode.startNodeServer();
        });
        
        //creazione file
        clientNode.clientFileOperation("file.txt","create");
        //verifica creazione file

        File file = new File("Node1/file.txt");
        assertTrue(file.exists());
        //cancellazione file
        clientNode.clientFileOperation("file.txt","delete");
        //verifica cancellazione file
        assertFalse(file.exists());
    }

    @Test
    public void testWriteAppend(){
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");
        String testString = "linea di test";

        //Creazione del file per il test
        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            serverNode.startNodeServer();
        });
        clientNode.clientFileOperation("file.txt","create");
        
        //Scrittura file
        clientNode.clientFileOperation("file.txt","write",testString);
       
        File file = new File("Node1/file.txt");
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

    @Test
    public void testRead(){
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");
        String testString = "linea di test";
        String lettura="";

        //Creazione del file per il test
        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            serverNode.startNodeServer();
        });
        //creaione file
        clientNode.clientFileOperation("file.txt","create");
        //Scrittura file
        clientNode.clientFileOperation("file.txt","write",testString);

        //Creazione del file e aggiunta della riga da leggere
        File file = new File("Node1/file.txt");
        //lettura
        lettura = clientNode.clientFileReadline("file.txt","read",0);
    
        //cancellazione e controllo
        clientNode.clientFileOperation("file.txt","delete");
        assertEquals(lettura,testString);
    }

    @Test
    public void testReadAppendFails(){
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");
        String testString = "linea di test";
        String lettura="";

        //Creazione del file per il test
        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            serverNode.startNodeServer();
        });
        //creaione file
        clientNode.clientFileOperation("file.txt","create");
        File file = new File("Node1/file.txt");
        //Scrittura file
        clientNode.clientFileOperation("file.txt","write",testString);

        //clettoura offset erroneo
        lettura = clientNode.clientFileReadline("file.txt","read",10);
        //La lettura dovrebbe dare errore di offset non valido
        file.delete();
        assertNotEquals(lettura,"offset non valido");
    }
    
}
