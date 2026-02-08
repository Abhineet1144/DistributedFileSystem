package socket;

import meta.FileMeta;
import meta.handler.MapFileMetaHandler;
import util.SocketIO;
import util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ControlRequestHandler implements Runnable {
    private SocketIO operationsRequester;

    public ControlRequestHandler(Socket requester) throws IOException {
        this.operationsRequester = new SocketIO(requester);
    }

    @Override
    public void run() {
        try {
            long len;
            FileMeta parent;
            String fileName;
            String[] params;
            String parentPath;

            String operation = operationsRequester.recieveText();
            switch (operation) {
                case "mkdir":
                    params = operation.split(":");
                    parentPath = Utils.getCleansedPath(params[0]);
                    fileName = params[1];
                    parent = MapFileMetaHandler.getInstance().getFileMetaForAbsPath(parentPath);
                    StorageServerManager.getInstance().createDirectory(parent, fileName);
                    break;
                case "upload-file":
                    params = operation.split(":");
                    parentPath = Utils.getCleansedPath(params[0]);
                    fileName = params[1];
                    parent = MapFileMetaHandler.getInstance().getFileMetaForAbsPath(parentPath);
                    len = operationsRequester.getStreamSize();
                    operationsRequester.receiveInputStream(operationsRequester, len);
                    StorageServerManager.getInstance().uploadFile(parent, fileName, null);
                    break;
                case "delete-file":
                    break;
                case "download-file":
                    break;
                default:
                    operationsRequester.sendText("Method not implemented: " + operation);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                operationsRequester.close();
            } catch (IOException ignored) {
            }
        }
    }
}
