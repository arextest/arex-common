package com.arextest.common.cache;

import java.util.concurrent.TimeUnit;

/**
 * @author wildeslam.
 * @create 2023/11/8 15:51
 */
public interface LockWrapper {
  void lock(long leaseTime, TimeUnit unit);

  void unlock();

}
