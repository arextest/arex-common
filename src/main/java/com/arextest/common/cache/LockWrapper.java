package com.arextest.common.cache;

import java.util.concurrent.TimeUnit;

/**
 * @author wildeslam.
 * @create 2023/11/8 15:51
 */
public interface LockWrapper {

  /**
   * Lock will be released automatically after defined leaseTime interval.
   *
   * @param waitTime the maximum time to acquire the lock
   * @param leaseTime lease time
   * @param unit time unit
   * @return <code>true</code> if lock is successfully acquired,
   *          otherwise <code>false</code> if lock is already set.
   */
  boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

  /**
   * Lock will be released automatically after defined leaseTime interval.
   *
   * @param leaseTime the maximum time to hold the lock after it's acquisition
   * @param unit the time unit
   */
  void lock(long leaseTime, TimeUnit unit);

  /**
   * Releases the lock.
   */
  void unlock();

}
