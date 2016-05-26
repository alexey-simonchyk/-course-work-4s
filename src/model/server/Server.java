package model.server;

import model.Card;
import model.Game;


public class Server {
    private Game game;
    private Deck deck;

    public Server() {
    }

    public void waitPlayers(String hostName) {
        SocketServer.setSocket();
        SocketServer.waitPlayers();
        byte[] receivedData = SocketServer.receiveData();
        String name = getName(receivedData);
        byte id = (byte)game.getNumberPlayers();
        deck = new Deck();
        game.setTrump(deck.getTrump().getClone());
        byte[] sendData = SocketServer.getArrayStartData(id, hostName, deck.getCards(6));
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
