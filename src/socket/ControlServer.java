package socket;

import java.io.IOException;
import java.net.ServerSocket;

import properties.Property;

public class ControlServer {
    private static final String CONTROLLER = "controller";

    private static boolean started = false;

    public void start() {
        if (!Property.getMode().equals(CONTROLLER)) {
            System.out.println("Current mode is not controller");
            return;
        } else if (started) {
            System.out.println("Already started");
            return;
        }

        started = true;
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(Integer.parseInt(Property.getPort()));
        } catch (IOException e) {
            System.out.println("Error in Socket creation:");
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            System.out.println("Error in port format:");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
