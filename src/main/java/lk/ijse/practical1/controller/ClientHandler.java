package lk.ijse.practical1.controller;

import lk.ijse.practical1.util.FileUtil;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ServerController server;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String username;

    public ClientHandler(Socket socket, ServerController server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            this.username = dis.readUTF();
            server.broadcastText("[SYSTEM]: " + username + " entered the chat room.", null);

            while (true) {
                int dataType = dis.readInt();
                if (dataType == FileUtil.TYPE_TEXT) {
                    String message = dis.readUTF();
                    String formattedMessage = username + ": " + message;
                    server.broadcastText(formattedMessage, this);
                } else if (dataType == FileUtil.TYPE_FILE) {
                    String fileName = dis.readUTF();
                    long size = dis.readLong();
                    byte[] fileBytes = FileUtil.readFileToByteArray(dis, size);
                    String detailedFileName = username + "_sent_" + fileName;
                    server.broadcastFile(detailedFileName, size, fileBytes, this);
                }
            }
        } catch (IOException e) {
            server.removeClient(this);
            server.broadcastText("[SYSTEM]: " + username + " logged off.", null);
        }
    }

    public void sendRawText(String msg) {
        try { FileUtil.sendText(msg, dos); } catch (IOException ignored) {}
    }

    public void sendRawFile(String name, long size, byte[] data) {
        try {
            dos.writeInt(FileUtil.TYPE_FILE);
            dos.writeUTF(name);
            dos.writeLong(size);
            dos.write(data);
            dos.flush();
        } catch (IOException ignored) {}
    }
}