package socket;

import util.SocketIO;

import java.io.IOException;
import java.net.Socket;

public class ControlRequestHandler implements Runnable {
    private SocketIO requesterOperations;

    public ControlRequestHandler(Socket requester) throws IOException {
        this.requesterOperations = new SocketIO(requester);
    }

    @Override
    public void run() {

    }
}
