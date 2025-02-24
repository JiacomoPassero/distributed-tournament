package Test;
import Tournament.TournamentNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

public class TestSet1{

    @Test
    public void testFileCreateLocale(){
        TournamentNode clientNode = new TournamentNode("Node2","localhost", 3001, "Node2/");

        //creazione file locale
        clientNode.createFile("file.txt","local");
        
        //verifica creazione file locale
        File file = new File("Node2/file.txt");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testFileCreateRemoto(){
        TournamentNode serverNode = new TournamentNode("Node1","localhost", 3000, "Node1/");
        TournamentNode clientNode = new TournamentNode("Node2","localhost", 3001, "Node2/");

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
        TournamentNode clientNode = new TournamentNode("Node2","localhost", 3001, "Node2/");

        //creazione file locale
        clientNode.createFile("file.txt","local");
        clientNode.deleteFile("file.txt");
        //verifica creazione file locale
        File file = new File("Node2/file.txt");
        assertFalse(file.exists());
    }

    @Test
    public void testDeleteRemoto(){
        TournamentNode serverNode = new TournamentNode("Node1","localhost", 3000, "Node1/");
        TournamentNode clientNode = new TournamentNode("Node2","localhost", 3001, "Node2/");

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
        TournamentNode clientNode = new TournamentNode("Node2","localhost", 3001, "Node2/");
        
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
        TournamentNode serverNode = new TournamentNode("Node1","localhost", 3000, "Node1/");
        TournamentNode clientNode = new TournamentNode("Node2","localhost", 3001, "Node2/");

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
    
    @Test
    public void testNodeJoin(){
        TournamentNode node4 = new TournamentNode("Node4","localhost", 3004, "Node4/");
        TournamentNode node5 = new TournamentNode("Node5","localhost", 3005, "Node5/");

        node5.addNeighbor("Node4", "localhost", 3004);

        //start node4
        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            node4.startNodeServer();
        });

        node5.tournamentJoin();

        node4.printNeighbors();

        assertTrue(node4.getNeighbors().containsKey("Node5"));
        assertTrue(node5.getNeighbors().containsKey("Node4"));
    } 

    @Test
    public void testNodeLeave(){
        TournamentNode node1 = new TournamentNode("Node1","localhost", 3001, "Node1/");
        TournamentNode node2 = new TournamentNode("Node2","localhost", 3002, "Node2/");

        node2.addNeighbor("Node1", "localhost", 3001);

        //start node1
        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            node1.startNodeServer();
        });
        
        //il nuovo nodo è entrato nella rete
        node2.tournamentJoin();
        assertTrue(node1.getNeighbors().containsKey("Node2"));

        //il nodo lascia la rete e non è più presente
        node2.TournamentLeave();
        assertFalse(node1.getNeighbors().containsKey("Node2"));

        //la lista dei vicini del nodo che ha lasciato la rete deve essere vuota
        assertTrue(node2.getNeighbors().isEmpty());
    } 

    @Test
    public void testServerStop(){
        TournamentNode node1 = new TournamentNode("Node1","localhost", 3007, "Node1/");

        //start node1
        CompletableFuture.runAsync(() -> { 
            // Handle the request 
            node1.startNodeServer();
        });
        
        //il nuovo nodo è entrato nella rete
        node1.stopNodeServer();
        //la porta non deve essere più occupata
        try (ServerSocket serverSocket = new ServerSocket(3007)) {
            serverSocket.setReuseAddress(true);  // Allow immediate reuse after closing
           assertTrue(true);; // Port is free
        } catch (IOException e) {
            // Port is in use
            fail();
        }
        
    } 
}
