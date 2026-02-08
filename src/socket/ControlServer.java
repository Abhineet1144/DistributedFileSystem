package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import properties.Property;

public class ControlServer {
    private static final String CONTROLLER = "controller";

    private static boolean started = false;

    public static void start() {
        if (!Property.getMode().equals(CONTROLLER)) {
            System.out.println("Current mode is not controller");
            return;
        } else if (started) {
            System.out.println("Already started");
            return;
        }

        started = true;
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(Property.getPort()))) {
            System.out.println("Started server in port: " + Property.getPort());
            while (true) {
                try (Socket requester = serverSocket.accept()) {
                    System.out.println("Recieved request from: " + requester.getInetAddress());
                    Executors.newSingleThreadExecutor().execute(new ControlRequestHandler(requester));
                } catch (IOException e) {
                    System.out.println("Exception connecting to requester:");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Error in Socket creation:");
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            System.out.println("Error in port format:");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            System.out.println("Server stopped");
        }
    }
}
