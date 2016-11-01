package me.ohdyno.xing.projects.tyrant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;

class TyrantMap implements Iterable<byte[]> {

    private static final int OPERATION_PREFIX = 0xC8;
    private static final int PUT_OPERATION = 0x10;
    private static final int GET_OPERATION = 0x30;
    private static final int VANISH_OPERATION = 0x72;
    private static final int REMOVE_OPERATION = 0x20;
    private static final int SIZE_OPERATION = 0x80;
    private static final int GET_NEXT_KEY_OPERATION = 0x51;
    private static final int RESET_ITERATOR_OPERATION = 0x50;
    private Socket socket;
    private DataOutputStream writer;
    private DataInputStream reader;

    void put(byte[] key, byte[] value) throws IOException {
        writeOperation(PUT_OPERATION);
        writer.writeInt(key.length);
        writer.writeInt(value.length);
        writer.write(key);
        writer.write(value);
        int status = reader.read();
        if (status != 0)
            throw new RuntimeException();
    }

    byte[] get(byte[] key) throws IOException {
        writeOperation(GET_OPERATION);
        writer.writeInt(key.length);
        writer.write(key);
        return readBytes();
    }

    void clear() throws IOException {
        writeOperation(VANISH_OPERATION);
        int status = reader.read();
        if (status != 0)
            throw new RuntimeException();
    }

    void remove(byte[] key) throws IOException {
        if (key == null)
            throw new IllegalArgumentException();

        writeOperation(REMOVE_OPERATION);
        writer.writeInt(key.length);
        writer.write(key);
        int status = reader.read();
        if (status == 1)
            return;
        if (status != 0)
            throw new RuntimeException();
    }

    long size() throws IOException {
        writeOperation(SIZE_OPERATION);
        int status = reader.read();
        if (status != 0)
            throw new RuntimeException();
        return reader.readLong();
    }

    @Override
    public Iterator<byte[]> iterator() {
        try {
            reset();
            byte[] firstKey = getNextKey();

            return new Iterator<byte[]>() {
                private byte[] previousKey;
                byte[] nextKey = firstKey;

                @Override
                public boolean hasNext() {
                    return nextKey != null;
                }

                @Override
                public byte[] next() {
                    try {
                        byte[] result = get(nextKey);
                        previousKey = nextKey;
                        nextKey = getNextKey();
                        return result;
                    } catch (IOException e) {
                        throw new RuntimeException();
                    }
                }

                @Override
                public void remove() {
                    try {
                        TyrantMap.this.remove(previousKey);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalStateException();
                    } catch (IOException e) {
                        throw new RuntimeException();
                    }
                }
            };
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    void open() throws IOException {
        socket = new Socket("localhost", 1978);
        writer = new DataOutputStream(socket.getOutputStream());
        reader = new DataInputStream(socket.getInputStream());
    }

    void close() throws IOException {
        socket.close();
    }

    private void writeOperation(int putOperation) throws IOException {
        writer.write(OPERATION_PREFIX);
        writer.write(putOperation);
    }

    private byte[] getNextKey() {
        try {
            writeOperation(GET_NEXT_KEY_OPERATION);
            return readBytes();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private byte[] readBytes() throws IOException {
        int status = reader.read();
        if (status == 1)
            return null;
        if (status != 0)
            throw new RuntimeException();
        int length = reader.readInt();
        byte[] results = new byte[length];
        reader.read(results);// TODO read longer values
        return results;
    }

    private void reset() throws IOException {
        writeOperation(RESET_ITERATOR_OPERATION);
        int status = reader.read();
        if (status != 0)
            throw new RuntimeException();
    }
}
