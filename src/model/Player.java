package model;

import model.server.ServerPlayer;

import java.util.ArrayList;

public class Player extends ServerPlayer {

    private ArrayList<Card> cards;// список карт игрока

    public Player(String name) {
        super(name);
        cards = new ArrayList<>();
    }

    public ArrayList<Card> getCards() { return cards; }

    public void removeCard(Card card) {
        int counter = 0;
        for (Card temp: cards) {
            if (card.getSuit() == temp.getSuit() &&
                    card.getValue() == temp.getValue()) {
                cards.remove(counter);
                break;
            }
            counter++;
        }
    }


    public void update(ArrayList<Card> cards) {
        this.cards.addAll(cards);
    }
}
