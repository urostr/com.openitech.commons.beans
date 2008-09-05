package com.openitech.db.filters;

import com.openitech.db.filters.Scheduler.SeekValueUpdateRunnable;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


public class FilterDocumentCaretListener extends Scheduler implements CaretListener {
  protected Document document;
  protected DataSourceFilters filter;
  protected DataSourceFilters.SeekType seek_type;

  public FilterDocumentCaretListener(Document document, DataSourceFilters filter, DataSourceFilters.SeekType seek_type) {
    this(document, filter, seek_type, Scheduler.DELAY);
  }
  
  public FilterDocumentCaretListener(Document document, DataSourceFilters filter, DataSourceFilters.SeekType seek_type, long delay) {
    this.document = document;
    this.filter = filter;
    this.seek_type = seek_type;
    this.delay = delay;
  }
  
  private String getText(CaretEvent e) {
    String txt;
    try {
      int min = Math.min(e.getDot(), e.getMark());
      //int max = Math.max(e.getDot(), e.getMark());
      //txt = document.getText(min==max?0:min, max);
      txt = document.getText(0, min==0?document.getLength():min);
    }  catch (BadLocationException err) {
      txt = null;
    }
    return txt;
  }  

  public void caretUpdate(CaretEvent e) {
    caretUpdate(e, delay);
  }
  
  protected void caretUpdate(CaretEvent e, long delay) {
    schedule(new SeekValueUpdateRunnable(filter, seek_type, getText(e)), delay);
  }
}