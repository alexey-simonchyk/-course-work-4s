package Model;

public class Enemy {
    private int numberCards;
    private String name;
    public Enemy(String name) {
        numberCards = 6;
        this.name = name;
    }
    public int getNumberCards() { return numberCards; }
    public void setNumberCards(int numberCards) { this.numberCards = numberCards; }
    public String getName() { return name; }
}
