package simple.impl;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ExpireCacheTest {

    @Test
    public void testPutGetDelete() {

        ExpireCache<String, Integer> cache = new ExpireCache<>();

        assertEquals(CacheExpireThread.DEFAULT.expireListeners.get(cache.id), cache);

        cache.put("a", 1);
        cache.put("b", 2);

        cache.delete("a");
        assertEquals(cache.get("a"), null);
        assertEquals(cache.get("a", 100), Integer.valueOf(100));

        assertEquals(cache.get("b"), Integer.valueOf(2));


        cache.close();
        assertEquals(CacheExpireThread.DEFAULT.expireListeners.get(cache.id), null);

    }

    @Test
    public void testExpire() throws InterruptedException {

        CacheExpireThread expirer = new CacheExpireThread(1, 100, TimeUnit.MILLISECONDS);

        ExpireCache<String, Integer> cache = new ExpireCache<>(
                100,
                // we increase expire check from 1second to 100ms for tests
                expirer
        );
        assertEquals(expirer.expireListeners.get(cache.id), cache);

        // never expire
        cache.put("a", 1);

        cache.put("b", 2, 500, TimeUnit.MILLISECONDS);
        assertEquals(cache.get("b"), Integer.valueOf(2));

        //hacky but so is testing any delay time based logic
        //check expiry
        int i = 0;
        while (cache.get("b") != null && i++ < 12) {
            Thread.sleep(100);
        }
        assertEquals(null, cache.get("b"));


        cache.close();
        assertEquals(CacheExpireThread.DEFAULT.expireListeners.get(cache.id), null);

    }

}
