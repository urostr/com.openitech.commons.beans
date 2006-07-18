/*
 * ActiveRowChangeListener.java
 *
 * Created on April 2, 2006, 12:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.events;

/**
 *
 * @author uros
 */
public interface ActiveRowChangeListener {
    void activeRowChanged(ActiveRowChangeEvent event);
    void fieldValueChanged(ActiveRowChangeEvent event);
}
