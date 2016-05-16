package Model;

import java.util.ArrayList;

public class Game {
    private boolean isEnd;
    private ArrayList<Card> cardsOnTable; // колода
    private Player player;
    private ArrayList<Enemy> enemies;

    public Game(Player player, int numberEnemies) {
        this.player = player;
        enemies = new ArrayList<Enemy>();
        for (int i = 0; i < numberEnemies; i++)
            enemies.add(new Enemy("Elli"));
    }

    public boolean getEndGame() { return isEnd; }

    public void setEndGame(boolean isEnd) { this.isEnd = isEnd; }

    public void update(ArrayList<Card> cardsOnTable, ArrayList<Card> playerCards, boolean isEnd) {
        this.cardsOnTable = cardsOnTable;
        player.update(playerCards);
        for (Enemy temp: enemies) {
            temp.setNumberCards(12);
        }
        setEndGame(isEnd);
    }

    public ArrayList<Card> getCardsOnTable() { return cardsOnTable; }
    public ArrayList<Enemy> getEnemies() { return enemies; }
}
