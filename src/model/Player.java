package model;

import model.server.ServerPlayer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Player extends ServerPlayer {
    private ArrayList<Card> cards;// список карт игрока

    public Player(String name) {
        super(name, 0);
        cards = new ArrayList<>();
    }

    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Card> getCards() { return cards; }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            inputStream = new DataInputStream(this.socket.getInputStream());
            outputStream = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public void update(ArrayList<Card> cards) {
        if (cards != null) {
            this.cards.addAll(cards);
            setNumberCards(getNumberCards() + cards.size());
        }
    }
}
