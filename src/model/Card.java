package model;

public class Card {
    private int suit; // масть
    private int value; // значение карты
    private static int trump;
    private int trumpCard;
    public Card(int suit, int value) {
        this.suit = suit;
        if (suit == trump) {
            this.trumpCard = 1;
        } else {
            this.trumpCard = 0;
        }
        this.value = value;
    }

    public void checkTrumpCard() {
        if (suit == trump) {
            this.trumpCard = 1;
        } else {
            this.trumpCard = 0;
        }
    }

    public int getTrumpCard() { return trumpCard; }

    public static void setTrump(int newTrump) {
        trump = newTrump;
    }

    public int getSuit() { return suit; }

    public int getValue() { return value; }

    public Card getClone() {
        return new Card(suit, value);
    }
}
