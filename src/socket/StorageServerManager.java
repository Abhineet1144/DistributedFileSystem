package socket;

import meta.FileMeta;
import meta.FileType;
import meta.handler.AbstractFileHandlerMeta;
import meta.handler.MapFileMetaHandler;
import properties.Property;
import util.SocketIO;

import java.io.IOException;
import java.net.Socket;

public class StorageServerManager {
    private static final String RESP_OK = "ok";
    private StorageServerManager() {
    }

    public static StorageServerManager getInstance() {
        return new StorageServerManager();
    }

    public SocketIO initSocket(String[] ipPort) throws IOException {
        Socket socket = new Socket(ipPort[0], Integer.parseInt(ipPort[1]));
        return new SocketIO(socket);
    }

    public String list(FileMeta folder) {
        String children = "";
        for (FileMeta child : AbstractFileHandlerMeta.getInstance().getChildren(folder)) {
            children += child.getFileName() + "\"";
        }
        return children;
    }

    public void createDirectory(FileMeta parent, String name) throws IOException {
        FileMeta dir = new FileMeta(name, FileType.FOLDER, 0, System.currentTimeMillis(), parent, "");
        AbstractFileHandlerMeta.getInstance().setFileMeta(dir);
    }

    public void uploadFile(FileMeta parent, String name, SocketIO socketIO) throws IOException {
        long len = socketIO.getStreamSize();
        parent.increaseSize(len);
        FileMeta file = new FileMeta(name, FileType.FILE, len, System.currentTimeMillis(), parent, "");

        SocketIO storageSocket = getFreeServerIp(len);
        if (storageSocket == null) {
            throw new IOException("No storage available for storing");
        }
        storageSocket.sendText("file-creation");
        storageSocket.sendText(String.valueOf(file.getId()));
        storageSocket.sendText("in:" + len);
        socketIO.sendInputStream(storageSocket.fileInputStream, len);
        storageSocket.fileOutputStream.flush();
    }

    public void deleteFile(long id) {
        AbstractFileHandlerMeta.getInstance().deleteFileMeta(id);
    }

    public SocketIO getFreeServerIp(long availableRequired) {
        String[] ipports = Property.getStorageServers();
        for (String ipport : ipports) {
            SocketIO socketIO = null;
            try {
                socketIO = initSocket(ipport.split(":"));
                String resp = socketIO.sendTextAndRecieveResp("check-available");
                if (RESP_OK.equals(resp)) {
                    return socketIO;
                }
            } catch (IOException e) {
                System.out.println("Couldn't connect to storage server: " + ipport);
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}
