package main;

import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

import util.SocketIO;

public class Temp {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 1423);
        SocketIO socketIO = new SocketIO(socket);

        File file = new File("src/main/test.txt");
        FileInputStream fs = new FileInputStream(file);

        socketIO.sendText("upload-file");

        socketIO.sendText(":" + file.getName());
        socketIO.sendInputStream(fs, file.length());
        socketIO.close();
    }
}
