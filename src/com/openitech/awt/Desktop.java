/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.awt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        if ((Boolean) (desktop.getMethod("isDesktopSupported", new Class[]{}).invoke(null, new Object[]{}))) {
          awt_desktop_manager = desktop.getMethod("getDesktop", new Class[]{}).invoke(null, new Object[]{});
          awt_open = desktop.getMethod("open", java.io.File.class);
          System.out.println("Using awt to open files.");
        }
      }
    } catch (Exception ex) {
      System.out.println("org.jdesktop.jdic.desktop.Desktop : " + ex.getMessage());
      jdic_open = null;
      awt_open = null;
      awt_desktop_manager = null;
    }
  }

  private Desktop() {
  }

  public static boolean isFileOpenSupported() {
    return jdic_open != null || awt_open != null;
  }
  private static final ExecutorService executorService = Executors.newCachedThreadPool();

  public static void open(final java.io.File file) {
    System.out.println("Opening "+file.getAbsolutePath());
    if (file.exists()) {
      executorService.execute(new Runnable() {

        @Override
        public void run() {
          try {
            if (jdic_open != null) {
              jdic_open.invoke(null, file);
            } else {
              awt_open.invoke(awt_desktop_manager, file);
            }
          } catch (IllegalAccessException ex) {
            Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
          } catch (IllegalArgumentException ex) {
            Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
          } catch (InvocationTargetException ex) {
            Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      });
    } else {
      System.out.println("File does not exist.");
    }
  }
}