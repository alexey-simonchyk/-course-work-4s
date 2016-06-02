package model.server;

import controller.Controller;
import model.Card;
import model.Connection;
import model.Game;
import model.Player;
import model.handle.HandleData;

import java.util.ArrayList;


public class Server extends Connection {
    private Deck deck;

    public void setIsStop() {
        if (this.isAlive()) {
            this.stop();
        }
        SocketServer.closeSockets();
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
        Card.setTrump(game.getTrump().getSuit());
        byte[] sendData = HandleData.getStartServerData(id, hostName, deck.getCards(6), game.getTrump(), !queueMove);
        SocketServer.sendData(sendData);
        game.addPlayer(new ServerPlayer(name, id));
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



    public void startGame(Player player) {
        this.player = player;
        game = new Game();
    }

    @Override
    public void run() {
        while (!game.getEnd()) {
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

    private boolean checkEnd() {
        byte[] sendData;
        if ((game.getPlayerNumberCards(0) == 0 || game.getPlayerNumberCards(1) == 0 )
                && deck.getNumberCards() == 0) {
            if (game.getPlayerNumberCards(0) == 0 && game.getPlayerNumberCards(1) == 0) {
                sendData = HandleData.getSendData(player.getId(), (byte)player.getNumberCards(), null, END_GAME_NO_WIN, null);
                controller.updateChatArea("Ничья");
            } else if (game.getPlayerNumberCards(player.getId()) == 0) {
                sendData = HandleData.getSendData(player.getId(), (byte)player.getNumberCards(), null, END_GAME_WIN, null);
                controller.updateChatArea("Вы Выиграли!!!");
            } else {
                sendData = HandleData.getSendData(player.getId(), (byte)player.getNumberCards(), null, END_GAME_LOSE, null);
                controller.updateChatArea("Вы проиграли...");
            }
            game.setEnd(true);
            SocketServer.sendData(sendData);
            return true;
        }
        return false;
    }

    @Override
    public void sendCommand(boolean isPass) {
        byte[] sendData;
        if (!checkEnd())
            if (isPass) {
            game.clearTable();
            player.update(deck.getCards(6 - player.getNumberCards()));
            ArrayList<Card> temp = deck.getCards(6 - game.getPlayerNumberCards(1));
            if (temp != null) {
                game.setNumberCards((byte)1, game.getPlayerNumberCards(1) + temp.size());
            }
            sendData = HandleData.getSendData(player.getId(), (byte)player.getNumberCards(), temp, PASS_MOVE, null);
            SocketServer.sendData(sendData);
        } else {
            player.update(game.getCardsOnTable());
            game.clearTable();
            ArrayList<Card> temp = deck.getCards(6 - game.getPlayerNumberCards(1));
            if (temp != null) {
                game.setNumberCards((byte)1, game.getPlayerNumberCards(1) + temp.size());
            }
            sendData = HandleData.getSendData(player.getId(), (byte)player.getNumberCards(), temp, TAKE_MOVE, null);
            SocketServer.sendData(sendData);
        }
        controller.setQueueMove(false);
    }

    @Override
    public void sendUpdate(Card card) {
        ArrayList<Card> temp = new ArrayList<>();
        temp.add(card);
        byte[] sendData = HandleData.getSendData(player.getId(), (byte) player.getNumberCards(), temp, null, null);
        SocketServer.sendData(sendData);
    }

    @Override
    public void sendMessage(String message) {
        byte[] sendData = HandleData.getSendData(player.getId(), (byte)0, null, null, message);
        SocketServer.sendData(sendData);
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
                game.updateTable(new Card(data[offset], data[offset + 1]));
                controller.setQueueMove(true);
            }
            if (data[3] > 0) {
                byte[] temp = new byte[data[3]];
                offset += data[2];
                System.arraycopy(data, offset, temp, 0, data[3]);
                checkCommand(temp, data[0]);
            }
        }
        return true;
    }

    private void checkCommand(byte[] data, byte id) {
        String receivedCommand = new String(data);
        if (receivedCommand.equals(PASS_MOVE)) {
            game.clearTable();
            ArrayList<Card> temp = deck.getCards(6 - game.getPlayerNumberCards(1));
            if (temp != null) {
                game.setNumberCards(id, game.getPlayerNumberCards(id) + temp.size());
            }
            player.update(deck.getCards(6 - player.getNumberCards()));
            byte[] sendData = HandleData.getSendData(player.getId(), (byte)player.getNumberCards(), temp, "PLAYER", null);
            SocketServer.sendData(sendData);
        } else if (receivedCommand.equals(TAKE_MOVE)) {
            game.setNumberCards(id, game.getCardsOnTable().size() + game.getPlayerNumberCards(id));
            player.update(deck.getCards(6 - player.getNumberCards()));
            game.clearTable();
        }
        controller.setQueueMove(true);
        checkEnd();
    }


    public Game getGame() {
        return game;
    }





}
