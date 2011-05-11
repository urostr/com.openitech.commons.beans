/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.value.events;

import java.sql.SQLException;

/**
 *
 * Se klièe iz sqlUtilities pred storeEvent()<b/>
 * Return value je nepomemben
 *
 * @author domenbasic
 */
public interface UpdateEventFields {

  public Event updateEventFields(Event newValues, Event oldValues) throws SQLException;

}
