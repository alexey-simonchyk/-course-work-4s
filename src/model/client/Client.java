package model.client;

import controller.Controller;
import model.Card;
import model.Game;
import model.Player;
import model.server.ServerPlayer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Thread {
    private volatile Player player;
    private volatile Game game;
    private volatile Controller controller;

    public Client(String name, Controller controller) {
        player = new Player(name);
        this.controller = controller;
        ArrayList<Card> playerCards = new ArrayList<>();
        player.update(playerCards);
    }

    public Client(String name) {
        player = new Player(name);
        ArrayList<Card> playerCards = new ArrayList<>();
        player.update(playerCards);
    }

    @Override
    public void run() {
        while (true) {
            byte[] dataReceived = receiveData();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if ( checkReceivedData(dataReceived)) {
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

    public boolean checkReturnMove(Card onTableCard, Card selectedCard, Card trump) {
        if (trump.getSuit() == onTableCard.getSuit()) {
            if (trump.getSuit() == selectedCard.getSuit() && onTableCard.getValue() < selectedCard.getValue()) {
                return true;
            }
        } else if (trump.getSuit() == selectedCard.getSuit()) {
            return true;
        } else if (onTableCard.getValue() < selectedCard.getValue() &&
                   onTableCard.getSuit() == selectedCard.getSuit()) {
            return true;
        }
        return false;
    }


    public Player getPlayer() { return player; }

    public void sendData(byte[] data) {
        try {
            player.getOutputStream().write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] receiveData() {
        byte[] data = new byte[1024];
        try {
            player.getInputStream().read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private byte[] getStartArrayData(String name) {
        byte[] nameArray = name.getBytes();
        int length = 1 + nameArray.length;
        byte[] sendData = new byte[length];
        sendData[0] = 0;
        System.arraycopy(nameArray, 0, sendData, 1, nameArray.length);
        return sendData;
    }

    private byte[] getMoveArrayData(byte id, Card card) {
        int length = 4;
        byte[] sendData = new byte[length];
        sendData[0] = 1;
        sendData[1] = id;
        sendData[2] = (byte)card.getSuit();
        sendData[3] = (byte)card.getValue();
        return sendData;
    }

    private byte[] getCommandArrayData(byte id, String command) {
        byte[] commandArray = command.getBytes();
        int length = 2 + commandArray.length;
        byte[] sendData = new byte[length];
        sendData[0] = 2;
        sendData[1] = id;
        System.arraycopy(commandArray, 0, sendData, 2, commandArray.length);
        return sendData;
    }

    public Game getGame() { return game; }

    public void setGame(Game game) { this.game = game; }

    private void handleStartGame(byte[] receivedData) {
        int offset = 5;
        if (receivedData[0] == 0) {
            player.setId((byte)1);
            byte[] name = new byte[receivedData[2]];
            System.arraycopy(receivedData, offset, name, 0, receivedData[2]);
            game.addPlayer(new ServerPlayer(new String(name), 0));
            game.addPlayer(player);
            offset += receivedData[2];
            byte[] cards = new byte[receivedData[3]];
            System.arraycopy(receivedData, offset, cards, 0, receivedData[3]);
            player.addCards(cards);
            offset += receivedData[3];
            game.setTrump(new Card(receivedData[offset], receivedData[++offset]));
            player.setQueueMove(receivedData[4] == 1);
        }
    }

    public void sendUpdate(Card card) {
        byte[] sendData = getMoveArrayData(player.getId(), card);
        sendData(sendData);
    }

    public void connect(String ip, int port) {
        game = new Game();
        try {
            player.setSocket(new Socket(ip, port));
            sendData(getStartArrayData(player.getName()));
            byte[] receiveData = receiveData();
            handleStartGame(receiveData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
