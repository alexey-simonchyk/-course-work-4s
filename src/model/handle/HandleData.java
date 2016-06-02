package model.handle;

import model.Card;

import java.util.ArrayList;

public class HandleData {

    public static byte[] getSendData(byte id, byte numberCards, ArrayList<Card> cards, String command, String message) {
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

    public static byte[] getStartClientData(String name) {
        byte[] nameArray = name.getBytes();
        int length = 1 + nameArray.length;
        byte[] sendData = new byte[length];
        sendData[0] = 0;
        System.arraycopy(nameArray, 0, sendData, 1, nameArray.length);
        return sendData;
    }

    public static byte[] getStartServerData(byte id, String name, ArrayList<Card> cards, Card trumpCard, boolean queueMove) { // флаг, ид , длина имени, количестов карт * 2
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
}
