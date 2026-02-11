package socket;

import meta.FileMeta;
import meta.FileType;
import meta.handler.AbstractFileHandlerMeta;
import properties.Property;
import util.SocketIO;

import java.io.IOException;
import java.net.Socket;

public class StorageServerManager {
    private StorageServerManager() {
    }

    public static StorageServerManager getInstance() {
        return new StorageServerManager();
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

    public void uploadFile(FileMeta parent, String name, SocketIO socketIO, long len) throws IOException {
        parent.increaseSize(len);
        FileMeta file = new FileMeta(name, FileType.FILE, len, System.currentTimeMillis(), parent, "");

        SocketIO storageSocket = getFreeServerSocket(len);
        if (storageSocket == null) {
            throw new IOException("No storage available for storing");
        }
        file.setIpport(storageSocket.getSocket().getInetAddress() + ":" + storageSocket.getSocket().getPort());
        System.out.println(storageSocket.getSocket().getInetAddress() + ":" + storageSocket.getSocket().getPort());
        AbstractFileHandlerMeta.getInstance().setFileMeta(file);
        storageSocket.sendTextAndRecieveResp("file-creation");
        storageSocket.sendText(String.valueOf(file.getId()));
        socketIO.receiveInputStream(storageSocket, len);
    }

    public void deleteFile(long id) {
        AbstractFileHandlerMeta.getInstance().deleteFileMeta(id);
    }

    public void downloadFile(String absPath, SocketIO requesterSocket) throws IOException {
        FileMeta meta = AbstractFileHandlerMeta.getInstance().getFileMetaForAbsPath(absPath);
        String ipport = meta.getIpport();
        SocketIO socketIO = SocketIO.getSocketIO(ipport);
        socketIO.sendText("download");
        if (socketIO.sendAndCheckSuccess(meta.getId())) {
            long len = socketIO.getStreamSize();
            socketIO.receiveInputStream(requesterSocket, len);
        }
    }

    public SocketIO getFreeServerSocket(long availableRequired) {
        String[] ipPorts = Property.getStorageServers();

        int currIndex = 0;
        // Each ipPorts should have its own path.
        for (String ipport : ipPorts) {
            SocketIO socketIO;
            try {
                socketIO = SocketIO.getSocketIO(ipport);
                socketIO.sendText("check-available");
                if (socketIO.sendAndCheckSuccess(availableRequired)) {
                    return socketIO;
                }
                socketIO.close();
                System.out.println("No storage available: " + ipport);
            } catch (IOException e) {
                System.out.println("Couldn't connect to storage server: " + ipport);
            }
            currIndex++;
        }

        throw new IllegalStateException("No storage available in configured all servers");
    }
}
