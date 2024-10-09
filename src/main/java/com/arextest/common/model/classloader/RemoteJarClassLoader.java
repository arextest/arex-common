package com.arextest.common.model.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class RemoteJarClassLoader extends URLClassLoader {

  public RemoteJarClassLoader(URL[] urls, ClassLoader parent) {
    super(urls, parent);
  }
}
