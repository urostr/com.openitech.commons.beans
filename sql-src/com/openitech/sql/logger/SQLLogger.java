/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class SQLLogger {

  private static boolean initialized = false;

  private SQLLogger() {
  }

//  private static Properties getLogConfig() throws Error, FileNotFoundException, IOException {
//    Properties logConfig = new Properties();
//    String fname = System.getProperty("java.util.logging.config.file");
//    if (fname == null) {
//      fname = System.getProperty("java.home");
//      if (fname == null) {
//        throw new Error("Can't find java.home ??");
//      }
//      File f = new File(fname, "lib");
//      f = new File(f, "logging.properties");
//      fname = f.getCanonicalPath();
//    }
//    InputStream in = new FileInputStream(fname);
//    BufferedInputStream bin = new BufferedInputStream(in);
//    try {
//      logConfig.load(bin);
//    } finally {
//      if (in != null) {
//        in.close();
//      }
//    }
//    return logConfig;
//  }
  public static void init() throws IOException {
    if (!initialized) {
      //    Properties logConfig = getLogConfig();
      //
      //    logConfig.setProperty("handlers", logConfig.getProperty("handler", "") + " com.openitech.sql.logger.SQLHandler");
      //
      //    File temp = File.createTempFile("cfg", "properties");
      //    PrintWriter pw = new PrintWriter(temp);
      //    logConfig.list(pw);
      //
      //    pw.flush();
      //    pw.close();
      //
      //    InputStream in = new FileInputStream(temp);
      //    BufferedInputStream bin = new BufferedInputStream(in);
      //
      //    try {
      //      LogManager.getLogManager().readConfiguration(bin);
      //    } finally {
      //      if (in != null) {
      //        in.close();
      //      }
      //    }
      Logger logger = LogManager.getLogManager().getLogger("");
      logger.addHandler(new SQLHandler());
      initialized = true;
    }
  }
}
