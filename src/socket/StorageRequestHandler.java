package socket;

import properties.Property;
import socket.handler.AbstractRequestHandler;
import util.SocketIO;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class StorageRequestHandler extends AbstractRequestHandler {

    public StorageRequestHandler(Socket requester) throws IOException {
        super(requester);
    }

    @Override
    protected void handleOperation(String operation) throws IOException {
        File file;
        String id;
        long len;

        switch (operation) {
            case "check-available":
                String requestedSize = socketIO.receiveText();
                File rootDir = new File(Property.getStoragePath());
                long availableSize = rootDir.getUsableSpace();
                if (Long.parseLong(requestedSize) > availableSize) {
                    socketIO.sendText("failed");
                    break;
                }
                socketIO.sendOkResp();
                break;
            case "file-creation":
                socketIO.sendOkResp();
                id = socketIO.receiveText();
                len = socketIO.getStreamSize();
                file = getFile(id + ".dat");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    socketIO.sendOkResp();
                    socketIO.receiveInputStream(fos, len);
                }
                socketIO.close();
                break;
            case "unChunk":
                socketIO.sendOkResp();
                String fileName = socketIO.receiveText();
                len = socketIO.getStreamSize();
                file = getFile(fileName);
                FileOutputStream fos = new FileOutputStream(file, true);
                socketIO.sendOkResp();
                socketIO.receiveInputStream(fos, len);

                socketIO.close();
                break;
            case "delete":
                socketIO.sendOkResp();
                socketIO.sendText(Property.getStoragePath());
                socketIO.receiveText();
                socketIO.close();
                break;
            case "download":
                id = socketIO.receiveText();
                file = getFile(id + ".dat");
                if (file.exists()) {
                    socketIO.sendOkResp();
                    socketIO.sendInputStream(new BufferedInputStream(new FileInputStream(file)), file.length());
                } else {
                    socketIO.sendText("failed");
                }
                socketIO.close();
                break;
            default:
                socketIO.sendText("Method not implemented: " + operation);
        }
    }

    private File getFile(String name) {
        return new File(Property.getStoragePath() + File.separatorChar + name);
    }
}