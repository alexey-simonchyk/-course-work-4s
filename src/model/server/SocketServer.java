package model.server;

import model.Card;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketServer {
    private static ServerSocket socket;
    private static Socket playerSocket;
    private static DataInputStream inputStream;
    private static DataOutputStream outputStream;


    public static void setSocket() {
        try {
            socket = new ServerSocket(7070, 0, InetAddress.getByName("localhost"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void waitPlayers() {
        String name = null;
        try {
            playerSocket = socket.accept();
            outputStream = new DataOutputStream(playerSocket.getOutputStream());
            inputStream = new DataInputStream(playerSocket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getArrayStartData(byte id, String name, ArrayList<Card> cards, Card trumpCard, boolean queueMove) { // флаг, ид , длина имени, количестов карт * 2
        byte[] nameArray = name.getBytes();
        int length = 4 + nameArray.length + cards.size() * 2 + 3;
        byte[] sendData = new byte[length];
        sendData[0] = 0;
        sendData[1] = id;
        sendData[2] = (byte)nameArray.length;
        sendData[3] = (byte)(cards.size() * 2);
        sendData[4] = queueMove ? (byte)1 : (byte)0;
        System.arraycopy(nameArray, 0, sendData, 5, nameArray.length);
        int counter = 5 + nameArray.length;
        for (Card temp: cards) {
            sendData[counter++] = (byte)temp.getSuit();
            sendData[counter++] = (byte)temp.getValue();
        }
        sendData[counter++] = (byte)trumpCard.getSuit();
        sendData[counter] = (byte)trumpCard.getValue();
        return sendData;
    }

    public static byte[] getArrayUpdateData(Card card, byte id) {
        int length = 4;
        byte[] sendData = new byte[length];
        sendData[0] = 1;
        sendData[1] = id;
        sendData[2] = (byte)card.getSuit();
        sendData[3] = (byte)card.getSuit();
        return sendData;
    }

    public static Socket getPlayerSocket() { return playerSocket; }

    public static void sendData(byte[] sendData) {
        try {
            outputStream.write(sendData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static byte[] receiveData() {
        byte[] result = new byte[1024];
        try {
            inputStream.read(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
