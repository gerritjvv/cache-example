package simple.impl;

import simple.Cache;

import java.io.Closeable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple in memory expiry cache, backed by a {@link ConcurrentHashMap}.
 * <p/>
 * Usage:
 * <pre>
 *     ExpireCache cache = new ExpireCache();
 *     cache.put("a", 1, 10, TimeUnit.SECONDS);
 *     ...
 *     cache.close();
 * </pre>
 * <p/>
 * By default every instance of the ExpireCache registers with the default single threaded {@link CacheExpireThread#DEFAULT}.
 * This thread will run a serial cleanup to check and evict any expired keys.
 * <p/>
 * Always call close
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class ExpireCache<K, V> implements Cache<K, V>, Expireable, Closeable {

    // used to generate unique ids for each ExpireCache
    private static final AtomicInteger CACHE_IDS = new AtomicInteger();

    private final ConcurrentHashMap<K, ValueWrapper<V>> store = new ConcurrentHashMap<>();

    protected final int id;
    private final Expirer expirer;

    public ExpireCache() {
        this(CACHE_IDS.getAndIncrement(), CacheExpireThread.DEFAULT);
    }

    public ExpireCache(int id, Expirer expirer) {
        this.id = id;
        this.expirer = expirer;
        expirer.put(id, this);
    }

    public V delete(K k) {
        ValueWrapper<V> v = store.remove(k);
        return v == null ? null : v.v;
    }

    /**
     * @param k      The key to store
     * @param v      The value, cannot be null to put here.
     * @param expire the expire time at which the value will be garbage collected i.e removed from the cache.
     * @param unit   the unit of time for expire
     */
    public void put(K k, V v, long expire, TimeUnit unit) {

        if (k == null) {
            throw new NullPointerException("Keys cannot be null");
        }

        if (v == null) {
            throw new NullPointerException("Values cannot be null");
        }

        store.put(k, new ValueWrapper<>(expire, unit == null ? TimeUnit.MILLISECONDS : unit, v));
    }

    public void put(K k, V v) {
        put(k, v, Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    /**
     * Same as calling {@link #get(Object, Object)} with defVal == null
     *
     * @param key
     * @return The value or null if not found
     */
    public V get(K key) {
        return get(key, null);
    }

    /**
     * @param key    The key to lookup
     * @param defVal the value to return if V is not found
     * @return V or defVal. We could have used Optional but didn't want to add the extra overhead of object creation
     * at this level. APIs higher up should use Optional though.
     */
    public V get(K key, V defVal) {
        ValueWrapper<V> val = store.get(key);
        return val == null ? defVal : val.v;
    }

    @Override
    public void checkExpire() {
        // check each value for expiration
        final long currentTimeMs = System.currentTimeMillis();
        store.values().removeIf(v -> v.expired(currentTimeMs));
    }

    /**
     * Cleanup any resources used and removes the cache from the Expirer its assigned to.
     */
    @Override
    public void close() {
        expirer.remove(id);
    }

    /**
     * Store a value with its expiry data.
     *
     * @param <V>
     */
    private static class ValueWrapper<V> {

        /**
         * The milliseconds + currentTimeInMills at which the value was stored.
         */
        final long expire;
        final V v;

        public ValueWrapper(long expire, TimeUnit unit, V v) {
            this.expire = unit.toMillis(expire) + System.currentTimeMillis();
            this.v = v;
        }

        /**
         * Returns true of expire > currentTimeMs
         *
         * @param currentTimeMs the current time to check expired against.
         * @return true if the value has expired.
         */
        public boolean expired(long currentTimeMs) {
            return expire > currentTimeMs;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValueWrapper<?> that = (ValueWrapper<?>) o;
            return v.equals(that.v);
        }

        @Override
        public int hashCode() {
            return Objects.hash(v);
        }
    }
}
