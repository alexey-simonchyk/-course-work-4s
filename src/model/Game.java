package model;


import model.server.ServerPlayer;

import java.util.ArrayList;

public class Game {
    private ArrayList<Card> cardsOnTable; // колода
    private Card trump;
    private ArrayList<ServerPlayer> players;


    public Game() {
        this.cardsOnTable = new ArrayList<>();
        players = new ArrayList<>();
    }

    public void addPlayer(ServerPlayer player) {
        players.add(player);
        player.setId((byte)(players.size() - 1));
    }

    public boolean checkMove(int cardValue) {
        if (cardsOnTable.size() == 0) {
            return true;
        }
        for (Card temp : cardsOnTable) {
            if (temp.getValue() == cardValue) {
                return true;
            }
        }
        return false;
    }


    public ArrayList<ServerPlayer> getPlayers(int id) {
        ArrayList<ServerPlayer> temp = new ArrayList<>();
        for (ServerPlayer player: players) {
            if (player.getId() != id) {
                temp.add(player);
            }
        }
        return temp;
    }


    public boolean needReturnMove() {
        return cardsOnTable.size() % 2 == 1;
    }

    public Card getLastTableCard() {
        return cardsOnTable.get(cardsOnTable.size() - 1);
    }

    public void updateTable(Card cardAdd) {
        this.cardsOnTable.add(cardAdd);
    }

    public void setTrump(Card trump) {
        this.trump = trump;
    }

    public Card getTrump() {
        return this.trump;
    }

    public ArrayList<Card> getCardsOnTable() { return cardsOnTable; }

    public int getPlayerNumberCards(int id) {
        return players.get(id).getNumberCards();
    }

    public String getPlayerName(byte id) { return players.get(id).getName(); }

    public void setNumberCards(byte id, int number) {
        players.get(id).setNumberCards(number);
    }

    public void clearTable() {
        if (cardsOnTable.size() > 0) {
            cardsOnTable.clear();
        }
    }

    public int getNumberPlayers() { return players.size(); }
}
