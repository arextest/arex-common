package io.arex.common.cache;

/**
 * @author jmo
 * @since 2022/2/16
 */
public interface CacheProvider {
    boolean put(byte[] key, long expiredSeconds, byte[] value);

    boolean putIfAbsent(byte[] key, long expiredSeconds, byte[] value);

    /**
     * Get the value of a key
     *
     * @param key the bytes of key
     * @return null when key does not exist.
     */
    byte[] get(byte[] key);

    /**
     * Increment integer value of the key by on
     *
     * @param key the key
     * @return The value of the key after increment
     */
    long incrValue(byte[] key);

    /**
     * Decrement the integer value of a key by one.
     *
     * @param key the key
     * @return the value of key after decrement
     */
    long decrValue(byte[] key);

    /**
     * Delete value specified by the key
     *
     * @param key the bytes of key
     * @return True if key is removed.
     */
    boolean remove(byte[] key);

    boolean expire(byte[] key, long seconds);

}
