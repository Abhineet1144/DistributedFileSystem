package socket;

import properties.Property;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class StorageServer {
    private static final String STORAGE = "storage";

    private static boolean started = false;

    public static void start() {
        if (!Property.getMode().equals(STORAGE)) {
            System.out.println("Current mode is not controller");
            return;
        } else if (started) {
            System.out.println("Already started");
            return;
        }

        File root = new File(Property.getStoragePath());
        if (root.getUsableSpace() < Property.getAllocatedBytes()) {
            throw new IllegalStateException("Less space available in disk");
        }

        started = true;
        try {
            int port = Integer.parseInt(Property.getPort());
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Started server in port: " + port);
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
        } catch (NumberFormatException e) {
            System.out.println("Error in port format:");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            System.out.println("Server stopped");
        }
    }
}
