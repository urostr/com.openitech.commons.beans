package com.openitech.db.filters;

import com.openitech.db.filters.Scheduler.SeekValueUpdateRunnable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;


public class FilterDocumentListener extends Scheduler implements DocumentListener {
  protected DataSourceFilters filter;
  protected DataSourceFilters.SeekType seek_type;
  
  public FilterDocumentListener(DataSourceFilters filter, DataSourceFilters.SeekType seek_type) {
    this(filter, seek_type, Scheduler.DELAY);
  }
  
  public FilterDocumentListener(DataSourceFilters filter, DataSourceFilters.SeekType seek_type, long delay) {
    this.filter = filter;
    this.seek_type = seek_type;
    this.delay = delay;
  }
  
  public String getText(DocumentEvent e) {
    String txt;
    try {
      txt = e.getDocument().getText(0, e.getDocument().getLength());
    } catch (BadLocationException err) {
      txt = null;
    }
    return txt;
  }
  
  public void changedUpdate(DocumentEvent e) {
    setSeekValue(e);
  }
  public void insertUpdate(DocumentEvent e) {
    setSeekValue(e);
  }
  public void removeUpdate(DocumentEvent e) {
    setSeekValue(e);
  }
  
  protected void setSeekValue(final DocumentEvent e) {
    schedule(new SeekValueUpdateRunnable<String>(filter, seek_type, getText(e)));
  }
}