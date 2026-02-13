package socket;

import meta.FileMeta;
import meta.handler.AbstractFileHandlerMeta;
import socket.handler.AbstractRequestHandler;
import util.SocketIO;
import util.Utils;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class ControlRequestHandler extends AbstractRequestHandler {

    public ControlRequestHandler(Socket requester) throws IOException {
        super(requester);
    }

    @Override
    protected void handleOperation(String operation) throws IOException {
        long len;
        StringBuilder fileTree;
        FileMeta dir;
        FileMeta parent;
        String fileName;
        String[] params = new String[2];
        String parentPath;

        System.out.println("Received oper req: " + operation);
        params = socketIO.receiveText().split(":");
        System.out.println("Received params: " + Arrays.toString(params));

        switch (operation) {
            case "list-all":
                fileTree = new StringBuilder();
                fileName = params[0];
                dir = AbstractFileHandlerMeta.getInstance().getFileMetaForAbsPath(fileName);
                AbstractFileHandlerMeta.getInstance().getFolderTree(dir, fileTree);
                socketIO.sendText(fileTree.toString());
                break;
            case "list":
                fileName = params[0];
                dir = AbstractFileHandlerMeta.getInstance().getFileMetaForAbsPath(fileName);
                socketIO.sendText(StorageServerManager.getInstance().list(dir));
                break;
            case "mkdir":
                parentPath = Utils.getCleansedPath(params[0]);
                fileName = params[1];
                parent = AbstractFileHandlerMeta.getInstance().getFileMetaForAbsPath(parentPath);
                StorageServerManager.getInstance().createDirectory(parent, fileName);
                socketIO.sendOkResp();
                break;
            case "exists":
                parentPath = Utils.getCleansedPath(params[0]);
                fileName = params[1];
                parent = AbstractFileHandlerMeta.getInstance().getFileMetaForAbsPath(parentPath);
                boolean exists = StorageServerManager.getInstance().exists(parent, fileName);
                socketIO.sendText(exists);

            case "upload-file":
                parentPath = Utils.getCleansedPath(params[0]);
                fileName = params[1];
                parent = AbstractFileHandlerMeta.getInstance().getFileMetaForAbsPath(parentPath);
                len = socketIO.getStreamSize();
                StorageServerManager.getInstance().uploadFile(parent, fileName, socketIO, len);
                break;
            case "delete-file":
                fileName = params[0];
                StorageServerManager.getInstance().deleteFile(fileName);
                break;
            case "download-file":
                fileName = params[0];
                StorageServerManager.getInstance().downloadFile(fileName, socketIO);
                break;
            default:
                socketIO.sendText("Method not implemented: " + operation);
        }
    }
}
