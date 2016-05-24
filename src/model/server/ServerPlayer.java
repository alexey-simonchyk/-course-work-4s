package model.server;

public class ServerPlayer {
    protected int id;
    private int numberCards;
    protected String name;

    public ServerPlayer(String name) {
        numberCards = 6;
        this.name = name;
    }


    public void setNumberCards(int numberCards) { this.numberCards = numberCards; }

    public int getNumberCards() { return numberCards; }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() { return id; }
}
