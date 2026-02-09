package socket;

import util.SocketIO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class StorageRequestHandler implements Runnable {
    private SocketIO operationsRequester;
    String absoluteRootPath = "/home/tejas/Projects/DistributedFileSystem/root/";

    public StorageRequestHandler(Socket requester) throws IOException {
        this.operationsRequester = new SocketIO(requester);
    }

    @Override
    public void run() {
        try {
            while (true) {
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
                        File file = new File(absoluteRootPath + id + ".dat");
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            operationsRequester.sendText("ok");
                            operationsRequester.receiveInputStream(fos, len);
                        }
                        operationsRequester.close();
                        break;
                    default:
                        operationsRequester.sendText("Method not implemented: " + operation);
                }
            }
        } catch (SocketException e) {
            System.out.println("Client disconnected: " + operationsRequester);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                operationsRequester.close();
            } catch (IOException ignored) {}
        }
    }
}