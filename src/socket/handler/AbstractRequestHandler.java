package socket.handler;

import util.SocketIO;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractRequestHandler implements Runnable {
    protected SocketIO socketIO;

    public AbstractRequestHandler(Socket requester) throws IOException {
        this.socketIO = new SocketIO(requester);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String operation = socketIO.receiveText();
                handleOperation(operation);
            }
        } catch (IOException e) {
            System.out.println("Client Disconnected...");
        } finally {
            try {
                socketIO.close();
            } catch (IOException ignored) {
            }
        }
    }

    protected abstract void handleOperation(String operation) throws IOException;
}
