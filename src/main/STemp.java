package main;

import util.SocketIO;

import java.io.IOException;
import java.net.Socket;

public class STemp {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 1423);
        SocketIO socketIO = new SocketIO(socket);
    }
}
