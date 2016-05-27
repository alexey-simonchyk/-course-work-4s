package model;


import model.server.ServerPlayer;

import java.util.ArrayList;

public class Game {
    private boolean isEnd;
    private ArrayList<Card> cardsOnTable; // колода
    private Card trump;
    private ArrayList<ServerPlayer> players;


    public Game() {
        this.cardsOnTable = new ArrayList<>();
        isEnd = true;
        players = new ArrayList<>();
    }

    public void addPlayer(ServerPlayer player) {
        players.add(player);
        player.setId((byte)(players.size() - 1));
    }

    public void playerMove(int id) {
        players.get(id - 1).setNumberCards(players.size() - 1);
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

    public boolean getEndGame() { return isEnd; }

    public boolean needReturnMove() {
        return cardsOnTable.size() % 2 == 1;
    }

    public Card getLastTableCard() {
        return cardsOnTable.get(cardsOnTable.size() - 1);
    }

    public void setEndGame(boolean isEnd) { this.isEnd = isEnd; }

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

    public void clearTable() {
        if (cardsOnTable.size() > 0) {
            cardsOnTable.clear();
        }
    }

    public int getNumberPlayers() { return players.size(); }
}
