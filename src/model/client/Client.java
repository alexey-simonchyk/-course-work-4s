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
    private final String PASS_MOVE = "PASS";
    private final String TAKE_MOVE = "TAKE";
    private volatile Player player;
    private volatile Game game;
    private volatile Controller controller;

    public void setIsStop() {
        this.stop();
        player.closeSocket();
    }

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

    public void sendMessage(String message) {
        byte[] sendData = getSendData(player.getId(), (byte)0, null, null, message);
        sendData(sendData);
    }

    private boolean checkReceivedData(byte[] data) {
        int offset = 5;
        if (data[4] > 0) {
            byte[] temp = new byte[data[4]];
            System.arraycopy(data, offset, temp, 0, data[4]);
            controller.updateChatArea(new String(temp), game.getPlayerName(data[0]));
        } else {
            game.setNumberCards(data[0], data[1]);
            if (data[2] > 0) {
                byte[] temp = new byte[data[2]];
                System.arraycopy(data, offset, temp, 0, data[2]);
                if (data[3] > 0) {
                    player.addCards(temp);
                } else {
                    game.updateTable(new Card(data[offset], data[offset + 1]));
                    player.setQueueMove(true);
                }
            }
            if (data[3] > 0) {
                byte[] temp = new byte[data[3]];
                offset += data[2];
                System.arraycopy(data, offset, temp, 0, data[3]);
                checkCommand(temp);
            }
        }
        return true;
    }

    private void checkCommand(byte[] data) {
        String receivedCommand = new String(data);
        if (receivedCommand.equals(PASS_MOVE)) {
            game.clearTable();
            player.setQueueMove(true);
        } else if (receivedCommand.equals(TAKE_MOVE)) {
            game.clearTable();
            player.setQueueMove(true);
        }
        controller.updateView();
    }


    public boolean checkReturnMove(Card onTableCard, Card selectedCard, Card trump) {
        if (trump.getSuit() == onTableCard.getSuit()) {
            if (trump.getSuit() == selectedCard.getSuit() &&
                    ( onTableCard.getValue() < selectedCard.getValue() || selectedCard.getValue() == 0 )) {
                return true;
            }
        } else if (trump.getSuit() == selectedCard.getSuit()) {
            return true;
        } else if (( onTableCard.getValue() < selectedCard.getValue() || selectedCard.getValue() == 0 ) &&
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

    public Game getGame() { return game; }

    public void setGame(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

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
            offset += receivedData[3];
            game.setTrump(new Card(receivedData[offset], receivedData[++offset]));
            Card.setTrump(game.getTrump().getSuit());
            player.addCards(cards);
            player.setQueueMove(receivedData[4] == 1);
        }
    }

    public void sendCommand(boolean isPass) {
        byte[] sendData;
        if (isPass) {
            game.clearTable();
            sendData = getSendData(player.getId(), (byte)player.getNumberCards(), null, PASS_MOVE, null);
            sendData(sendData);
        } else {
            player.update(game.getCardsOnTable());
            game.clearTable();
            sendData = getSendData(player.getId(), (byte)player.getNumberCards(), null, TAKE_MOVE, null);
            sendData(sendData);
        }
        player.setQueueMove(false);
    }


    public void sendUpdate(Card card) {
        ArrayList<Card> temp = new ArrayList<>();
        temp.add(card);
        byte[] sendData = getSendData(player.getId(), (byte)player.getNumberCards(), temp, null, null);
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

    private byte[] getSendData(byte id, byte numberCards, ArrayList<Card> cards, String command, String message) {
        int messageLength = 0;
        byte[] messageBytes = null;
        if (message != null) {
            messageBytes = message.getBytes();
            messageLength = messageBytes.length;
        }
        byte commandLength = 0;
        byte[] commandBytes = null;
        if (command != null) {
            commandBytes = command.getBytes();
            commandLength = (byte)commandBytes.length;
        }
        byte cardsNumber = 0;
        if (cards != null && cards.size() > 0) {
            cardsNumber = (byte)cards.size();
        }
        int length = 5 + cardsNumber * 2 + commandLength + messageLength;
        byte[] sendData = new byte[length];
        sendData[0] = id;
        sendData[1] = numberCards;
        sendData[2] = (byte)(cardsNumber * 2);
        sendData[3] = commandLength;
        sendData[4] = (byte)messageLength;
        int offset = 5;
        if (cardsNumber > 0) {
            for (Card card : cards) {
                sendData[offset++] = (byte)card.getSuit();
                sendData[offset++] = (byte)card.getValue();
            }
        }
        if (commandLength > 0) {
            System.arraycopy(commandBytes, 0, sendData, offset, commandLength);
        }
        offset += commandLength;
        if (messageLength > 0) {
            System.arraycopy(messageBytes, 0, sendData, offset, messageLength);
        }
        return sendData;
    }

}
