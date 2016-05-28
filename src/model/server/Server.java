package model.server;

import controller.Controller;
import model.Card;
import model.Game;


public class Server extends Thread {
    private Game game;
    private Deck deck;
    private Controller controller;

    public void setIsStop() {
        SocketServer.closeSockets();
        this.stop();
    }

    public Server(Controller controller) {
        this.controller = controller;
    }

    public void waitPlayers(String hostName, boolean queueMove) {
        SocketServer.setSocket();
        SocketServer.waitPlayers();
        byte[] receivedData = SocketServer.receiveData();
        String name = getName(receivedData);
        byte id = (byte)game.getNumberPlayers();
        deck = new Deck();
        game.setTrump(deck.getTrump().getClone());
        byte[] sendData = SocketServer.getArrayStartData(id, hostName, deck.getCards(6), game.getTrump(), !queueMove);
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
            if (dataReceived != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (checkReceivedData(dataReceived)) {
                            controller.updateView();
                        }
                    }
                }).start();
            }
        }
    }

    public void sendUpdate(Card card, byte id) {
        controller.setQueueMove(false);
        byte[] sendData = SocketServer.getArrayUpdateData(card, id);
        SocketServer.sendData(sendData);
    }

    private boolean checkReceivedData(byte[] data) {
        if (data[0] == 1) {
            controller.setQueueMove(true);
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
