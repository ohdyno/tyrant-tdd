import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class TyrantMap {

    private static final int OPERATION_PREFIX = 0xC8;
    private static final int PUT_OPERATION = 0x10;
    private static final int GET_OPERATION = 0x30;
    private static final int VANISH_OPERATION = 0x72;
    private static final int REMOVE_OPERATION = 0x20;
    private static final int SIZE_OPERATION = 0x80;
    private Socket socket;
    private DataOutputStream writer;
    private DataInputStream reader;

    void put(byte[] key, byte[] value) throws IOException {
        writer.write(OPERATION_PREFIX);
        writer.write(PUT_OPERATION);
        writer.writeInt(key.length);
        writer.writeInt(value.length);
        writer.write(key);
        writer.write(value);
        int status = reader.read();
        if (status != 0)
            throw new RuntimeException();
    }

    byte[] get(byte[] key) throws IOException {
        writer.write(OPERATION_PREFIX);
        writer.write(GET_OPERATION);
        writer.writeInt(key.length);
        writer.write(key);
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

    void clear() throws IOException {
        writer.write(OPERATION_PREFIX);
        writer.write(VANISH_OPERATION);
        int status = reader.read();
        if (status != 0)
            throw new RuntimeException();
    }

    void remove(byte[] key) throws IOException {
        writer.write(OPERATION_PREFIX);
        writer.write(REMOVE_OPERATION);
        writer.writeInt(key.length);
        writer.write(key);
        int status = reader.read();
        if (status == 1)
            return;
        if (status != 0)
            throw new RuntimeException();
    }

    long size() throws IOException {
        writer.write(OPERATION_PREFIX);
        writer.write(SIZE_OPERATION);
        int status = reader.read();
        if (status != 0)
            throw new RuntimeException();
        return reader.readLong();
    }

    void open() throws IOException {
        socket = new Socket("localhost", 1978);
        writer = new DataOutputStream(socket.getOutputStream());
        reader = new DataInputStream(socket.getInputStream());
    }

    void close() throws IOException {
        socket.close();
    }
}
