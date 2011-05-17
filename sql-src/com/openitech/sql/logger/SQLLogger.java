/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.logger;

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

  public static void init() {
    if (!initialized) {
      Logger logger = LogManager.getLogManager().getLogger("");
      logger.addHandler(new SQLHandler());
      initialized = true;
    }
  }
}
