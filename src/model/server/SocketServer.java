package model.server;

import model.Card;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class SocketServer {
    private static volatile ServerSocket socket;
    private static volatile Socket playerSocket;
    private static DataInputStream inputStream;
    private static DataOutputStream outputStream;


    static void setSocket() {
        try {
            socket = new ServerSocket(7070, 0, InetAddress.getByName("localhost"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void waitPlayers() {
        try {
            playerSocket = socket.accept();
            outputStream = new DataOutputStream(playerSocket.getOutputStream());
            inputStream = new DataInputStream(playerSocket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static byte[] getArrayStartData(byte id, String name, ArrayList<Card> cards, Card trumpCard, boolean queueMove) { // флаг, ид , длина имени, количестов карт * 2
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

    static byte[] getSendData(byte id, byte numberCards, ArrayList<Card> cards, String command) {
        byte commandLength = 0;
        byte[] commandBytes = null;
        if (!command.equals("")) {
            commandBytes = command.getBytes();
            commandLength = (byte)commandBytes.length;
        }
        byte cardsNumber = 0;
        if (cards != null && cards.size() > 0) {
            cardsNumber = (byte)cards.size();
        }
        int length = 4 + cardsNumber * 2 + commandLength;
        byte[] sendData = new byte[length];
        sendData[0] = id;
        sendData[1] = numberCards;
        sendData[2] = (byte)(cardsNumber * 2);
        sendData[3] = commandLength;
        int offset = 4;
        if (cardsNumber > 0) {
            for (Card card : cards) {
                sendData[offset++] = (byte)card.getSuit();
                sendData[offset++] = (byte)card.getValue();
            }
        }
        if (commandLength > 0) {
            System.arraycopy(commandBytes, 0, sendData, offset, commandLength);
        }
        return sendData;
    }

    static void closeSockets() {
        try {
            playerSocket.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Socket getPlayerSocket() { return playerSocket; }

    static void sendData(byte[] sendData) {
        try {
            outputStream.write(sendData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static byte[] receiveData() {
        byte[] result = new byte[1024];
        try {
            inputStream.read(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
