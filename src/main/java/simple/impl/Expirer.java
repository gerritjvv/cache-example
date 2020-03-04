package simple.impl;

/**
 * An expirer will call Expireable(s) at set intervals to check for expired keys.
 */
public interface Expirer {

    /**
     * Add an Expirable with id.
     * @param id the id of the expirable.
     * @param expireable the expirable itself.
     */
    void put(int id, Expireable expireable);

    /**
     * Removes an expireable.
     * @param id the id of the expireable.
     */
    void remove(int id);
}
