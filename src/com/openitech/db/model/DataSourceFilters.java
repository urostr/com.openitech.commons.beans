package com.openitech.db.model;

import com.openitech.util.Equals;
import java.text.MessageFormat;
import java.util.HashSet;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;


public class DataSourceFilters extends DbDataSource.SubstSqlParameter {
  private static final String EMPTY = " 0=1 ";
  
  public class SeekType {
    public static final int UPPER_EQUALS = 0;
    public static final int UPPER_BEGINS_WITH = 1;
    public static final int UPPER_END_WITH = 2;
    public static final int UPPER_CONTAINS = 3;
    public static final int EQUALS = 4;
    public static final int GREATER_OR_EQUALS = 5;
    public static final int LESS_OR_EQUALS = 6;
    
    private MessageFormat[] formati = new MessageFormat[] {
      new MessageFormat(" (UPPER({0}) = ?) "), 
      new MessageFormat(" (UPPER({0}) like (?+''%'')) "), 
      new MessageFormat(" (UPPER({0}) like (''%''+?)) "), 
      new MessageFormat(" (UPPER({0}) like (''%''+?+''%'')) "), 
      new MessageFormat(" ({0} = ?) "), 
      new MessageFormat(" ({0} >= ?) "), 
      new MessageFormat(" ({0} <= ?) ")};
    
    private String field;
    private String value = "";
    private int min_length = 3;
    private int i_type = 1;

    
    public SeekType(String field) {
      this.field = field;
    }
    
    public SeekType(String field, int i_type) {
      this.field = field;
      this.i_type = i_type;
    }
    
    public SeekType(String field, int i_type, int min_length) {
      this.field = field;
      this.i_type = i_type;
      this.min_length = min_length;
    }
    
    public boolean setValue(String value) {
      if (value==null)
        value = "";
      else if (value.endsWith("%"))
        value = value.substring(0,value.length()-1);
      if (value.length()<min_length)
        value = "";
      else
        value=value.toUpperCase();
      if (!Equals.equals(getValue(), value)) {
        this.value = value;
        return true;
      } else
        return false;
    }
    
    public boolean setSeekType(int i) {
      boolean result = false;
      
      switch (i)  {
        case 0:
        case 1:
        case 2:
        case 3:
          result=i_type!=i;
          i_type=i;
          break;
        default:
          result=i_type!=1;
          i_type=1;
          break;
      }
      
      return result;
    }
    
    public String getValue() {
      return value;
    }
    
    public StringBuffer getSQLSegment() {
      return formati[i_type].format(new Object[]{field}, new StringBuffer(27), null);
    }
    
    public boolean hasValue() {
      return value!=null && value.length()>0;
    }
  }
  
  public static class RequiredFields {
    SeekType field;
    int no_fields = 1;
    
    public RequiredFields(SeekType field, int no_fields) {
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

  private HashSet<SeekType> seek_types = new HashSet<SeekType>();
  private HashSet<RequiredFields> required = new HashSet<RequiredFields>();
  
  private boolean filterRequired = false;
  
  
  public DataSourceFilters(String replace) {
    super(replace);
    setParameters(false);
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
            for(SeekType seek : seek_types) {
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
      for(SeekType seek : seek_types) {
        if (seek.hasValue()) {
          value.append(value.length()>0?" and ":"").append(seek.getSQLSegment());
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
  public void setSeekType(SeekType seek, int seek_type) {
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
  public void setSeekValue(SeekType seek, String seek_value) {
    if (!seek_types.contains(seek)) {
      seek_types.add(seek);
    }
    if (seek_value==null)
      seek_value = "";
    if (seek.setValue(seek_value.trim())) {
      setParameters(true);
    }
  }
  
  public String getSeekValue(SeekType seek) {
    String result = null;
    
    for(SeekType s : seek_types) {
      if (Equals.equals(s, seek)) {
        if (s.hasValue())
          result = s.getValue();
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