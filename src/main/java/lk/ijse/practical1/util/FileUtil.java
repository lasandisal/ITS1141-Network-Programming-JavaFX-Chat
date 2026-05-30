package lk.ijse.practical1.util;

import java.io.*;

public class FileUtil {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_FILE = 1;
    private static final int BUFFER_SIZE = 4096;

    public static void sendText(String msg, DataOutputStream dos) throws IOException {
        dos.writeInt(TYPE_TEXT);
        dos.writeUTF(msg);
        dos.flush();
    }

    public static void sendFile(File file, DataOutputStream dos) throws IOException {
        dos.writeInt(TYPE_FILE);
        dos.writeUTF(file.getName());
        dos.writeLong(file.length());

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, read);
            }
        }
        dos.flush();
    }

    public static String receiveAndSaveFile(DataInputStream dis, String dirName) throws IOException {
        String fileName = dis.readUTF();
        long size = dis.readLong();

        File dir = new File(dirName);
        if (!dir.exists()) dir.mkdir();
        File targetFile = new File(dir, "received_" + fileName);

        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long totalRead = 0;
            int read;
            while (totalRead < size && (read = dis.read(buffer, 0, (int) Math.min(buffer.length, size - totalRead))) != -1) {
                fos.write(buffer, 0, read);
                totalRead += read;
            }
        }
        return targetFile.getAbsolutePath();
    }

    public static byte[] readFileToByteArray(DataInputStream dis, long size) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        long totalRead = 0;
        int read;
        while (totalRead < size && (read = dis.read(buffer, 0, (int) Math.min(buffer.length, size - totalRead))) != -1) {
            baos.write(buffer, 0, read);
            totalRead += read;
        }
        return baos.toByteArray();
    }
}