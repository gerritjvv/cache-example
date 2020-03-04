package simple.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CacheExpireThreadTest {

    @Test
    public void testExpireRemoveAdd() {

        CacheExpireThread th = CacheExpireThread.DEFAULT;

        final Expireable listener1 = () -> {};
        final Expireable listener2 = () -> {};

        th.put(1, listener1);
        th.put(2, listener2);

        assertEquals(th.expireListeners.get(1), listener1);
        assertEquals(th.expireListeners.get(2), listener2);


        th.remove(1);
        assertEquals(th.expireListeners.get(1), null);
        assertEquals(th.expireListeners.get(2), listener2);

    }
}
