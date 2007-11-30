/*
 * ListDataConcurrentListener.java
 *
 * Created on Torek, 18 julij 2006, 7:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.ref.events;

import com.openitech.db.model.concurrent.ConcurrentEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author uros
 */
public class ListDataConcurrentListener extends ListDataWeakListener  implements ConcurrentEvent {
  
  /** Creates a new instance of ListDataConcurrentListener */
  public ListDataConcurrentListener(ListDataListener owner) {
    super(owner);
  }
  
  public ListDataConcurrentListener(Object owner, String intervalAddedMethod, String intervalRemovedMethod, String contentsChangedMethod) throws
      NoSuchMethodException {
    super(owner, intervalAddedMethod, intervalRemovedMethod, contentsChangedMethod);
  }
}
