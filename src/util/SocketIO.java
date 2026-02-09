package util;

import java.io.*;
import java.net.Socket;

public class SocketIO {
    private static final String OKAY_RESP = "ok";

    private Socket socket;
    private BufferedReader textInputStream;
    public InputStream fileInputStream;
    private PrintWriter textOutputStream;
    public OutputStream fileOutputStream;

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

    public String sendTextAndRecieveResp(String text) throws IOException {
        sendText(text);
        return receiveText();
    }

    public void sendText(String text) {
        textOutputStream.println(text);
    }

    // Receives text from Tomcat Server, like MKDIR and LIST
    public String receiveText() throws IOException {
        while (true) {
            String line = textInputStream.readLine();
            if (line != null) {
                return line;
            }
        }
    }

    public void sendInputStream(InputStream in, long size) throws IOException {
        String resp = sendTextAndRecieveResp("in:" + size);
        if (OKAY_RESP.equals(resp)) {
            in.transferTo(fileOutputStream);
            fileOutputStream.flush();
        }
    }

    public long getStreamSize() throws IOException {
        return Long.parseLong(receiveText().replace("in:", ""));
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
        receiveInputStream(socketIO.fileOutputStream, contentSize);
    }

    public void close() throws IOException {
        textOutputStream.close();
        fileInputStream.close();
        socket.close();
    }
}
