import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TyrantTest {

    private TyrantMap tyrant;

    @Test
    public void getReturnsNullIfKeyNotFound() throws Exception {
        byte[] key = {'k', 'e', 'y'};
        assertNull(tyrant.get(key));
    }

    @Test
    public void getRetrievesWhatWasPut() throws IOException {
        byte[] key = {'k', 'e', 'y'};
        byte[] value = {'v', 'a', 'l', 'u', 'e'};
        tyrant.put(key, value);
        assertArrayEquals(value, tyrant.get(key));
    }

    @Test
    public void clearDeletesAllValues() throws Exception {
        byte[] key = {'k', 'e', 'y'};
        byte[] value = {'v', 'a', 'l', 'u', 'e'};
        tyrant.put(key, value);
        tyrant.clear();
        assertNull(tyrant.get(key));
    }

    @Test
    public void removeRemovesKey() throws Exception {
        byte[] key = {'k', 'e', 'y'};
        byte[] value = {'v', 'a', 'l', 'u', 'e'};
        tyrant.put(key, value);
        tyrant.remove(key);
        assertNull(tyrant.get(key));
    }

    @Test
    public void removeMissingKeyDoesNothing() throws Exception {
        byte[] key = {'k', 'e', 'y'};
        tyrant.remove(key);
    }

    @Test
    public void emptyMapSizeIsZero() throws Exception {
        assertEquals(0, tyrant.size());
    }

    @Test
    public void oneElementMapSizeIsOne() throws Exception {
        byte[] key = {'k', 'e', 'y'};
        byte[] value = {'v', 'a', 'l', 'u', 'e'};
        tyrant.put(key, value);
        assertEquals(1, tyrant.size());
    }

    @Before
    public void connect() throws IOException {
        tyrant = new TyrantMap();
        tyrant.open();
    }

    @After
    public void disconnect() throws IOException {
        tyrant.clear();
        tyrant.close();
    }
}
