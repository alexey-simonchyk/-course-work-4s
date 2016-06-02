package model.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class SocketClient {
    private static Socket socket = null;
    private static DataInputStream inputStream;
    private static DataOutputStream outputStream;
    private static final int port = 7070;


    static void sendData(byte[] sendData) {
        try {
            outputStream.write(sendData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void setSocket(String ip) {
        try {
            socket = new Socket(ip, port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void closeSocket() {
        if (socket != null)
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
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
