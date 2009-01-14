package com.openitech.db.filters;

import com.openitech.db.model.*;
import com.openitech.formats.FormatFactory;
import com.openitech.util.Equals;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class DataSourceFilters extends DbDataSource.SubstSqlParameter {

  private static final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
  private static final String EMPTY = " 0=1 ";

  public abstract static class AbstractSeekType<E> {

    public static final int EQUALS = 4;
    public static final int GREATER_OR_EQUALS = 5;
    public static final int LESS_OR_EQUALS = 6;
    public static final int PREFORMATTED = 7;
    protected final MessageFormat[] formati = new MessageFormat[]{
      new MessageFormat(" (UPPER({0}) = ?) "), //-0
      new MessageFormat(" (UPPER({0}) like (?+''%'')) "), //-1
      new MessageFormat(" (UPPER({0}) like (''%''+?)) "), //-2
      new MessageFormat(" (UPPER({0}) like (''%''+?+''%'')) "), //-3
      new MessageFormat(" ({0} = ?) "), //-4
      new MessageFormat(" ({0} >= ?) "), //-5
      new MessageFormat(" ({0} <= ?) "), //-6
      new MessageFormat(" ({0}) "), //-7
      new MessageFormat(" ({0} like (?+''%'')) "), //-8
      new MessageFormat(" ({0} like (''%''+?)) "), //-9
      new MessageFormat(" ({0} like (''%''+?+''%'')) "),          //-10
    };
    protected final String[] descriptions = new String[] {
       "je enak",
       "se zaène z",
       "se konèa z",
       "vsebuje",
       "je enak",
       "je veèji ali enak",
       "je manjši ali enak",
       "je",
       "se zaène z",
       "se konèa z",
       "vsebuje"
    };
    protected String field;
    protected E value = null;
    protected int def_i_type = 4;
    protected int i_type = 4;
    protected int p_count = 1;
    private String name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
      return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
      this.name = name;
    }

    public String getDescription() {
      return this.toString()+" "+descriptions[i_type]+" "+value.toString();
    }

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
      return value != null;
    }

    public boolean setSeekType(int i) {
      boolean result = false;

      if (i >= 0 && i < formati.length) {
        result = i_type != i;
        i_type = i;
      } else {
        result = i_type != def_i_type;
        i_type = def_i_type;
      }

      return result;
    }

    public int getSeekType() {
      return i_type;
    }

    public StringBuffer getSQLSegment() {
      return formati[i_type].format(new Object[]{field}, new StringBuffer(27), null);
    }

    @Override
    public String toString() {
      return name!=null?name:super.toString();
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
      if (value == null) {
        value = "";
      } else if (value.endsWith("%")) {
        value = value.substring(0, value.length() - 1);
      }
      value = value.trim();

      if (value.length() < min_length) {
        value = "";
      } else {
        value = caseSensitive ? value : value.toUpperCase();
      }
      if (!Equals.equals(getValue(), value)) {
        this.value = value;
        return true;
      } else {
        return false;
      }
    }

    public boolean hasValue() {
      return value != null && value.length() > 0;
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
      value = value == null ? null : new java.sql.Date(value.getTime());
      if (!Equals.equals(getValue(), value)) {
        this.value = value;
        return true;
      } else {
        return false;
      }
    }
  }

  public final static class BetweenDateSeekType extends AbstractSeekType<java.util.List<java.util.Date>> {

    public BetweenDateSeekType(String field) {
      super(field+" BETWEEN ? AND ? ", SeekType.PREFORMATTED, 1);
    }

    @Override
    public String getDescription() {
      return this.toString()+" je"+ (value.get(0).getTime()>0?" od "+FormatFactory.DATE_FORMAT.format(value.get(0)):"")+" do "+FormatFactory.DATE_FORMAT.format(value.get(1));
    }

    @Override
    public boolean setValue(List<Date> value) {
      if (!Equals.equals(getValue(), value)) {
        if (value!=null&&value.size()==2) {
          java.sql.Date from = value.get(0) == null ? null : new java.sql.Date(value.get(0).getTime());
          java.sql.Date to = value.get(1) == null ? null : new java.sql.Date(value.get(1).getTime());
          value.set(0, from);
          value.set(1, to);

          if (from!=null||to!=null) {
            this.value=value;
          } else {
            this.value=null;
          }
          return true;
        } else
          return false;
      } else {
        return false;
      }
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
      } else {
        return false;
      }
    }

    public boolean hasValue() {
      return value != null && value.intValue() != Integer.MIN_VALUE && value.intValue() != Integer.MAX_VALUE;
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

    if (isDisabled()) {
      value.append(EMPTY);
    } else {
      boolean do_seek = true;
      if (!required.isEmpty()) {
        do_seek = false;
        for (RequiredFields type : required) {
          if (seek_types.contains(type.field) && type.field.hasValue()) {
            if (type.no_fields > 1) {
              int count = 1;
              for (AbstractSeekType seek : seek_types) {
                if (!seek.equals(type.field) && seek.hasValue()) {
                  count++;
                }
              }
              do_seek = count >= type.no_fields;
            } else {
              do_seek = true;
            }
            if (do_seek) {
              break;
            }
          }
        }
      }

      if (do_seek) {
        for (AbstractSeekType seek : seek_types) {
          if (seek.hasValue()) {
            value.append(value.length() > 0 ? " and " : "").append(seek.getSQLSegment());
            for (int p = 1; p <= seek.p_count; p++) {
              if (seek.getValue() instanceof java.util.List) {
                for (Object v:(java.util.List) seek.getValue()) {
                  parameters.add(v);
                }
              } else
                parameters.add(seek.getValue());
            }
          }
        }
      }
    }

    if (filterRequired && (value.length() == 0)) {
      value.append(EMPTY);
    }

    if (!(super.getValue().equals(EMPTY) && value.toString().equals(EMPTY))) {
      super.setValue(value.toString());
      firePropertyChange("query", notify, false);
    }
  }
  
  protected boolean disabled = false;

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    if (this.disabled!=disabled) {
      this.disabled = disabled;
      setParameters(true);
      firePropertyChange("query", true, false);
    }
  }

  @Override
  public void reloadDataSources() {
    if (!isDisabled()) {
      super.reloadDataSources();
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

    for (AbstractSeekType s : seek_types) {
      if (Equals.equals(s, seek)) {
        if (s.hasValue()) {
          result = (E) s.getValue();
        }
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