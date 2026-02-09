package main;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

import util.SocketIO;

public class Temp {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 1423);
        SocketIO socketIO = new SocketIO(socket);

//        String content = "Hello world";
//        socketIO.sendText("upload-file");
//        socketIO.sendText(":test3.txt");
//        socketIO.sendInputStream(new ByteArrayInputStream(content.getBytes()), content.length());

        socketIO.sendText("list-all");
        socketIO.sendText("");
        System.out.println(socketIO.receiveText());


        socketIO.close();
    }
}
