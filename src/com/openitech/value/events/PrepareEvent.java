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
public interface PrepareEvent {

  public void register(PrepareEvent prepareEvent);

  public void unregister(PrepareEvent prepareEvent);

  public void prepareEvent(Event parentEvent) throws SQLException;

  public void reloadEventDataSources();
}
