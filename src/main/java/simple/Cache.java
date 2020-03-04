package simple;

import java.util.concurrent.TimeUnit;

/**
 * Simple expiring cache interface.
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface Cache<K, V> {

    /**
     * Sets a value to a key in the cache.
     *
     * @param k      the key.
     * @param v      the value.
     * @param expire the time that the value will expire.
     * @param unit   the unit millis, seconds ...
     */
    void put(K k, V v, long expire, TimeUnit unit);

    /**
     * Sets a value to a key that will never expire.
     *
     * @param k the key.
     * @param v the value.
     */
    void put(K k, V v);

    /**
     * Delete the value at key k.
     *
     * @param k the key to delete.
     * @return The value if any was set for the key k.
     */
    V delete(K k);

    /**
     * Get the value at key. If no value is found defVal is returned.
     *
     * @param key    the key search for.
     * @param defVal the value returns if no value found.
     * @return The value at key or defVal.
     */
    V get(K key, V defVal);

    /**
     * Get a value at key and if not found return null.
     *
     * @param key the key to search for.
     * @return null or the value at key.
     */
    V get(K key);

}