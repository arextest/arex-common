package com.arextest.common.desensitization;

import com.arextest.common.model.classloader.RemoteJarClassLoader;
import com.arextest.common.utils.RemoteJarLoaderUtils;
import com.arextest.extension.desensitization.DataDesensitization;
import com.arextest.extension.desensitization.DefaultDataDesensitization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class DesensitizationProvider {

  private volatile DataDesensitization desensitizationService;

  private final String jarUrl;

  public DataDesensitization get() {
    if (desensitizationService == null) {
      synchronized (DesensitizationProvider.class) {
        if (desensitizationService == null) {
          try {
            desensitizationService = loadDesensitization(jarUrl);
            LOGGER.info("load desensitization success, className:{}",
                desensitizationService.getClass().getName());
          } catch (Exception runtimeException) {
            LOGGER.error("load desensitization error", runtimeException);
            throw new RuntimeException(runtimeException.getMessage());
          }
        }
      }
    }
    return desensitizationService;
  }

  protected DataDesensitization loadDesensitization(String remoteJarUrl)
      throws Exception {
    DataDesensitization dataDesensitization = new DefaultDataDesensitization();
    if (StringUtils.isEmpty(remoteJarUrl)) {
      return dataDesensitization;
    }
    RemoteJarClassLoader remoteJarClassLoader = RemoteJarLoaderUtils.loadJar(remoteJarUrl);
    dataDesensitization = RemoteJarLoaderUtils
        .loadService(DataDesensitization.class, remoteJarClassLoader)
        .get(0);
    return dataDesensitization;
  }

}