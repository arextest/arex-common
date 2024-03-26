package com.arextest.common.runnable;

import com.arextest.common.utils.GroupContextUtil;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

@Slf4j
public abstract class AbstractContextWithTraceRunnable implements Runnable {

  private final Map<String, String> traceMap;
  private final String group;

  public AbstractContextWithTraceRunnable() {
    this.traceMap = MDC.getCopyOfContextMap();
    this.group = GroupContextUtil.getGroup();
  }


  @Override
  public final void run() {
    Map<String, String> old = mark();
    String oldGroup = markContext();
    try {
      doWithContextRunning();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    } finally {
      removeMark(old);
      removeContext(oldGroup);
    }
  }

  protected abstract void doWithContextRunning();

  private Map<String, String> mark() {
    if (MapUtils.isEmpty(this.traceMap)) {
      return null;
    } else {
      Map<String, String> old = MDC.getCopyOfContextMap();
      MDC.setContextMap(this.traceMap);
      return old;
    }
  }

  private void removeMark(Map<String, String> prev) {
    if (MapUtils.isEmpty(prev)) {
      MDC.clear();
    } else {
      MDC.setContextMap(prev);
    }
  }

  private String markContext() {
    if (StringUtils.isEmpty(this.group)) {
      return null;
    } else {
      String old = GroupContextUtil.getGroup();
      GroupContextUtil.setGroup(this.group);
      return old;
    }
  }

  private void removeContext(String prev) {
    if (StringUtils.isEmpty(prev)) {
      GroupContextUtil.clear();
    } else {
      GroupContextUtil.setGroup(prev);
    }
  }


}