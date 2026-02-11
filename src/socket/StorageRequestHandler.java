package socket;

import properties.Property;
import util.SocketIO;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class StorageRequestHandler implements Runnable {
    private SocketIO controlServerRequest;

    public StorageRequestHandler(Socket requester) throws IOException {
        this.controlServerRequest = new SocketIO(requester);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String operation = controlServerRequest.receiveText();
                System.out.println("Received: " + operation);

                File file;
                String id;

                switch (operation) {
                    case "check-available":
                        String requestedSize = controlServerRequest.receiveText();
                        File rootDir = new File(Property.getStoragePath());
                        long availableSize = rootDir.getUsableSpace();
                        if (Long.parseLong(requestedSize) > availableSize) {
                            controlServerRequest.sendText("failed");
                            break;
                        }
                        controlServerRequest.sendOkResp();
                        break;
                    case "file-creation":
                        controlServerRequest.sendOkResp();
                        id = controlServerRequest.receiveText();
                        long len = controlServerRequest.getStreamSize();
                        file = getFile(id + ".dat");
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            controlServerRequest.sendOkResp();
                            controlServerRequest.receiveInputStream(fos, len);
                        }
                        controlServerRequest.close();
                        break;
                    case "download":
                        id = controlServerRequest.receiveText();
                        file = getFile(id + ".dat");
                        if (file.exists()) {
                            controlServerRequest.sendOkResp();
                            controlServerRequest.sendInputStream(new BufferedInputStream(new FileInputStream(file)),
                                    file.length());
                        } else {
                            controlServerRequest.sendText("failed");
                        }
                    default:
                        controlServerRequest.sendText("Method not implemented: " + operation);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Client disconnected");
        } finally {
            try {
                controlServerRequest.close();
            } catch (IOException ignored) {}
        }
    }

    private File getFile(String name) {
        return new File(Property.getStoragePath() + File.separatorChar + name);
    }
}