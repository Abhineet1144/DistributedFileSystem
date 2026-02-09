package socket;

import util.SocketIO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class StorageRequestHandler implements Runnable {
    private SocketIO operationsRequester;
    String absoluteRootPath = "root\\";

    public StorageRequestHandler(Socket requester) throws IOException {
        this.operationsRequester = new SocketIO(requester);
    }

    @Override
    public void run() {
        try {
            String operation = operationsRequester.receiveText();
            System.out.println("Received: " + operation);

            switch (operation) {
                case "check-available":
                    operationsRequester.sendText("ok");
                    break;
                case "file-creation":
                    operationsRequester.sendText("ok");
                    String id = operationsRequester.receiveText();
                    long len = operationsRequester.getStreamSize();
                    operationsRequester.sendText("ok");
                    File file = new File(absoluteRootPath + id + ".dat");
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        operationsRequester.receiveInputStream(fos, len);
                    }
                    operationsRequester.sendText("ok");
                    break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}