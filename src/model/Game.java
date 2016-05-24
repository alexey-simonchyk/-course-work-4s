package model;


import java.util.ArrayList;

public class Game {
    private boolean isEnd;
    private ArrayList<Card> cardsOnTable; // колода
    private int trump;
    private int idMove;

    public Game() {
        ArrayList<Card> cardsOnTable = new ArrayList<>();
        cardsOnTable.add(new Card(0, 1));
        cardsOnTable.add(new Card(0, 2));
        cardsOnTable.add(new Card(1, 10));
        cardsOnTable.add(new Card(3, 9));
        cardsOnTable.add(new Card(2, 11));
        cardsOnTable.add(new Card(1, 8));
        cardsOnTable.add(new Card(0, 1));
        cardsOnTable.add(new Card(0, 2));
        cardsOnTable.add(new Card(1, 10));
        cardsOnTable.add(new Card(3, 9));
        cardsOnTable.add(new Card(0, 4));
        this.cardsOnTable = cardsOnTable;
        isEnd = true;
        this.trump = 1;
    }


    public void addCardOnTheTable(Card card) { cardsOnTable.add(card); }

    public boolean getEndGame() { return isEnd; }

    public boolean needReturnMove() {
        return cardsOnTable.size() % 2 == 1;
    }

    public Card getLastCard() {
        return cardsOnTable.get(cardsOnTable.size() - 1);
    }

    public void setEndGame(boolean isEnd) { this.isEnd = isEnd; }

    public void update(Card cardAdd) {
        this.cardsOnTable.add(cardAdd);
    }

    public void setTrump(int trump) {
        this.trump = trump;
    }

    public int getTrump() {
        return this.trump;
    }

    public ArrayList<Card> getCardsOnTable() { return cardsOnTable; }
}
