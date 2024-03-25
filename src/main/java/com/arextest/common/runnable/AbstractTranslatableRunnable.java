package com.arextest.common.runnable;

import com.alibaba.ttl.TtlRunnable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTranslatableRunnable implements Runnable {


  @Override
  public final void run() {
    try {
      TtlRunnable.get(this::doWithContextRunning).run();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  protected abstract void doWithContextRunning();

}
