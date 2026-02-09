package socket;

import properties.Property;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class StorageServer {
    public static void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Started server in port: " + Property.getPort());
            while (true) {
                Socket requester = null;
                try {
                    requester = serverSocket.accept();
                    System.out.println("Recieved request from: " + requester.getInetAddress());
                    Executors.newSingleThreadExecutor().execute(new StorageRequestHandler(requester));
                } catch (IOException e) {
                    System.out.println("Exception connecting to requester:");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Exception connecting");
            e.printStackTrace();
        }
    }
}
