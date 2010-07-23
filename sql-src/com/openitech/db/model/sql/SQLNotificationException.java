/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db.model.sql;

import java.sql.SQLException;

/**
 *
 * @author uros
 */
public class SQLNotificationException extends SQLException {

  public SQLNotificationException(String reason, String sqlState, int vendorCode, Throwable cause) {
    super(reason, sqlState, vendorCode, cause);
  }

  public SQLNotificationException(String reason, String sqlState, Throwable cause) {
    super(reason, sqlState, cause);
  }

  public SQLNotificationException(String reason, Throwable cause) {
    super(reason, cause);
  }

  public SQLNotificationException(Throwable cause) {
    super(cause);
  }

  public SQLNotificationException() {
  }

  public SQLNotificationException(String reason) {
    super(reason);
  }

  public SQLNotificationException(String reason, String SQLState) {
    super(reason, SQLState);
  }

  public SQLNotificationException(String reason, String SQLState, int vendorCode) {
    super(reason, SQLState, vendorCode);
  }

}
