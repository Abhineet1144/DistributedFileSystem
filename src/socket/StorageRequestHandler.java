package socket;

import util.SocketIO;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class StorageRequestHandler implements Runnable {
    private SocketIO storageServerRequest;
    String absoluteRootPath = null;

    public StorageRequestHandler(Socket requester) throws IOException {
        this.storageServerRequest = new SocketIO(requester);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String operation = storageServerRequest.receiveText();
                System.out.println("Received: " + operation);

                File file;
                String id;

                switch (operation) {
                    case "check-available":
                        absoluteRootPath = storageServerRequest.receiveText();
                        String requestedSize = storageServerRequest.receiveText();
                        File rootDir = new File(absoluteRootPath);
                        long availableSize = rootDir.getUsableSpace();
                        if (Long.parseLong(requestedSize) > availableSize) {
                            storageServerRequest.sendFailureResp();
                            break;
                        }
                        storageServerRequest.sendOkResp();
                        break;
                    case "get-server":
                        absoluteRootPath = storageServerRequest.receiveText();
                        if (absoluteRootPath == null) {
                            storageServerRequest.sendFailureResp();
                            break;
                        }
                        storageServerRequest.sendOkResp();
                        break;
                    case "file-creation":
                        storageServerRequest.sendOkResp();
                        id = storageServerRequest.receiveText();
                        long len = storageServerRequest.getStreamSize();
                        file = getFile(id + ".dat");
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            storageServerRequest.sendOkResp();
                            storageServerRequest.receiveInputStream(fos, len);
                        }
                        storageServerRequest.close();
                        break;
                    case "delete":
                        storageServerRequest.sendOkResp();
                        storageServerRequest.sendText(absoluteRootPath);
                        storageServerRequest.receiveText();
                        storageServerRequest.close();
                        break;
                    case "download":
                        id = storageServerRequest.receiveText();
                        file = getFile(id + ".dat");
                        if (file.exists()) {
                            storageServerRequest.sendOkResp();
                            storageServerRequest.sendInputStream(new BufferedInputStream(new FileInputStream(file)), file.length());
                        } else {
                            storageServerRequest.sendText("failed");
                        }
                    default:
                        storageServerRequest.sendText("Method not implemented: " + operation);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Client disconnected");
        } finally {
            try {
                storageServerRequest.close();
            } catch (IOException ignored) {
            }
        }
    }

    private File getFile(String name) {
        return new File(absoluteRootPath + File.separatorChar + name);
    }
}