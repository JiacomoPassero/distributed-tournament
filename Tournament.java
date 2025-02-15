public class Tournament {
    public static void main(String[] args) {
        System.out.println("-Inizio torneo");

        TournamentServer tournament = new TournamentServer(4, 8);
        tournament.initializeFencers();
        tournament.printFencers();

        System.out.println("-Fine torneo");
    }

}
