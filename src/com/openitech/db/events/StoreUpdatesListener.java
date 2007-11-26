/*
 * ActiveRowChangeListener.java
 *
 * Created on April 2, 2006, 12:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.events;

import java.util.EventListener;

/**
 *
 * @author uros
 */
public interface StoreUpdatesListener extends EventListener {
    void storeUpdates(StoreUpdatesEvent event) throws java.sql.SQLException;
}
