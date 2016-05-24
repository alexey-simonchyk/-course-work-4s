package model.server;

import model.Card;
import model.Game;

import java.util.ArrayList;

public class Server {
    private Game game;
    private ArrayList<ServerPlayer> players;

    public Server() {
        players = new ArrayList<>();
    }

    public void setPlayers() {
        game = new Game();
    }

    public void addPlayer(ServerPlayer player) {
        players.add(player);
        player.setId(players.size());
    }

    public void updatePlayer(int id) {
        players.get(id - 1).setNumberCards(players.size() - 1);
    }

    public void update(Card card, int id) {
        if (card != null) {
            game.update(card);
            updatePlayer(id);
        }
    }

    public Game getGame() {
        return game;
    }

    public ArrayList<ServerPlayer> getPlayers(int id) {
        ArrayList<ServerPlayer> temp = new ArrayList<>();
        for (ServerPlayer player: players) {
            if (player.getId() != id) {
                temp.add(player);
            }
        }
        return temp;
    }



}
