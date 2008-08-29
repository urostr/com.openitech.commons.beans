package com.openitech.db.model;

import com.openitech.util.Equals;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


public class DataSourceFilters extends DbDataSource.SubstSqlParameter {
  private static final ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();
  private static final long DELAY = 9;

  private static final String EMPTY = " 0=1 ";
  
  public abstract static class AbstractSeekType<E> {
    public static final int EQUALS = 4;
    public static final int GREATER_OR_EQUALS = 5;
    public static final int LESS_OR_EQUALS = 6;
    public static final int PREFORMATTED = 7;
    
    protected final MessageFormat[] formati = new MessageFormat[] {
      new MessageFormat(" (UPPER({0}) = ?) "),                    //-0
      new MessageFormat(" (UPPER({0}) like (?+''%'')) "),         //-1
      new MessageFormat(" (UPPER({0}) like (''%''+?)) "),         //-2
      new MessageFormat(" (UPPER({0}) like (''%''+?+''%'')) "),   //-3
      new MessageFormat(" ({0} = ?) "),                           //-4
      new MessageFormat(" ({0} >= ?) "),                          //-5
      new MessageFormat(" ({0} <= ?) "),                          //-6
      new MessageFormat(" ({0}) "),                               //-7
      new MessageFormat(" ({0} like (?+''%'')) "),                //-8
      new MessageFormat(" ({0} like (''%''+?)) "),                //-9
      new MessageFormat(" ({0} like (''%''+?+''%'')) "),          //-10
   };
    
    protected String field;
    protected E value = null;
    protected int def_i_type = 4;
    protected int i_type = 4;
    protected int p_count = 1;

    public AbstractSeekType(String field, int i_type, int p_count) {
      this.field = field;
      this.i_type = i_type;
      this.p_count = p_count;
    }
    
    public abstract boolean setValue(E value);

    public E getValue() {
      return value;
    }
    
    public boolean hasValue() {
      return value!=null;
    }
    
    public boolean setSeekType(int i) {
      boolean result = false;
      
      if (i>=0 && i<formati.length) {
          result=i_type!=i;
          i_type=i;
      } else {
          result=i_type!=def_i_type;
          i_type=def_i_type;
      }
      
      return result;
    }
    
    public StringBuffer getSQLSegment() {
      return formati[i_type].format(new Object[]{field}, new StringBuffer(27), null);
    }
  }
  
  public final static class SeekType extends AbstractSeekType<String> {
    public static final int UPPER_EQUALS = 0;
    public static final int UPPER_BEGINS_WITH = 1;
    public static final int UPPER_END_WITH = 2;
    public static final int UPPER_CONTAINS = 3;
    
    private int min_length = 3;

    public SeekType(String field) {
      this(field, UPPER_EQUALS, 3, 1);
    }
    
    public SeekType(String field, int i_type) {
      this(field, i_type, 3, 1);
    }
    
    public SeekType(String field, int i_type, int min_length) {
      this(field, i_type, min_length, 1);
    }
    
    public SeekType(String field, int i_type, int min_length, int p_count) {
      super(field, i_type, p_count);
      this.def_i_type = 1;
      this.min_length = min_length;
    }
    
    public boolean setValue(String value) {
      if (value==null)
        value = "";
      else if (value.endsWith("%"))
        value = value.substring(0,value.length()-1);
      
      value = value.trim();
      
      if (value.length()<min_length)
        value = "";
      else
        value=caseSensitive?value:value.toUpperCase();
      if (!Equals.equals(getValue(), value)) {
        this.value = value;
        return true;
      } else
        return false;
    }

    public boolean hasValue() {
      return value!=null && value.length()>0;
    }

    /**
     * Holds value of property caseSensitive.
     */
    private boolean caseSensitive = false;

    /**
     * Getter for property caseSensitive.
     * @return Value of property caseSensitive.
     */
    public boolean isCaseSensitive() {
      return this.caseSensitive;
    }

    /**
     * Setter for property caseSensitive.
     * @param caseSensitive New value of property caseSensitive.
     */
    public void setCaseSensitive(boolean caseSensitive) {
      this.caseSensitive = caseSensitive;
    }
  }
  
  public final static class DateSeekType extends AbstractSeekType<java.util.Date> {

    public DateSeekType(String field) {
      this(field, EQUALS, 1);
    }
    
    public DateSeekType(String field, int i_type) {
      this(field, i_type, 1);
    }
    
    public DateSeekType(String field, int i_type, int p_count) {
      super(field, i_type, p_count);
    }
    
    public boolean setValue(java.util.Date value) {
      value = value==null?null:new java.sql.Date(value.getTime());
      if (!Equals.equals(getValue(), value)) {
        this.value = value;
        return true;
      } else
        return false;
    }
  }
  public final static class IntegerSeekType extends AbstractSeekType<java.lang.Integer> {

    public IntegerSeekType(String field) {
      this(field, EQUALS, 1);
    }
    
    public IntegerSeekType(String field, int i_type) {
      this(field, i_type, 1);
    }
    
    public IntegerSeekType(String field, int i_type, int p_count) {
      super(field, i_type, p_count);
    }
    
    public boolean setValue(java.lang.Integer value) {
     if (!Equals.equals(getValue(), value)) {
        this.value = value;
        return true;
      } else
        return false;
    }

    public boolean hasValue() {
      return value!=null && value.intValue()!=Integer.MIN_VALUE && value.intValue()!=Integer.MAX_VALUE;
    }
  }
  
  public static class RequiredFields {
    AbstractSeekType field;
    int no_fields = 1;
    
    public RequiredFields(AbstractSeekType field, int no_fields) {
      this.field = field;
      this.no_fields = no_fields;
    }
  }
  
  public static class FilterDocumentListener implements DocumentListener {
    DataSourceFilters filter;
    SeekType seek_type;
    
    public FilterDocumentListener(DataSourceFilters filter, SeekType seek_type) {
      this.filter = filter;
      this.seek_type = seek_type;
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
      filter.setSeekValue(seek_type, getText(e));
    }
    public void insertUpdate(DocumentEvent e) {
      filter.setSeekValue(seek_type, getText(e));
    }
    public void removeUpdate(DocumentEvent e) {
      filter.setSeekValue(seek_type, getText(e));
    }
  }
  
  public static class FilterDocumentCaretListener implements CaretListener {
    private Future event = null;


    Document document;
    DataSourceFilters filter;
    SeekType seek_type;
    
    public FilterDocumentCaretListener(Document document, DataSourceFilters filter, SeekType seek_type) {
      this.document = document;
      this.filter = filter;
      this.seek_type = seek_type;
    }
    
    private String getText(CaretEvent e) {
      String txt;
      try {
        //int min = Math.min(e.getDot(), e.getMark());
        int max = Math.max(e.getDot(), e.getMark());
        //txt = document.getText(min==max?0:min, max);
        txt = document.getText(0, max);
      } catch (BadLocationException err) {
        txt = null;
      }
      return txt;
    }
    
//    private static boolean isFullTextSearch(Document document, CaretEvent e) {
//      return (e.getDot()!=e.getMark())&&(e.getDot()!=0 || e.getMark()!=document.getLength());
//    }
    public void caretUpdate(CaretEvent e) {
      caretUpdate(e, DELAY);
    }
    
    public void caretUpdate(CaretEvent e, long delay) {
      if (!(event==null||event.isDone()||event.isCancelled())) {
        event.cancel(false);
      }

      event = schedule.schedule(new SeekValueUpdateRunnable<String>(filter, seek_type, getText(e)), delay, TimeUnit.MILLISECONDS);
    }
  }

    
  private static class SeekValueUpdateRunnable<E> implements Runnable {
    DataSourceFilters filter;
    AbstractSeekType<E> seek_type; 
    E value;
    
    public SeekValueUpdateRunnable(DataSourceFilters filter, AbstractSeekType<E> seek_type, E value) {
      this.filter = filter;
      this.seek_type = seek_type;
      this.value = value;
    }
    
    public void run() {
      filter.setSeekValue(seek_type, value);
    }
  }
  private HashSet<AbstractSeekType> seek_types = new HashSet<AbstractSeekType>();
  private HashSet<RequiredFields> required = new HashSet<RequiredFields>();
  
  private boolean filterRequired = false;
  
  
  public DataSourceFilters(String replace) {
    super(replace);
    setParameters(false);
  }
  
  public boolean addRequired(AbstractSeekType field) {
    return addRequired(field, 1);
  }
  
  public boolean addRequired(AbstractSeekType field, int no_fields) {
    return required.add(new RequiredFields(field, no_fields));
  }
  
  public boolean addRequired(RequiredFields field) {
    return required.add(field);
  }
  
  public boolean removeRequired(RequiredFields field) {
    return required.remove(field);
  }
  
  public void clearRequired(RequiredFields field) {
    required.clear();
  }
  
  private void setParameters(boolean notify) {
    StringBuffer value = new StringBuffer(108);
    
    parameters.clear();
    
    boolean do_seek = true;
    if (!required.isEmpty()) {
      do_seek=false;
      for(RequiredFields type : required) {
        if (seek_types.contains(type.field)&&type.field.hasValue()) {
          if (type.no_fields>1) {
            int count = 1;
            for(AbstractSeekType seek : seek_types) {
              if (!seek.equals(type.field)&&seek.hasValue())
                count++;
            }
            do_seek = count>=type.no_fields;
          }  else
            do_seek=true;
          if (do_seek)
            break;
        }
      }
    }
    
    if (do_seek) {
      for(AbstractSeekType seek : seek_types) {
        if (seek.hasValue()) {
          value.append(value.length()>0?" and ":"").append(seek.getSQLSegment());
          for (int p=1; p<=seek.p_count; p++)
            parameters.add(seek.getValue());
        }
      }
    }
    
    if (filterRequired&&(value.length()==0)) {
      value.append(EMPTY);
    }
    
    if (!(super.getValue().equals(EMPTY)&&value.equals(EMPTY))) {
      super.setValue(value.toString());
      firePropertyChange("query", notify, false);
    }
  }
  
  public void setValue(String value) {
    throw new UnsupportedOperationException("Can't set value");
  }
  
  /**
   * Setter for property itype.
   * @param itype New value of property itype.
   */
  public void setSeekType(AbstractSeekType seek, int seek_type) {
    if (!seek_types.contains(seek)) {
      seek_types.add(seek);
    }
    if (seek.setSeekType(seek_type)) {
      setParameters(true);
    }
  }
  
  
  /**
   * Setter for property itype.
   * @param itype New value of property itype.
   */
  public <E> void setSeekValue(AbstractSeekType<E> seek, E seek_value) {
    if (!seek_types.contains(seek)) {
      seek_types.add(seek);
    }
    if (seek.setValue(seek_value)) {
      setParameters(true);
    }
  }
  
  public <E> E getSeekValue(AbstractSeekType<E> seek) {
    E result = null;
    
    for(AbstractSeekType s : seek_types) {
      if (Equals.equals(s, seek)) {
        if (s.hasValue())
          result = (E) s.getValue();
      }
    }
    
    return result;
  }
  
  public void clear() {
    if (!seek_types.isEmpty()) {
      seek_types.clear();
      setParameters(true);
    }
  }

  public void setFilterRequired(boolean filterRequired) {
    if (this.filterRequired != filterRequired) {
      this.filterRequired = filterRequired;
      setParameters(true);
    }
  }

  public boolean isFilterRequired() {
    return filterRequired;
  }
}