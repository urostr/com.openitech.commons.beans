/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.sql.events;

import com.openitech.sql.events.Event;
import java.sql.SQLException;

/**
 *
 * @author uros
 */
public interface UpdateEvent {

  Long updateEvent(Event event) throws SQLException;

}
