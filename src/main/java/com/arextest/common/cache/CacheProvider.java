package com.arextest.common.cache;

/**
 * @author jmo
 * @since 2022/2/16
 */
public interface CacheProvider {
    /**
     * put the value with expired seconds
     * @param key the key for value
     * @param expiredSeconds  the expired seconds
     * @param value bytes of the object
     * @return true if success,others false
     */
    boolean put(byte[] key, long expiredSeconds, byte[] value);

    /**
     * @param key
     * @param expiredSeconds
     * @param value
     * @return
     */
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

    /**
     * @param key
     * @param seconds
     * @return
     */
    boolean expire(byte[] key, long seconds);

}
