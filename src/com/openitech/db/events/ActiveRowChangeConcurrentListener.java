/*
 * ActiveRowChangeConcurrentListener.java
 *
 * Created on Ponedeljek, 17 julij 2006, 20:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.events;

import com.openitech.events.concurrent.ConcurrentEvent;

/**
 *
 * @author uros
 */
public class ActiveRowChangeConcurrentListener extends ActiveRowChangeWeakListener implements ConcurrentEvent {
  public ActiveRowChangeConcurrentListener(ActiveRowChangeListener owner) {
    super(owner);
  }
  public ActiveRowChangeConcurrentListener(Object owner, String fieldValueChangedMethod, String activeRowChangedMethod) throws
      NoSuchMethodException {
    super(owner,fieldValueChangedMethod,activeRowChangedMethod);
  }
}
