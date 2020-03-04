package simple.impl;

/**
 * Expirer(s) store Expireable(s), and call their checkExpire methods so that they can check for expired entries.
 */
public interface Expireable {

    void checkExpire();

}
