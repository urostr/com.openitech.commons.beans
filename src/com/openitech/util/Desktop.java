/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author uros
 */
public class Desktop {

  private static Method jdic_open;
  private static Method awt_open;
  private static Object awt_desktop_manager;

  static {
    try {
      try {
        Class desktop = Class.forName("org.jdesktop.jdic.desktop.Desktop");
        jdic_open = desktop.getMethod("open", java.io.File.class);
        System.out.println("Using jdic to open files.");
      } catch (java.lang.ClassNotFoundException ex) {
        Class desktop = Class.forName("java.awt.Desktop");
        if ((Boolean) (desktop.getMethod("isDesktopSupported", new Class[] {}).invoke(null, new Object[] {}))) {
          awt_desktop_manager = desktop.getMethod("getDesktop", new Class[] {}).invoke(null, new Object[] {});
          awt_open = desktop.getMethod("open", java.io.File.class);
          System.out.println("Using awt to open files.");
        }
      }
    }
    catch (Exception ex) {
      System.out.println("org.jdesktop.jdic.desktop.Desktop : "+ex.getMessage());
      jdic_open = null;
      awt_open  = null;
      awt_desktop_manager = null;
    }
  }

  private Desktop() {

  }

  public static boolean isFileOpenSupported() {
    return jdic_open!=null||awt_open!=null;
  }

  public static void open(java.io.File file) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    if (file.exists()) {
      if (jdic_open!=null) {
        jdic_open.invoke(null, file);
      } else {
        awt_open.invoke(awt_desktop_manager, file);
      }
    }
  }
  
}
