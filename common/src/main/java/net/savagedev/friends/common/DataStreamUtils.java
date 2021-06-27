package net.savagedev.friends.common;

import java.io.*;

public class DataStreamUtils {
    public static byte[] toByteArray(String... messages) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutput output = new DataOutputStream(outputStream);

        for (String message : messages) {
            try {
                output.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return outputStream.toByteArray();
    }

    public static DataInputStream newDataInput(byte[] message) {
        return new DataInputStream(new ByteArrayInputStream(message));
    }
}