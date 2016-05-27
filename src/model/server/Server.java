package model.server;

import controller.Controller;
import model.Card;
import model.Game;


public class Server extends Thread {
    private Game game;
    private Deck deck;
    private Controller controller;

    public Server(Controller controller) {
        this.controller = controller;
    }

    public void waitPlayers(String hostName, boolean queueMove) {
        SocketServer.setSocket();
        SocketServer.waitPlayers();
        byte[] receivedData = SocketServer.receiveData();
        String name = getName(receivedData);
        byte id = 1;//(byte)game.getNumberPlayers();
        deck = new Deck();
        game.setTrump(deck.getTrump().getClone());
        byte[] sendData = SocketServer.getArrayStartData(id, hostName, deck.getCards(6), game.getTrump(), true);
        SocketServer.sendData(sendData);
        game.addPlayer(new ServerPlayer(name, SocketServer.getPlayerSocket(), id));
    }

    public Deck getDeck() { return deck; }

    private String getName(byte[] data) {
        byte[] temp;
        if (data[0] == 0) {
            temp = new byte[data.length - 1];
            System.arraycopy(data, 1, temp, 0, data.length - 1);
        } else {
            return "Error";
        }
        return new String(temp);
    }



    public void startGame() {
        game = new Game();
    }

    @Override
    public void run() {
        while (true) {
            byte[] dataReceived = SocketServer.receiveData();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (checkReceivedData(dataReceived)) {
                        controller.update();
                        currentThread().interrupt();
                    }
                }
            }).start();
        }

    }

    private boolean checkReceivedData(byte[] data) {
        if (data == null) {
            return false;
        }
        if (data[0] == 1) {
            update(new Card(data[2], data[3]) , data[1]);
        }
        return true;
    }

    public void update(Card card, int id) {
        if (card != null) {
            game.updateTable(card);
            game.playerMove(id);
        }
    }


    public Game getGame() {
        return game;
    }





}
