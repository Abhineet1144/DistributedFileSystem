package main;

import java.io.ByteArrayInputStream;
import java.net.Socket;

import util.SocketIO;

public class Temp {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 1423);
        SocketIO socketIO = new SocketIO(socket);

//        socketIO.sendText("mkdir");
//        socketIO.sendText(":folder");
//
//        socketIO.sendText("mkdir");
//        socketIO.sendText("folder/:folder1");


        String content = "hi world";
        socketIO.sendText("delete");

        socketIO.sendText("/folder/test.txt");
//        socketIO.sendInputStream(new ByteArrayInputStream(content.getBytes()), content.length());
        socketIO.close();
    }
}