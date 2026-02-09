package socket;

import util.SocketIO;

import java.io.IOException;
import java.net.Socket;

public class StorageRequestHandler implements Runnable {
    private SocketIO operationsRequester;

    public StorageRequestHandler(Socket requester) throws IOException {
        this.operationsRequester = new SocketIO(requester);
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("Received: " + operationsRequester.receiveText());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
