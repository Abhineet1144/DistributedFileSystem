package main;

import java.net.Socket;

import util.SocketIO;

public class STemp {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("192.168.1.74", 1122);
        SocketIO socketIO = new SocketIO(socket);

        socketIO.sendText("hi from ss");
    }
}
