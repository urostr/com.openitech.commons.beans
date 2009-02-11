/*
 * FormattedFilterPlainDocument.java
 *
 * Created on Torek, 8 januar 2008, 14:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.components.model;

import com.openitech.Settings;
import java.awt.EventQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.text.PlainDocument;

/**
 *
 * @author uros
 */
public class FormattedFilterPlainDocument extends PlainDocument {
  private static final ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();
  private static final long DELAY = 15;
  private Future event = null;
  
  
  /**
   * Creates a new instance of FormattedFilterPlainDocument
   */
  public FormattedFilterPlainDocument() {
  }
  
  /**
   * Constructs a plain text document.  A default root element is created,
   * and the tab size set to 8.
   *
   * @param c  the container for the content
   */
  public FormattedFilterPlainDocument(Content c) {
    
  }
  
  /**
   * Notifies all listeners that have registered interest for
   * notification on this event type.  The event instance
   * is lazily created using the parameters passed into
   * the fire method.
   *
   * @param e the event
   * @see EventListenerList
   */
  protected void fireInsertUpdate(DocumentEvent e) {
    if (!(event==null||event.isDone()||event.isCancelled())) {
      event.cancel(false);
    }
    
    event = schedule.schedule(new DocumentEventsRunnable(e, DocumentEvent.EventType.INSERT), DELAY, TimeUnit.MILLISECONDS);
  }
  
  private void inheritedInsertUpdate(DocumentEvent e) {
    super.fireInsertUpdate(e);
  }

  /**
   * Notifies all listeners that have registered interest for
   * notification on this event type.  The event instance
   * is lazily created using the parameters passed into
   * the fire method.
   *
   * @param e the event
   * @see EventListenerList
   */
  protected void fireChangedUpdate(DocumentEvent e) {
    if (!(event==null||event.isDone()||event.isCancelled())) {
      event.cancel(false);
    }
    
    event = schedule.schedule(new DocumentEventsRunnable(e, DocumentEvent.EventType.CHANGE), DELAY, TimeUnit.MILLISECONDS);
  }
  
  private void inheritedChangedUpdate(DocumentEvent e) {
    super.fireChangedUpdate(e);
  }

  /**
   * Notifies all listeners that have registered interest for
   * notification on this event type.  The event instance
   * is lazily created using the parameters passed into
   * the fire method.
   *
   * @param e the event
   * @see EventListenerList
   */
  protected void fireRemoveUpdate(final DocumentEvent e) {
    if (!(event==null||event.isDone()||event.isCancelled())) {
      event.cancel(false);
    }
    
    event = schedule.schedule(new DocumentEventsRunnable(e, DocumentEvent.EventType.REMOVE), DELAY, TimeUnit.MILLISECONDS);
  }
  
  private void inheritedRemoveUpdate(DocumentEvent e) {
    super.fireRemoveUpdate(e);
  }
  
  private class DocumentEventsRunnable implements Runnable {
    DocumentEvent e;
    DocumentEvent.EventType type;    
    
    public DocumentEventsRunnable(DocumentEvent e, DocumentEvent.EventType type) {
      this.e = e;
      this.type = type;
    }

    public void run() {
      try {
        EventQueue.invokeAndWait(new Runnable() {
          public void run() {
            if (type==DocumentEvent.EventType.CHANGE) {
              inheritedChangedUpdate(e);
            } else if (type==DocumentEvent.EventType.INSERT) {
              inheritedInsertUpdate(e);
            } else if (type==DocumentEvent.EventType.REMOVE) {
              inheritedRemoveUpdate(e);
            }
          }
        });
      } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't notify document contents change", ex);
      }
    }
  }
}
