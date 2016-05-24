package controller;

import model.Card;
import model.Game;
import model.client.Client;
import model.server.Server;
import model.server.ServerPlayer;
import view.Window;

public class Controller extends Thread{
    private Game game;
    private Window window;
    private Client client;
    private boolean isServer;
    private Server server;

    public Controller(Window window) {
        isServer = true;
        client = new Client();
        this.window = window;
        this.window.setController(this);
        server = new Server();
        server.addPlayer(client.getPlayer());
        server.addPlayer(new ServerPlayer("Ger"));
        server.setPlayers();
        start();
    }

    public void update() {
        game = server.getGame();
        window.update(game.getCardsOnTable(), client.getPlayerCards(),
                server.getPlayers(client.getPlayer().getId()));
    }

    public void move(Card card) {
        if (game.needReturnMove()) {
            if (client.checkReturnMove(game.getLastCard(), card, game.getTrump())) {
                if (isServer) {
                    server.update(card, client.getPlayer().getId());
                    client.getPlayer().removeCard(card);
                    update();
                } else {
                    //отправка данных
                }
            }
        } else {

        }
    }

    @Override
    public void run() {
        if (isServer) {
            game = server.getGame();
            client.update();
            server.update(null, client.getPlayer().getId());
        } else {
            // отправка данных
        }
        window.update(game.getCardsOnTable(), client.getPlayerCards(),
                server.getPlayers(client.getPlayer().getId()));
    }
}
