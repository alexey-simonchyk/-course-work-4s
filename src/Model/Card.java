package Model;

public class Card {
    private int suit; // масть
    private int value; // значение карты
    public Card(int suit, int value) {
        this.suit = suit;
        this.value = value;
    }
    public int getSuit() { return suit; }

    public int getValue() { return value; }

    public void setValue(int value) { this.value = value; }

    public void setSuit(int suit) { this.suit = suit; }
}
