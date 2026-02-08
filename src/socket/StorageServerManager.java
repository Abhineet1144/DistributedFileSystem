package socket;

import util.SocketIO;

import java.io.IOException;
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
}
