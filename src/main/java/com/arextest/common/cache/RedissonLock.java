package com.arextest.common.cache;

import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;

/**
 * @author wildeslam.
 * @create 2023/11/8 15:58
 */
public class RedissonLock implements LockWrapper  {

  private final RLock rLock;

  public RedissonLock(RLock rLock) {
    this.rLock = rLock;
  }

  @Override
  public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
    return rLock.tryLock(waitTime, leaseTime, unit);
  }

  @Override
  public void lock(long leaseTime, TimeUnit unit) {
    rLock.lock(leaseTime, unit);
  }

  @Override
  public void unlock() {
    rLock.unlock();
  }

}
