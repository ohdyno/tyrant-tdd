import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class TyrantMapTest {

    private TyrantMap tyrant;
    private static final byte[] KEY = new byte[]{'k', 'e', 'y'};
    private static final byte[] VALUE = new byte[]{'v', 'a', 'l', 'u', 'e'};

    @Test
    public void getReturnsNullIfKeyNotFound() throws Exception {
        assertNull(tyrant.get(KEY));
    }

    @Test
    public void getRetrievesWhatWasPut() throws IOException {
        tyrant.put(KEY, VALUE);
        assertArrayEquals(VALUE, tyrant.get(KEY));
    }

    @Test
    public void clearDeletesAllValues() throws Exception {
        tyrant.put(KEY, VALUE);
        tyrant.clear();
        assertNull(tyrant.get(KEY));
    }

    @Test
    public void removeRemovesKey() throws Exception {
        tyrant.put(KEY, VALUE);
        tyrant.remove(KEY);
        assertNull(tyrant.get(KEY));
    }

    @Test
    public void removeMissingKeyDoesNothing() throws Exception {
        tyrant.remove(KEY);
    }

    @Test
    public void emptyMapSizeIsZero() throws Exception {
        assertEquals(0, tyrant.size());
    }

    @Test
    public void oneElementMapSizeIsOne() throws Exception {
        tyrant.put(KEY, VALUE);
        assertEquals(1, tyrant.size());
    }

    @Test
    public void iterateOverAnEmptyTyrant() throws Exception {
        for (byte[] ignored : tyrant)
            fail();
    }

    @Test
    public void iterateOverTwoElementTyrant() throws Exception {
        tyrant.put(KEY, VALUE);
        tyrant.put(new byte[]{'k','e','y','s'}, VALUE);
        int count = 0;
        for (byte[] each : tyrant) {
            assertArrayEquals(VALUE, each);
            count++;
        }
        assertEquals(2, count);
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
