package model.server;

public class ServerPlayer {
    protected int id;
    private int numberCards;
    private boolean queueMove;
    protected String name;

    public boolean getQueueMove() { return queueMove; }

    public void setQueueMove (boolean queueMove) { this.queueMove = queueMove; }

    public ServerPlayer(String name, int id) {
        numberCards = 6;
        this.name = name;
        this.id = id;
    }

    public void setNumberCards(int numberCards) { this.numberCards = numberCards; }

    public int getNumberCards() { return numberCards; }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getId() { return (byte)id; }

    public String getName() {
        return name;
    }
}
