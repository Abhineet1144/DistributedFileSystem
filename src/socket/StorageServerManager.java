package socket;

import meta.FileMeta;
import meta.FileType;
import meta.handler.AbstractFileHandlerMeta;
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

    public void uploadFile(FileMeta parent, String name, SocketIO socketIO, long len) throws IOException {
        parent.increaseSize(len);
        FileMeta file = new FileMeta(name, FileType.FILE, len, System.currentTimeMillis(), parent, "");

        SocketIO storageSocket = getFreeServerIp(len);
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

    public SocketIO getFreeServerIp(long availableRequired) {
        String[] ipPorts = Property.getStorageServers();
        String[] paths = Property.getStoragePaths();

        if (ipPorts.length != paths.length) {
            return null;
        }

        int currIndex = 0;
        // Each ipPorts should have its own path.
        for (String ipport : ipPorts) {
            SocketIO socketIO = null;
            try {
                socketIO = initSocket(ipport.split(":"));
                socketIO.sendText("check-available");
                socketIO.sendText(paths[currIndex]);
                String resp = socketIO.sendTextAndRecieveResp(String.valueOf(availableRequired));
                if (RESP_OK.equals(resp)) {
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
