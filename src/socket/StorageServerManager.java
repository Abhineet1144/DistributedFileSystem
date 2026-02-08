package socket;

import meta.FileMeta;
import meta.FileType;
import meta.handler.MapFileMetaHandler;
import util.SocketIO;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class StorageServerManager {
    private StorageServerManager() {
        
    }

    public static StorageServerManager getInstance() {
        return new StorageServerManager();
    }

    public SocketIO initSockets(String ipport) throws IOException {
        String[] ipPort = ipport.split(":");
        Socket socket = new Socket(ipPort[0], Integer.parseInt(ipPort[1]));
        return new SocketIO(socket);
    }

    public void createDirectory(FileMeta parent, String name) {
        FileMeta dir = new FileMeta(name, FileType.FOLDER, 0, System.currentTimeMillis(), parent);
        MapFileMetaHandler.getInstance().setFileMeta(dir);
    }

    public void uploadFile(FileMeta parent, String name, InputStream inputStream) throws IOException {
        parent.increaseSize(inputStream.available());
        FileMeta file = new FileMeta(name, FileType.FILE, inputStream.available(), System.currentTimeMillis(), parent);
        MapFileMetaHandler.getInstance().setFileMeta(file);
    }

    public void deleteFile(long ID) {
        MapFileMetaHandler.getInstance().deleteFileMeta(ID);
    }
}
