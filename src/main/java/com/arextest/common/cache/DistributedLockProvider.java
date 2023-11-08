package com.arextest.common.cache;

/**
 * @author wildeslam.
 * @create 2023/11/8 14:33
 */
public interface DistributedLockProvider<T> {
  T getLock(String namespaceId);
}
