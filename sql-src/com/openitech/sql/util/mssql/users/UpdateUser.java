/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.util.mssql.users;

import com.openitech.value.events.AfterUpdateEvent;
import com.openitech.value.events.Event;
import com.openitech.value.events.UpdateEventFields;
import java.sql.SQLException;

/**
 * Uporablja se kot Field action
 *
 * @author domenbasic
 */
public class UpdateUser implements AfterUpdateEvent, UpdateEventFields {

  @Override
  public Event afterUpdateEvent(Event newValues, Event oldValues) throws SQLException {

    if (newValues.getOperation() == Event.EventOperation.DELETE) {
      UserManager userManager = UserManager.getInstance();
      String userName = (String) newValues.getValue("USER_USERNAME");

      userManager.deleteUser(userName);
    }
    return newValues;
  }

  @Override
  public Event updateEventFields(Event newValues, Event oldValues) throws SQLException {
    UserManager userManager = UserManager.getInstance();
    String userName = (String) newValues.getValue("USER_USERNAME");
    String password = (String) newValues.getValue("USER_PASSWORD");

    if (newValues.getOperation() == Event.EventOperation.UPDATE) {

      if (newValues.getId() == null) {
        userManager.addUser(userName, password);
      } else {
        userManager.editUser(userName, password);
      }
    }
    return newValues;
  }
}
