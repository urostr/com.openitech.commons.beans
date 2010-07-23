/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.value.events;

import java.sql.SQLException;

/**
 *
 * @author uros
 */
public interface UpdateEvent {

  Long updateEvent(Event newValues, Event oldValues) throws SQLException;
  
}
