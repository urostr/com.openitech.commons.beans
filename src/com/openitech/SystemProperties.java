/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;

/**
 *
 * @author uros
 */
public class SystemProperties {

  private SystemProperties() {
  }

  private static void setOuputLog(String applicationName) {
    try {
      String filename = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "Output Log " + applicationName + ".txt";
      java.io.File oFile = new java.io.File(filename);
      if (oFile.exists()) {
        oFile.delete();
      }
      oFile.createNewFile();
      PrintStream outputFile = new PrintStream(new FileOutputStream(oFile, false), true, "cp1250");
      System.setErr(outputFile);
      System.setOut(outputFile);
    } catch (IOException ex) {
      Logger.getLogger(SystemProperties.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static boolean isLinux() {
    String osName = System.getProperty("os.name");
    return osName.startsWith("Linux");
  }

  public static boolean isMacOSX() {
    String osName = System.getProperty("os.name");
    return osName.startsWith("Mac OS X");
  }

  public static boolean isMacOSXLeopardOrBetter() {
    String osName = System.getProperty("os.name");
    if (!osName.startsWith("Mac OS X")) {
      return false;
    }

    // split the "10.x.y" version number
    String osVersion = System.getProperty("os.version");
    String[] fragments = osVersion.split("\\.");

    // sanity check the "10." part of the version
    if (!fragments[0].equals("10")) {
      return false;
    }
    if (fragments.length < 2) {
      return false;
    }

    // check if Mac OS X 10.5(.y)
    try {
      int minorVers = Integer.parseInt(fragments[1]);
      if (minorVers >= 5) {
        return true;
      }
    } catch (NumberFormatException e) {
      // was not an integer
    }

    return false;
  }

  public static boolean isMacOSXSnowLeopardOrBetter() {
    String osName = System.getProperty("os.name");
    if (!osName.startsWith("Mac OS X")) {
      return false;
    }

    // split the "10.x.y" version number
    String osVersion = System.getProperty("os.version");
    String[] fragments = osVersion.split("\\.");

    // sanity check the "10." part of the version
    if (!fragments[0].equals("10")) {
      return false;
    }
    if (fragments.length < 2) {
      return false;
    }

    // check if Mac OS X 10.6(.y)
    try {
      int minorVers = Integer.parseInt(fragments[1]);
      if (minorVers >= 6) {
        return true;
      }
    } catch (NumberFormatException e) {
      // was not an integer
    }

    return false;
  }

  public static void configure(String applicationName) {
    if (!System.getProperties().containsKey("netbeans.debug")) {
      if (!isMacOSX()) {
        setOuputLog(applicationName);
      }
    } else {
      com.openitech.db.model.DbDataSource.DUMP_SQL = true;
    }


    if (isMacOSX()) {
      try {
        System.setProperty("com.apple.mrj.application.live-resize", "true");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        //        System.setProperty("acroread.bin","/Applications/Preview.app/Contents/MacOS/Preview");
        if (!System.getProperties().containsKey("swing.defaultlaf")) {
          Class<?> quaquaManagerClass = Class.forName("ch.randelshofer.quaqua.QuaquaManager");
          String version = (String) quaquaManagerClass.getDeclaredMethod("getVersion", (Class<?>[]) null).invoke(null, (Object[]) null);
          if (version.startsWith("5.2.1")) {
            System.setProperty("swing.defaultlaf", "ch.randelshofer.quaqua.leopard.Quaqua15LeopardCrossPlatformLookAndFeel");
            if (isMacOSXLeopardOrBetter()) {
              String libraryName = "lib" + (System.getProperty("os.arch").equals("x86_64") ? "quaqua_521_64" : "quaqua_521");

              if (loadLibrary(libraryName, "jnilib")) {
                System.setProperty("Quaqua.jniIsPreloaded", Boolean.toString(true));
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Quaqua JNI:{0} is preloaded.", libraryName);
              }
            }
          } else {
            System.setProperty("swing.defaultlaf", (String) quaquaManagerClass.getDeclaredMethod("getLookAndFeelClassName", (Class<?>[]) null).invoke(null, (Object[]) null));
            if (isMacOSXLeopardOrBetter()) {
              String libraryName = "lib" + (System.getProperty("os.arch").equals("x86_64") ? "quaqua64" : "quaqua");

              if (loadLibrary(libraryName, "jnilib")) {
                System.setProperty("Quaqua.jniIsPreloaded", Boolean.toString(true));
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Quaqua JNI:{0} is preloaded.", libraryName);
              }
            }
          }
        }
      } catch (Exception ex) {
        //ignore it
      }
    }
    if (!System.getProperties().containsKey("swing.defaultlaf") && !isLinux()) {
      try {
        Class.forName("ch.randelshofer.quaqua.leopard.Quaqua15LeopardCrossPlatformLookAndFeel");
        System.setProperty("swing.defaultlaf", "ch.randelshofer.quaqua.leopard.Quaqua15LeopardCrossPlatformLookAndFeel");
      } catch (ClassNotFoundException ex) {
        //ignore it
      }
    }
    try {
      UIManager.setLookAndFeel(System.getProperty("swing.defaultlaf", UIManager.getSystemLookAndFeelClassName()));
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Using ''{0}'' l&f on {1}.", new Object[]{UIManager.getLookAndFeel().getName(), System.getProperty("os.name")});
    } catch (Exception e) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Invalid system look&feel");
    }

    if (System.getProperty("view.pdfs", "").length() == 0) {
      System.setProperty("view.pdfs", "true");
    }
  }

  public static boolean loadLibrary(String libraryName, String suffix) {
    boolean loaded = false;
    try {
      System.loadLibrary(libraryName + '.' + suffix);
      loaded = true;
    } catch (UnsatisfiedLinkError e) {
    }
    if (!loaded) {
      try {
        loadFromJar(libraryName, suffix);
        loaded = true;
      } catch (UnsatisfiedLinkError e) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "SYSTEM:LOAD_LIBRARY:" + libraryName, e);
      } catch (Exception e) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "SYSTEM:LOAD_LIBRARY:" + libraryName, e);
      }
    }
    return loaded;
  }

  /**
   * When packaged into JAR extracts DLLs, places these into
   */
  private static void loadFromJar(String libraryName, String suffix) throws IOException {
    // have to use a stream
    InputStream in = SystemProperties.class.getResourceAsStream(libraryName + "." + suffix);
    // always write to different location

    File fileOut = File.createTempFile(libraryName + '_' + System.currentTimeMillis(), suffix);
    OutputStream out = new FileOutputStream(fileOut);

    copy(in, out);
    in.close();
    out.close();
    System.load(fileOut.toString());

  }
  /**
   * Copy the content of the input stream into the output stream, using a temporary
   * byte array buffer whose size is defined by {@link #IO_BUFFER_SIZE}.
   *
   * @param in The input stream to copy from.
   * @param out The output stream to copy to.
   *
   * @throws IOException If any error occurs during the copy.
   */
  private static final int IO_BUFFER_SIZE = 4 * 1024;

  private static void copy(InputStream in, OutputStream out) throws IOException {
    byte[] b = new byte[IO_BUFFER_SIZE];
    int read;
    while ((read = in.read(b)) != -1) {
      out.write(b, 0, read);
    }
  }

  public static class HttpProxyConfig {

    private static boolean useSystemProxies;
    private static String host;
    private static int port;
    private static boolean init = false;
    private static final Object lock = new Object();

    public static void init() {
      synchronized (lock) {
        if (!init) {
          useSystemProxies = Boolean.valueOf(System.getProperty("java.net.useSystemProxies", "false"));
          
          host = System.getProperty("http.proxyHost", null);
          
          if (host == null) {
            System.setProperty("java.net.useSystemProxies", "true");
            Proxy proxy = getSystemProxy();
            if (proxy != null) {
              InetSocketAddress addr = (InetSocketAddress) proxy.address();

              if (addr != null) {
                host = addr.getHostName();
                port = addr.getPort();

//          System.setProperty("java.net.useSystemProxies", "false");
                System.setProperty("http.proxyHost", host);
                System.setProperty("http.proxyPort", "" + port);
              }
            }
            System.setProperty("java.net.useSystemProxies", Boolean.toString(useSystemProxies));
          } else {
            port = Integer.valueOf(System.getProperty("http.proxyPort", "80"));
          }
          
          init = true;
        }
      }
    }

    public static boolean isUseHttpProxy() {
      init();
      return host != null;
    }
    
    public static Proxy getProxy() {
      init();
      return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }

    private static Proxy getSystemProxy() {
      List<Proxy> l = null;
      try {
        ProxySelector def = ProxySelector.getDefault();

        l = def.select(new URI("http://www.google.si"));
//        ProxySelector.setDefault(null);
      } catch (Exception e) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, e.getMessage(), e);
      }
      if (l != null) {
        for (Iterator<Proxy> iter = l.iterator(); iter.hasNext();) {
          java.net.Proxy proxy = iter.next();
          return proxy;
        }
      }
      return null;
    }
  }
}
