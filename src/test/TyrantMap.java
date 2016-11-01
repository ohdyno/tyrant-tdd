import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class TyrantMap {

    private static final int OPERATON_PREFIX = 0xC8;
    private static final int PUT_OPERATION = 0x10;
    private static final int GET_OPERATION = 0x30;
    private Socket socket;
    private DataOutputStream writer;
    private DataInputStream reader;

    void put(byte[] key, byte[] value) throws IOException {
        writer.write(OPERATON_PREFIX);
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
        writer.write(OPERATON_PREFIX);
        writer.write(GET_OPERATION);
        writer.writeInt(key.length);
        writer.write(key);
        int status = reader.read();
        if (status != 0)
            throw new RuntimeException();
        int length = reader.readInt();
        byte[] results = new byte[length];
        reader.read(results);// TODO read longer values
        return results;
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
