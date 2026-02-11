package socket;

import meta.FileMeta;
import meta.handler.AbstractFileHandlerMeta;
import util.SocketIO;
import util.Utils;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class ControlRequestHandler implements Runnable {
    private SocketIO operationsRequester;

    public ControlRequestHandler(Socket requester) throws IOException {
        this.operationsRequester = new SocketIO(requester);
    }

    @Override
    public void run() {
        try {
            while (true) {
                long len;
                StringBuilder fileTree;
                FileMeta dir;
                FileMeta parent;
                String fileName;
                String[] params;
                String parentPath;

                String operation = operationsRequester.receiveText();
                System.out.println("Received oper req: " + operation);
                params = operationsRequester.receiveText().split(":");
                System.out.println("Received params: " + Arrays.toString(params));
                switch (operation) {
                case "list-all":
                    fileTree = new StringBuilder();
                    fileName = params[0];
                    dir = AbstractFileHandlerMeta.getInstance().getFileMetaForAbsPath(fileName);
                    AbstractFileHandlerMeta.getInstance().getFolderTree(dir, fileTree);
                    operationsRequester.sendText(fileTree.toString());
                case "list":
                    fileName = params[0];
                    dir = AbstractFileHandlerMeta.getInstance().getFileMetaForAbsPath(fileName);
                    operationsRequester.sendText(StorageServerManager.getInstance().list(dir));
                    break;
                case "mkdir":
                    parentPath = Utils.getCleansedPath(params[0]);
                    fileName = params[1];
                    parent = AbstractFileHandlerMeta.getInstance().getFileMetaForAbsPath(parentPath);
                    StorageServerManager.getInstance().createDirectory(parent, fileName);
                    break;
                case "upload-file":
                    parentPath = Utils.getCleansedPath(params[0]);
                    fileName = params[1];
                    parent = AbstractFileHandlerMeta.getInstance().getFileMetaForAbsPath(parentPath);
                    len = operationsRequester.getStreamSize();
                    StorageServerManager.getInstance().uploadFile(parent, fileName, operationsRequester, len);
                    break;
                case "delete-file":
                    fileName = params[0];
                    StorageServerManager.getInstance().deleteFile(fileName);
                    break;
                case "download-file":
                    fileName = params[0];
                    StorageServerManager.getInstance().downloadFile(fileName, operationsRequester);
                    break;
                default:
                    operationsRequester.sendText("Method not implemented: " + operation);
                }
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
