package Controller;

import Model.Card;
import Model.Game;
import Model.Player;
import View.Window;

import java.util.ArrayList;

public class Controller extends Thread{
    private Player player;
    private Game game;
    private Window window;

    public Controller(Window window) {
        player = new Player();
        game = new Game(player, 2);
        this.window = window;
        start();
    }

    @Override
    public void run() {
        //while (true) {
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
        cardsOnTable.add(new Card(2, 11));
        cardsOnTable.add(new Card(1, 8));
            ArrayList<Card> playerCards = new ArrayList<>();
            playerCards.add(new Card(0, 4));
            playerCards.add(new Card(0, 5));
            playerCards.add(new Card(0, 4));
            playerCards.add(new Card(0, 5));
            playerCards.add(new Card(0, 4));
            boolean isEnd = false;
            game.update(cardsOnTable, playerCards, isEnd);
            window.update(game.getCardsOnTable(), player.getCards(), game.getEnemies());
        //}
    }
}
