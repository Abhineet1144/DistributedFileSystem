package util;

import java.io.*;
import java.net.Socket;

public class SocketIO {
    private static final String OKAY_RESP = "ok";

    private Socket socket;
    private BufferedReader textInputStream;
    private InputStream fileInputStream;
    private PrintWriter textOutputStream;
    private OutputStream fileOutputStream;

    public SocketIO(Socket socket) throws IOException {
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        socket.setSendBufferSize(256 * 1024);
        socket.setReceiveBufferSize(256 * 1024);
        this.socket = socket;
        this.textInputStream = new BufferedReader(new InputStreamReader(in));
        this.fileInputStream = new BufferedInputStream(in);
        this.textOutputStream = new PrintWriter(out, true);
        this.fileOutputStream = new BufferedOutputStream(out);
    }

    public static SocketIO getSocketIO(String ipPort) throws IOException {
        return getSocketIO(ipPort.split(":"));
    }

    public static SocketIO getSocketIO(String[] ipPort) throws IOException {
        Socket socket = new Socket(ipPort[0], Integer.parseInt(ipPort[1]));
        return new SocketIO(socket);
    }

    public Socket getSocket() { return socket; }

    public String sendTextAndRecieveResp(Object text) throws IOException {
        sendText(text);
        return receiveText();
    }

    public void sendText(Object text) {
        textOutputStream.println(text);
        System.out.println("Sent: " + text);
    }

    public void sendOkResp() {
        sendText(OKAY_RESP);
    }

    public boolean checkForOkResp() throws IOException {
        return OKAY_RESP.equals(receiveText());
    }

    public boolean sendAndCheckSuccess(Object text) throws IOException {
        return OKAY_RESP.equals(sendTextAndRecieveResp(text));
    }

    public String receiveText() throws IOException {
        while (true) {
            String line = textInputStream.readLine();
            if (line != null) {
                return line;
            }
        }
    }

    public void sendInputStream(InputStream in, long size) throws IOException {
        String resp = setStreamSize(size);
        if (OKAY_RESP.equals(resp)) {
            in.transferTo(fileOutputStream);
            fileOutputStream.flush();
        }
    }

    public String setStreamSize(long size) throws IOException {
        return sendTextAndRecieveResp("in:" + size);
    }

    public long getStreamSize() throws IOException {
        long size = Long.parseLong(receiveText().replace("in:", ""));
        sendOkResp();
        return size;
    }

    public void receiveInputStream(OutputStream out, long contentSize) throws IOException {
        byte[] buffer = new byte[64 * 1024];
        long remaining = contentSize;

        while (remaining > 0) {
            int read = fileInputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining));
            if (read == -1) {
                throw new EOFException(
                        "Stream ended early; missing " + remaining + " bytes");
            }
            out.write(buffer, 0, read);
            remaining -= read;
        }
        out.flush();
    }

    public void receiveInputStream(SocketIO socketIO, long contentSize) throws IOException {
        socketIO.setStreamSize(contentSize);
        receiveInputStream(socketIO.fileOutputStream, contentSize);
    }

    public void close() throws IOException {
        textOutputStream.close();
        textInputStream.close();
        socket.close();
    }
}
