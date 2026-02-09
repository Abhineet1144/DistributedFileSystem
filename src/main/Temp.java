package main;

import java.net.Socket;

import util.SocketIO;

public class Temp {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 1423);
        SocketIO socketIO = new SocketIO(socket);

        socketIO.sendText("mkdir");
        socketIO.sendText(":new-1");
        socketIO.sendText("mkdir");
        socketIO.sendText(":new-2");
        socketIO.sendText("mkdir");
        socketIO.sendText("/new-1/:n3");
        socketIO.sendText("list");
        socketIO.sendText("");
        System.out.println(socketIO.receiveText());
    }
}