import java.io.FileReader;
import java.util.*;

public class TournamentServer {
    private int group_size;
    private int fencer_number;
    private ArrayList<String> fencers;

    public TournamentServer(int group_size, int fencer_number) {
        this.fencer_number = fencer_number;
        this.group_size = group_size;
    }

    //funzione per inizializzare i partecipanti, ogni giocatore farà parte dell'entry di una lista
    public boolean initializeFencers() {
       
        this.fencers = new ArrayList<>();

        try (Scanner s = new Scanner(new FileReader("StaticData/fencers.txt"))) {
            while (s.hasNext()) {
                this.fencers.add(s.nextLine());
            }
        }catch (Exception e) {
            System.out.println("Errore nella lettura del file");
            return false;
        }
        return true;
    }

    public void printFencers() {
        System.out.println("Torneo da " + this.fencer_number + " partecipanti");
        System.out.println("Dimensione gruppi: " + this.group_size);
        for (String fencer : this.fencers) {
            System.out.println(fencer);
        }
    }
    
}
