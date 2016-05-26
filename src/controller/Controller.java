package controller;

import model.Card;
import model.client.Client;
import model.server.Server;
import view.Window;

public class Controller {
    private Window window;
    private Client client;
    private boolean isServer;
    private Server server;
    private boolean isInMainMenu;

    public Controller(Window window) {
        isServer = false;
        this.window = window;
        isInMainMenu = false;
    }

    public void connectToServer(String ip, int port) {
        client.connect(ip, port);
        window.startGame();
    }

    public void setIsServer(boolean isServer) {
        if (isServer) {
            client = new Client("Server");
            server = new Server();
        } else {
            server = null;
            client = new Client("Client");
        }
        this.isServer = isServer;
    }

    public void playerWait() {
        server.startGame();
        client.getPlayer().setId(0);
        server.getGame().addPlayer(client.getPlayer());
        client.setGame(server.getGame());
        server.waitPlayers(client.getPlayer().getName());
        client.getPlayer().update(server.getDeck().getCards(6));
        window.startGame();
    }

    public void setInMainMenu(boolean isInMainMenu) {
        if (isInMainMenu) {
            window.setMainMenuScene();
        } else {
            window.setGameScene();
        }
        this.isInMainMenu = isInMainMenu;
    }

    public void update() {
        window.update(client.getGame().getCardsOnTable(), client.getPlayer().getCards(),
                server.getGame().getPlayers(client.getPlayer().getId()), /*client.getGame().getTrump()*/new Card(1, 1));
    }

    public void move(Card card) {
        if (client.getGame().needReturnMove()) {
            if (client.checkReturnMove(client.getGame().getLastTableCard(), card, client.getGame().getTrump())) {
                if (isServer) {
                    server.update(card, client.getPlayer().getId());
                    client.getPlayer().removeCard(card);
                    update();
                } else {
//                    отправка данных
                }

            }
        } else {
            // ход
        }
    }

    public void start() {
        if (!isInMainMenu) {
            if (isServer) {
                server.update(null, client.getPlayer().getId());
            } else {

                // отправка данных
            }
            window.update(client.getGame().getCardsOnTable(), client.getPlayer().getCards(),
                    client.getGame().getPlayers(client.getPlayer().getId()), /*client.getGame().getTrump()*/new Card(1, 1));

        }
    }
}
