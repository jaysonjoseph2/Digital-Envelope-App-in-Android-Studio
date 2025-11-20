package com.example.digital_envelope;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class EnvelopeFile {

    public byte[] wrappedKey;
    public byte[] iv;
    public byte[] encryptedData;

    public EnvelopeFile(byte[] wrappedKey, byte[] iv, byte[] encryptedData) {
        this.wrappedKey = wrappedKey;
        this.iv = iv;
        this.encryptedData = encryptedData;
    }

    public byte[] toBytes() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // wrapped key length (4 bytes)
        baos.write(ByteBuffer.allocate(4).putInt(wrappedKey.length).array());
        // iv length (4 bytes)
        baos.write(ByteBuffer.allocate(4).putInt(iv.length).array());
        // encrypted data length (4 bytes)
        baos.write(ByteBuffer.allocate(4).putInt(encryptedData.length).array());

        baos.write(wrappedKey);
        baos.write(iv);
        baos.write(encryptedData);

        return baos.toByteArray();
    }


    public static EnvelopeFile fromBytes(byte[] blob) {
        ByteBuffer buffer = ByteBuffer.wrap(blob);

        int keyLen = buffer.getInt();
        int ivLen = buffer.getInt();
        int dataLen = buffer.getInt();

        byte[] wrappedKey = new byte[keyLen];
        buffer.get(wrappedKey);

        byte[] iv = new byte[ivLen];
        buffer.get(iv);

        byte[] encryptedData = new byte[dataLen];
        buffer.get(encryptedData);

        return new EnvelopeFile(wrappedKey, iv, encryptedData);
    }
}
