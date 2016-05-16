package Model;

import java.util.ArrayList;

public class Player {

    private ArrayList<Card> cards;// список карт игрока

    public Player() {
        cards = new ArrayList<>();
    }

    public ArrayList<Card> getCards() { return cards; }


    public void update(ArrayList<Card> cards) {
        this.cards = cards;
    }
}
