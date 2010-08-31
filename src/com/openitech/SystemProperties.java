/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
  
  public static void configure(String applicationName) {
    if (!System.getProperties().containsKey("netbeans.debug")) {
      setOuputLog(applicationName);
    } else {
      com.openitech.db.model.DbDataSource.DUMP_SQL = true;
    }


    if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
      try {
        System.setProperty("com.apple.mrj.application.live-resize", "true");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
//        System.setProperty("acroread.bin","/Applications/Preview.app/Contents/MacOS/Preview");
        Class.forName("ch.randelshofer.quaqua.snow_leopard.Quaqua16SnowLeopardLookAndFeel");
        System.setProperty("swing.defaultlaf", "ch.randelshofer.quaqua.snow_leopard.Quaqua16SnowLeopardLookAndFeel");
      } catch (ClassNotFoundException ex) {
        //ignore it
      }
    }
    if (!System.getProperties().containsKey("swing.defaultlaf")) {
      try {
        Class.forName("ch.randelshofer.quaqua.leopard.Quaqua15LeopardCrossPlatformLookAndFeel");
        System.setProperty("swing.defaultlaf", "ch.randelshofer.quaqua.leopard.Quaqua15LeopardCrossPlatformLookAndFeel");
      } catch (ClassNotFoundException ex) {
        //ignore it
      }
    }
    try {
      UIManager.setLookAndFeel(System.getProperty("swing.defaultlaf", UIManager.getSystemLookAndFeelClassName()));
      Logger.getLogger(Settings.LOGGER).log(Level.INFO, "Using ''{0}'' l&f on {1}.", new Object[]{UIManager.getLookAndFeel().getName(), System.getProperty("os.name")});
    } catch (Exception e) {
      Logger.getLogger(Settings.LOGGER).info("Invalid system look&feel");
    }

    if (System.getProperty("view.pdfs", "").length() == 0) {
      System.setProperty("view.pdfs", "true");
    }
  }

}
