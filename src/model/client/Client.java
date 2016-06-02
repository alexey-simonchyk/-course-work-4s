package model.client;

import controller.Controller;
import model.Card;
import model.Connection;
import model.Game;
import model.Player;
import model.handle.HandleData;
import model.server.ServerPlayer;

import java.util.ArrayList;

public class Client extends Connection {

    @Override
    public void setIsStop() {
        if (this.isAlive()) {
            this.stop();
        }
        SocketClient.closeSocket();
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
        while (!game.getEnd()) {
            byte[] dataReceived = SocketClient.receiveData();
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

    @Override
    public void sendMessage(String message) {
        byte[] sendData = HandleData.getSendData(player.getId(), (byte)0, null, null, message);
        SocketClient.sendData(sendData);
    }

    @Override
    protected boolean checkReceivedData(byte[] data) {
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
        switch (receivedCommand) {
            case PASS_MOVE:
                game.clearTable();
                player.setQueueMove(true);
                break;
            case TAKE_MOVE:
                game.clearTable();
                player.setQueueMove(true);
                break;
            case END_GAME_LOSE:
                controller.updateChatArea("Вы выиграли!!!");
                game.setEnd(true);
                break;
            case END_GAME_WIN:
                controller.updateChatArea("Выиграл ваш противник....");
                game.setEnd(true);
                break;
            case END_GAME_NO_WIN:
                controller.updateChatArea("Ничья");
                game.setEnd(true);
                break;
        }
        controller.updateView();
    }


    public boolean checkReturnMove(Card onTableCard, Card selectedCard, Card trump) {
        if (trump.getSuit() == onTableCard.getSuit()) {
            if (trump.getSuit() == selectedCard.getSuit() &&
                    (( onTableCard.getValue() < selectedCard.getValue() && onTableCard.getValue() != 0)
                            || selectedCard.getValue() == 0 )) {
                return true;
            }
        } else if (trump.getSuit() == selectedCard.getSuit()) {
            return true;
        } else if ((( onTableCard.getValue() < selectedCard.getValue() && onTableCard.getValue() != 0)
                        || selectedCard.getValue() == 0 ) &&
                   onTableCard.getSuit() == selectedCard.getSuit()) {
            return true;
        }
        return false;
    }


    public Player getPlayer() { return player; }

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

    @Override
    public void sendCommand(boolean isPass) {
        byte[] sendData;
        if (isPass) {
            game.clearTable();
            sendData = HandleData.getSendData(player.getId(), (byte)player.getNumberCards(), null, PASS_MOVE, null);
            SocketClient.sendData(sendData);
        } else {
            player.update(game.getCardsOnTable());
            game.clearTable();
            sendData = HandleData.getSendData(player.getId(), (byte)player.getNumberCards(), null, TAKE_MOVE, null);
            SocketClient.sendData(sendData);
        }
        player.setQueueMove(false);
    }


    @Override
    public void sendUpdate(Card card) {
        ArrayList<Card> temp = new ArrayList<>();
        temp.add(card);
        byte[] sendData = HandleData.getSendData(player.getId(), (byte)player.getNumberCards(), temp, null, null);
        SocketClient.sendData(sendData);
    }

    public void connect(String ip) {
        game = new Game();
        SocketClient.setSocket(ip);
        SocketClient.sendData(HandleData.getStartClientData(player.getName()));
        byte[] receiveData = SocketClient.receiveData();
        handleStartGame(receiveData);
    }


}
