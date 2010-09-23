/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.sql;

import com.openitech.value.events.EventPK;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author domenbasic
 */
public class SQLPrimaryKeyException extends SQLException {

  private String reason = "";
  private EventPK eventPK;

  public SQLPrimaryKeyException(String reason, Throwable cause, EventPK eventPK) {
    super(reason, cause);
    this.reason = reason;
    this.eventPK = eventPK;
    showDialog();
  }

  private void showDialog() {
    JOptionPane.showMessageDialog(null, reason  + "\n\n" + eventPK.getDebugString(), "Napaka!", JOptionPane.ERROR_MESSAGE, null);
  }
}
