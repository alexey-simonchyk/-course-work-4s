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
        client.start();
    }

    public void setIsServer(boolean isServer) {
        if (isServer) {
            client = new Client("Server");
            server = new Server(this);
        } else {
            server = null;
            client = new Client("Client", this);
        }
        this.isServer = isServer;
    }

    public void playerWait() {
        server.startGame();
        client.getPlayer().setId((byte)0);
        server.getGame().addPlayer(client.getPlayer());
        client.setGame(server.getGame());
        client.getPlayer().setQueueMove(Math.round(Math.random()) == 1);
        server.waitPlayers(client.getPlayer().getName(), !client.getPlayer().getQueueMove());
        client.getPlayer().update(server.getDeck().getCards(6));
        window.startGame();
        server.start();
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
                client.getGame().getPlayers(client.getPlayer().getId()), client.getGame().getTrump());
    }

    public void move(Card card) {
        if (client.getPlayer().getQueueMove()) {
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
                if (isServer) {
                    server.update(card, client.getPlayer().getId());
                    client.getPlayer().removeCard(card);
                    update();
                } else {
                    client.getPlayer().removeCard(card);
                    if (card != null) {
                        client.getGame().updateTable(card);
                        update();
                    }
                }
                        client.sendUpdate(card);
                        System.out.println("Here");
                // ход
            }
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
                    client.getGame().getPlayers(client.getPlayer().getId()), client.getGame().getTrump());

        }
    }
}
