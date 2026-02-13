package main;

import util.SocketIO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class GatewayServer {
    private static SocketIO socketIO;
    private static String tempFolder = "/UploadedChunkStorage";
    private static final String sep = ":";

    public GatewayServer(String host, int port) throws IOException {
        socketIO = new SocketIO(new Socket(host, port));
    }

    public static boolean exists(String parent, String fileName) throws IOException {
        socketIO.sendText("exists");
        socketIO.sendText(parent + sep + fileName);
        return Boolean.parseBoolean(socketIO.receiveText());
    }

    public static void createTemp(String uploadID) throws IOException {
        socketIO.sendText("mkdir");
        socketIO.sendText(tempFolder + sep + uploadID);
    }

    public static void uploadChunks(String fileName, String uploadID, InputStream inputStream, int len) throws IOException {
        socketIO.sendText("upload-file");
        socketIO.sendText(tempFolder + File.separatorChar + uploadID + sep + fileName);
        socketIO.sendInputStream(inputStream, len);
    }
}
