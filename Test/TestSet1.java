package Test;
import Tournament.TournamentNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

public class TestSet1{

    @Test
    public void testFileCreateLocale(){
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3001", "Node2/");

        //creazione file locale
        clientNode.createFile("file.txt","local");
        
        //verifica creazione file locale
        File file = new File("Node2/file.txt");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testFileCreateRemoto(){
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");

        clientNode.addNeighbor("Node1", "localhost", 3000);

        //start server
        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            serverNode.startNodeServer();
        });
        //creazione file remoto
        System.out.println(clientNode.createFile("file.txt","Node1"));
        
        //verifica creazione file remoto
        File file = new File("Node1/file.txt");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testDeleteLocale(){
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3001", "Node2/");

        //creazione file locale
        clientNode.createFile("file.txt","local");
        clientNode.deleteFile("file.txt");
        //verifica creazione file locale
        File file = new File("Node2/file.txt");
        assertFalse(file.exists());
    }

    @Test
    public void testDeleteRemoto(){
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");

        clientNode.addNeighbor("Node1", "localhost", 3000);

        //start server
        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            serverNode.startNodeServer();
        });
        //creazione file remoto
        clientNode.createFile("file.txt","Node1");

        //cancellazione e verifica remoto
        clientNode.deleteFile("file.txt");
        File file = new File("Node2/file.txt");
        assertFalse(file.exists());
    }

    @Test
    public void testReadWriteAppendLocale(){
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");
        
        String testString = "linea di test";
        //creazione file locale
        clientNode.createFile("file.txt","local");
        //scrittura
        clientNode.writeFile("file.txt",testString);
        
        //lettura linea inesistente e scritta
        String lettura_erronea = clientNode.readFile("file.txt", 100);
        String lettura = clientNode.readFile("file.txt",0);

        //cancellazione file e verifica
        clientNode.deleteFile("file.txt");
        assertEquals(testString, lettura);
        assertEquals(lettura_erronea, "Offset non valido");
    }

    @Test
    public void testReadWriteAppendRemoto(){
        TournamentNode serverNode = new TournamentNode("localhost", 3000, "localhost:3001", "Node1/");
        TournamentNode clientNode = new TournamentNode("localhost", 3001, "localhost:3000", "Node2/");

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
        
        //lettura linea inesistente e scritta
        String lettura_erronea = clientNode.readFile("file.txt", 100);
        String lettura = clientNode.readFile("file.txt",0);

        //cancellazione file e verifica
        clientNode.deleteFile("file.txt");
        assertEquals(testString, lettura);
        assertEquals(lettura_erronea, "Offset non valido");
    }    
}
