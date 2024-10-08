package com.arextest.common.utils;

import com.arextest.common.model.classloader.RemoteJarClassLoader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class RemoteJarLoaderUtils {

  private static final String RESOURCE_PREFIX = "classpath:";
  private static final String BOOT_INF = "BOOT-INF";

  public static RemoteJarClassLoader loadJar(String jarUrl) throws MalformedURLException {

    URL resource = null;
    if (jarUrl.startsWith("http")) {
      LOGGER.info("do http load jar: {}", jarUrl);
      resource = new URL(jarUrl);
      if (existJarExist(resource)) {
        return createRemoteJarClassLoader(resource);
      }
    }

    if (jarUrl.startsWith(RESOURCE_PREFIX)) {
      jarUrl = jarUrl.substring(RESOURCE_PREFIX.length());
      LOGGER.info("do classpath load jar: {}", jarUrl);
      resource = Thread.currentThread().getContextClassLoader().getResource(jarUrl);
      if (existJarExist(resource)) {
        return createRemoteJarClassLoader(resource);
      }
    }

    if (resource == null) {
      LOGGER.info("do file load jar: {}", jarUrl);
      resource = new File(jarUrl).toURI().toURL();
      if (existJarExist(resource)) {
        return createRemoteJarClassLoader(resource);
      }
    }
    throw new RuntimeException("Failed to load jar: " + jarUrl);
  }

  public static <T> List<T> loadService(Class<T> clazz,
      RemoteJarClassLoader classLoader) {
    ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz, classLoader);
    List<T> res = new ArrayList<>();
    Iterator<T> iterator = serviceLoader.iterator();
    while (iterator.hasNext()) {
      res.add(iterator.next());
    }
    return res;
  }

  private static boolean existJarExist(URL resource) {
    if (resource == null) {
      return false;
    }

    try (InputStream inputStream = resource.openConnection().getInputStream()) {
      return true;
    } catch (IOException e) {
      LOGGER.error("Failed to load resource: {}", resource, e);
      return false;
    }
  }

  private static RemoteJarClassLoader createRemoteJarClassLoader(URL url) {
    URL resourceUrl = crateTempFile(url);
    return new RemoteJarClassLoader(new URL[]{resourceUrl},
        RemoteJarLoaderUtils.class.getClassLoader());
  }

  private static URL crateTempFile(URL resource) {
    String resourcePath = Optional.ofNullable(resource).map(URL::getPath).orElse(null);
    if (StringUtils.isEmpty(resourcePath) || !resourcePath.contains(BOOT_INF)) {
      return resource;
    }
    LOGGER.info("create temp file for jar: {}", resourcePath);

    try {
      URLConnection urlConnection = resource.openConnection();
      try (InputStream inputStream = urlConnection.getInputStream()) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
          baos.write(buffer, 0, length);
        }

        byte[] data = baos.toByteArray();

        String fileName = UUID.randomUUID().toString();
        String path = resource.getPath();
        if (StringUtils.isNotEmpty(path)) {
          String lowerCasePath = path.toLowerCase();
          fileName = lowerCasePath.replaceAll("[^a-z]", "_");
        }

        Path tempDir = Paths.get(System.getProperty("user.dir"));
        Path fixedNameFile = tempDir.resolve(fileName + ".tmp");
        if (Files.exists(fixedNameFile)) {
          return fixedNameFile.toUri().toURL();
        }

        Files.write(fixedNameFile, data, StandardOpenOption.CREATE_NEW);
        File file = fixedNameFile.toFile();
        file.deleteOnExit();
        return file.toURL();
      } catch (IOException e) {
        LOGGER.error("Failed to load resource: {}", resource, e);
        return null;
      }
    } catch (IOException e) {
      LOGGER.error("Failed to open connection: {}", resource, e);
      return null;
    }
  }
}