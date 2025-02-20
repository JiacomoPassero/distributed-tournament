package Tournament;

public class TournamentNeighbor {
    String address;
    int port;

    public TournamentNeighbor(String address, int port){
        this.address = address;
        this.port = port;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
