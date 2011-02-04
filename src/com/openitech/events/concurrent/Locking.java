/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.events.concurrent;

/**
 *
 * @author uros
 */
public interface Locking {

  public boolean lock();

  public boolean canLock();

  public boolean lock(boolean fatal);

  public boolean lock(boolean fatal, boolean force);

  public void unlock();
}
