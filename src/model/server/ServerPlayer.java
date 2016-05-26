package model.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ServerPlayer {
    protected int id;
    private int numberCards;
    protected String name;
    protected Socket socket;
    protected DataInputStream inputStream;
    protected DataOutputStream outputStream;

    public ServerPlayer(String name, int id) {
        numberCards = 6;
        this.name = name;
    }

    public DataOutputStream getOutputStream() { return outputStream; }

    public DataInputStream getInputStream() { return inputStream; }

    public ServerPlayer(String name, Socket socket, int id) {
        numberCards = 6;
        this.name = name;
        this.socket = socket;
        this.id = id;
    }


//    private void setSocket(Socket socket) { this.socket = socket; }

    public void setNumberCards(int numberCards) { this.numberCards = numberCards; }

    public int getNumberCards() { return numberCards; }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() { return id; }

    public String getName() {
        return name;
    }
}
