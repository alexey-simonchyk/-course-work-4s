package model;

import model.server.ServerPlayer;

import java.util.ArrayList;
import java.util.Comparator;

public class Player extends ServerPlayer {
    private ArrayList<Card> cards;// список карт игрока

    public Player(String name) {
        super(name, 0);
        cards = new ArrayList<>();
    }

    public ArrayList<Card> getCards() { return cards; }

    @Override
    public int getNumberCards() { return cards.size(); }

    public void removeCard(Card card) {
        int counter = 0;
        for (Card temp: cards) {
            if (card.getSuit() == temp.getSuit() &&
                    card.getValue() == temp.getValue()) {
                cards.remove(counter);
                setNumberCards(getNumberCards() - 1);
                break;
            }
            counter++;
        }
    }

    public void addCards(byte[] cards) {
        for (int i = 0; i < cards.length - 1; i += 2) {
            this.cards.add(new Card(cards[i], cards[i + 1]));
        }
    }

    public void sortCards() {
        if (cards != null && cards.size() > 0) {
            cards.sort(new Comparator<Card>() {
                @Override
                public int compare(Card o1, Card o2) {
                    return o1.getTrumpCard() - o2.getTrumpCard();
                }
            });
        }
    }

    public void update(ArrayList<Card> cards) {
        if (cards != null) {
            this.cards.addAll(cards);
            setNumberCards(getNumberCards() + cards.size());
        }
    }
}
