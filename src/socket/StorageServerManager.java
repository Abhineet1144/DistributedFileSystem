package socket;

import meta.FileMeta;
import meta.FileType;
import meta.handler.AbstractFileHandlerMeta;
import properties.Property;
import util.SocketIO;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

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

    public void createDirectory(FileMeta parent, String folderName) throws IOException {
        Collection<FileMeta> children = AbstractFileHandlerMeta.getInstance().getChildren(parent);
        for (FileMeta child : children) {
            if (folderName.equals(child.getFileName())) {
                System.out.println("Folders cannot have duplicate names.");
                return;
            }
        }
        FileMeta dir = new FileMeta(folderName, FileType.FOLDER, 0, System.currentTimeMillis(), parent, "");
        AbstractFileHandlerMeta.getInstance().setFileMeta(dir);
        System.out.println("Successfully created folder: " + folderName);
    }

    public void uploadFile(FileMeta parent, String fileName, SocketIO socketIO, long len) throws IOException {
        Collection<FileMeta> children = AbstractFileHandlerMeta.getInstance().getChildren(parent);
        for (FileMeta child : children) {
            if (fileName.equals(child.getFileName())) {
                System.out.println("Files cannot have duplicate names.");
                return;
            }
        }
        parent.increaseSize(len);
        FileMeta file = new FileMeta(fileName, FileType.FILE, len, System.currentTimeMillis(), parent, "");

        Entry<String, SocketIO> storageSocketEntry = getFreeServerSocket(len);
        SocketIO storageSocket = storageSocketEntry.getValue();
        if (storageSocket == null) {
            throw new IOException("No storage available for storing");
        }
        file.setIpport(storageSocketEntry.getKey());
        AbstractFileHandlerMeta.getInstance().setFileMeta(file);
        storageSocket.sendTextAndRecieveResp("file-creation");
        storageSocket.sendText(String.valueOf(file.getId()));
        socketIO.receiveInputStream(storageSocket, len);
    }

    public void deleteFile(String absPath) throws IOException {
        FileMeta meta = AbstractFileHandlerMeta.getInstance().getFileMetaForAbsPath(absPath);
        if (meta.getType() == FileType.FOLDER) {
            Collection<FileMeta> children = AbstractFileHandlerMeta.getInstance().getChildren(meta);
            for (FileMeta child : children) {
                deleteFile(child.getAbsolutePath());
            }
            AbstractFileHandlerMeta.getInstance().deleteFileMeta(meta.getId());
            return;
        }
        SocketIO storageSocket = SocketIO.getSocketIO(meta.getIpport());
        if (storageSocket == null) {
            throw new IOException("Incorrect ipPort or server is down.");
        }
        storageSocket.sendTextAndRecieveResp("delete");
        String storageServerPath = storageSocket.receiveText();
        long ID = meta.getId();
        File file = new File(storageServerPath + File.separatorChar + ID + ".dat");
        if (file.delete()) storageSocket.sendText("Successfully deleted:" + absPath);
        else return;
        AbstractFileHandlerMeta.getInstance().deleteFileMeta(ID);
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

    public Entry<String, SocketIO> getFreeServerSocket(long availableRequired) {
        String[] ipPorts = Property.getStorageServers();

        for (String ipport : ipPorts) {
            SocketIO socketIO;
            try {
                socketIO = SocketIO.getSocketIO(ipport);
                socketIO.sendText("check-available");
                if (socketIO.sendAndCheckSuccess(availableRequired)) {
                    return Map.entry(ipport, socketIO);
                }
                socketIO.close();
                System.out.println("No storage available: " + ipport);
            } catch (IOException e) {
                System.out.println("Couldn't connect to storage server: " + ipport);
            }
        }

        throw new IllegalStateException("No storage available in all configured servers");
    }
}
