package model.server;

import model.Card;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;
    public Deck() {
        cards = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 13; j++) {
                cards.add(new Card(i, j));
            }
        }
        Collections.shuffle(cards);
    }

    int getNumberCards() { return cards.size(); }

    public ArrayList<Card> getCards(int number) {
        ArrayList<Card> temp = new ArrayList<>();
        if (number > cards.size()) {
            number = cards.size();
        }
        if (number > 0) {
            for (int i = 0; i < number; i++) {
                cards.get(cards.size() - 1).checkTrumpCard();
                temp.add(cards.get(cards.size() - 1));
                cards.remove(cards.size() - 1);
            }
        } else {
            return null;
        }
        return temp;
    }


    public Card getTrump () {
        return cards.get(0);
    }
}
