package model.client;

import model.Card;
import model.Player;

import java.util.ArrayList;

public class Client {
    private Player player;

    public Client() {
        player = new Player("Name");
        ArrayList<Card> playerCards = new ArrayList<>();
        playerCards.add(new Card(0, 4));
        playerCards.add(new Card(0, 5));
        player.update(playerCards);
    }

    public boolean checkReturnMove(Card onTableCard, Card selectedCard, int trump) {
        if (trump == onTableCard.getSuit()) {
            if (trump == selectedCard.getSuit() && onTableCard.getValue() < selectedCard.getValue()) {
                return true;
            }
        } else if (trump == selectedCard.getSuit()) {
            return true;
        } else if (onTableCard.getValue() < selectedCard.getValue() &&
                   onTableCard.getSuit() == selectedCard.getSuit()) {
            return true;
        }
        return false;
    }

    public void update() {

    }

    public ArrayList<Card> getPlayerCards() {
        return player.getCards();
    }

    public Player getPlayer() { return player; }

}
