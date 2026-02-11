package socket;

import meta.FileMeta;
import meta.FileType;
import meta.handler.AbstractFileHandlerMeta;
import properties.Property;
import util.SocketIO;
import util.Utils;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;

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

    public void deleteFile(String absPath) throws IOException {
        FileMeta meta = AbstractFileHandlerMeta.getInstance().getFileMetaForAbsPath(absPath);
        if (meta.getType() == FileType.FOLDER) {
            Collection<FileMeta> children = AbstractFileHandlerMeta.getInstance().getChildren(meta);
            for (FileMeta child : children) {
                deleteFile(child.getAbsolutePath());
            }
            AbstractFileHandlerMeta.getInstance().deleteFileMeta(meta.getId());
        }
        SocketIO storageSocket = connectTo(meta.getIpport());
        if (storageSocket == null) {
            throw new IOException("Incorrect ipPort or server is down");
        }
        storageSocket.sendTextAndRecieveResp("delete");
        String storageServerPath = storageSocket.receiveText();
        long ID = meta.getId();
        File file = new File(storageServerPath + File.separatorChar + ID + ".dat");
        if (file.delete()) storageSocket.sendText("Successfully deleted:" + absPath);
        else return;
        AbstractFileHandlerMeta.getInstance().deleteFileMeta(ID);
    }

    public void uploadFile(FileMeta parent, String name, SocketIO socketIO, long len) throws IOException {
        parent.increaseSize(len);
        FileMeta file = new FileMeta(name, FileType.FILE, len, System.currentTimeMillis(), parent, "");

        SocketIO storageSocket = getFreeServerSocket(len);
        if (storageSocket == null) {
            throw new IOException("No storage available for storing");
        }
        file.setIpport((storageSocket.getSocket().getInetAddress() + ":" + storageSocket.getSocket().getPort()).replace("/", ""));
        System.out.println(storageSocket.getSocket().getInetAddress() + ":" + storageSocket.getSocket().getPort());
        AbstractFileHandlerMeta.getInstance().setFileMeta(file);
        storageSocket.sendTextAndRecieveResp("file-creation");
        storageSocket.sendText(String.valueOf(file.getId()));
        socketIO.receiveInputStream(storageSocket, len);
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


    public SocketIO connectTo(String ipPort) throws IOException {
        SocketIO socketIO = null;
        String[] ipPorts = Property.getStorageServers();
        String[] paths = Property.getStoragePaths();

        if (ipPorts.length != paths.length) {
            return null;
        }

        if (ipPort != null) {
            socketIO = SocketIO.getSocketIO(ipPort);
            socketIO.sendText("get-server");
            if (socketIO.sendAndCheckSuccess(Utils.findPathFromIPs(ipPorts, paths, ipPort))) {
                return socketIO;
            }
        }
        assert socketIO != null;
        socketIO.close();
        System.out.println("Cannot find the ipPort: " + ipPort);
        return null;
    }

    public SocketIO getFreeServerSocket(long availableRequired) {
        String[] ipPorts = Property.getStorageServers();
        String[] paths = Property.getStoragePaths();

        int currIndex = 0;
        // Each ipPorts should have its own path.
        for (String ipport : ipPorts) {
            SocketIO socketIO = null;
            try {
                socketIO = SocketIO.getSocketIO(ipport.split(":"));
                socketIO.sendText("check-available");
                socketIO.sendText(paths[currIndex]);
                if (socketIO.sendAndCheckSuccess(availableRequired)) {
                    // only send its respective path.
                    return socketIO;
                }
                socketIO.close();
                System.out.println("No storage available: " + ipport);
            } catch (IOException e) {
                System.out.println("Couldn't connect to storage server: " + ipport);
            }
            currIndex++;
        }
        return null;
    }
}
