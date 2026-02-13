package main;

import util.SocketIO;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class GatewayServer {
    private static SocketIO socketIO;
    private static String tempFolder = "/UploadedChunkStorage";
    private static final String sep = ":";

    public GatewayServer(String host, int port) throws IOException {
        socketIO = new SocketIO(new Socket(host, port));
    }

    public static void createTemp(String uploadID) throws IOException {
        boolean resp = socketIO.sendAndCheckSuccess("mkdir");
        socketIO.sendText(tempFolder + sep + uploadID);
        if (!resp) {
            return;
        }
    }

    public static void uploadChunks(String parent, String fileName, String uploadID, ByteArrayInputStream byteArrayInputStream, int len) throws IOException {
        socketIO.sendText("upload-file");
        socketIO.sendText(tempFolder + File.separatorChar + parent + sep + fileName);
        socketIO.sendInputStream(byteArrayInputStream, len);
    }
}
