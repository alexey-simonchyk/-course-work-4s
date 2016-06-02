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
    private volatile boolean isInMainMenu;

    public Controller(Window window) {
        isServer = false;
        this.window = window;
        isInMainMenu = true;
    }

    public void connectToServer(String ip) {
        client.connect(ip);
        client.getPlayer().sortCards();
        window.startGame();
        client.start();
        updateChatArea(client.getPlayer().getQueueMove() ? "Игра начата, ваш ход." : "Игра начата, первым ходит ваш противник.");
    }

    public void updateChatArea(String message, String name) {
        window.updateChat(name + " : " + message);
    }

    public void updateChatArea(String message) {
        window.updateChat(message);
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
        server.startGame(client.getPlayer());
        client.getPlayer().setId((byte)0);
        server.getGame().addPlayer(client.getPlayer());
        client.setGame(server.getGame(), client.getPlayer());
        client.getPlayer().setQueueMove(Math.round(Math.random()) == 1);
        server.waitPlayers(client.getPlayer().getName(), client.getPlayer().getQueueMove());
        client.getPlayer().update(server.getDeck().getCards(6));
        client.getPlayer().sortCards();
        window.startGame();
        server.start();
        updateChatArea(client.getPlayer().getQueueMove() ? "Игра начата, ваш ход." : "Игра начата, первым ходит ваш противник.");
    }

    public void setInMainMenu(boolean isInMainMenu) {
        this.isInMainMenu = isInMainMenu;
    }

    public void setQueueMove(boolean temp) {
        client.getPlayer().setQueueMove(temp);
    }

    public void updateView() {
        if (!isInMainMenu) {
            window.update(client.getGame().getCardsOnTable(), client.getPlayer().getCards(),
                    client.getGame().getPlayers(client.getPlayer().getId()), client.getGame().getTrump());
        }
    }

    public void stopThreads() {
        if (isServer) {
            if (server != null) {
                server.setIsStop();
            }
        } else {
            if (client != null) {
                client.setIsStop();
            }
        }
    }

    public void controlButtonPressed(boolean isPass) {
        if (client.getPlayer().getQueueMove() && client.getGame().getCardsOnTable().size() > 0
                && !client.getGame().getEnd()) {
            if (( !client.getGame().needReturnMove() && isPass) || (client.getGame().needReturnMove() && !isPass)) {
                client.getPlayer().setQueueMove(false);
                if (isServer) {
                    server.sendCommand(isPass);
                    updateView();
                } else {
                    client.sendCommand(isPass);
                    updateView();
                }
            }
        }
    }

    public void buttonSendMessagePressed(String message) {
        if (message != null && !message.equals(""))
            if (isServer) {
                server.sendMessage(message);
                window.updateChat(client.getPlayer().getName() + " : " + message);
            } else {
                client.sendMessage(message);
                window.updateChat(client.getPlayer().getName() + " : " + message);
            }
    }

    public void move(Card card) {
        if (client.getPlayer().getQueueMove() && card != null && !client.getGame().getEnd()) {
            if (client.getGame().needReturnMove()) { // ОТВЕТНЫЙ ХОД
                if (client.checkReturnMove(client.getGame().getLastTableCard(), card, client.getGame().getTrump())) {
                    client.getPlayer().setQueueMove(false);
                    if (isServer) {
                        server.getGame().updateTable(card);
                        client.getPlayer().removeCard(card);
                        updateView();
                        server.sendUpdate(card);
                    } else {
                        client.getPlayer().removeCard(card);
                        client.getGame().updateTable(card);
                        updateView();
                        client.sendUpdate(card);
                    }
                }
            } else {
                if (client.getGame().checkMove(card.getValue())) { // ОБЫЧНЫЙ ХОД
                    client.getPlayer().setQueueMove(false);
                    if (isServer) {
                        server.getGame().updateTable(card);
                        client.getPlayer().removeCard(card);
                        updateView();
                        server.sendUpdate(card);
                    } else {
                        client.getPlayer().removeCard(card);
                        client.getGame().updateTable(card);
                        updateView();
                        client.sendUpdate(card);
                    }
                }
            }
        }
    }
}
