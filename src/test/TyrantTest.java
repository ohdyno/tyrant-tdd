import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertArrayEquals;

public class TyrantTest {

    private TyrantMap tyrant;

    @Test
    public void getRetrievesWhatWasPut() throws IOException {
        byte[] key = {'k', 'e', 'y'};
        byte[] value = {'v', 'a', 'l', 'u', 'e'};
        tyrant.put(key, value);
        assertArrayEquals(value, tyrant.get(key));
        disconnected();
    }

    @Before
    public void connect() throws IOException {
        tyrant = new TyrantMap();
        tyrant.open();
    }

    @After
    public void disconnected() throws IOException {
        tyrant.close();
    }
}
