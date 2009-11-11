package com.openitech.db.filters;

import com.openitech.db.model.*;
import com.openitech.formats.FormatFactory;
import com.openitech.sql.events.Event;
import com.openitech.util.Equals;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;

public class DataSourceFilters extends DbDataSource.SubstSqlParameter {

  private static final String EMPTY = " 0=1 ";

  public abstract static class AbstractSeekType<E> {

    /**
     * Equals has value 4
     */
    public static final int EQUALS = 4;
    /**
     * GREATER_OR_EQUALS has value 5
     */
    public static final int GREATER_OR_EQUALS = 5;
    /**
     * LESS_OR_EQUALS has value 6
     */
    public static final int LESS_OR_EQUALS = 6;
    /**
     * PREFORMATTED has value 7
     */
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
      new MessageFormat(" ({0} like (''%''+?+''%'')) "), //-10
    };
    protected final String[] descriptions = new String[]{
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
    protected String operator = "and";

    /**
     * Get the value of operator
     *
     * @return the value of operator
     */
    public String getOperator() {
      return operator;
    }

    /**
     * Set the value of operator
     *
     * @param operator new value of operator
     */
    public void setOperator(String operator) {
      this.operator = operator;
    }

    /**
     * Get the value of automatic
     *
     * @return the value of automatic
     */
    public boolean isAutomatic() {
      return documents == null;
    }
    private Document[] documents;

    /**
     * Get the value of documents
     *
     * @return the value of documents
     */
    public Document[] getDocuments() {
      return documents;
    }

    /**
     * Set the value of documents
     *
     * @param documents new value of documents
     */
    public void setDocuments(Document... documents) {
      setDocuments(null, documents);
    }

    /**
     * Set the value of documents
     *
     * @param documents new value of documents
     */
    public void setDocuments(DataSourceFilters filter, Document... documents) {
      if (filter != null) {
        for (Document document : documents) {
          document.addDocumentListener(new FilterDocumentListener(filter, this));
        }
      }
      this.documents = documents;
    }

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
      return this.toString() + " " + descriptions[i_type] + " " + (value == null ? "prazen" : value.toString());
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

      int old_value = i_type;

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

    public StringBuilder getSQLSegment() {
      StringBuffer sb = formati[i_type].format(new Object[]{field}, new StringBuffer(27), null);
      return new StringBuilder(sb);
    }

    @Override
    public String toString() {
      return name != null ? name : super.toString();
    }
  }

  public final static class SeekType extends AbstractSeekType<String> {

    /**
     * UPPER_EQUALS has value 0
     */
    public static final int UPPER_EQUALS = 0;
    /**
     * UPPER_BEGINS_WITH has value 1
     */
    public static final int UPPER_BEGINS_WITH = 1;
    /**
     * UPPER_END_WITH has value 2
     */
    public static final int UPPER_END_WITH = 2;
    /**
     * UPPER_CONTAINS has value 3
     */
    public static final int UPPER_CONTAINS = 3;
    private int min_length = 3;

    /**
     * Set seekType
     * @param filed  Polje po katerem se išèe
     *    * default type = UPPER_EQUALS 
     *    * default minimum length = 3 
     *    * default count = 1 
     */
    public SeekType(String field) {
      this(field, UPPER_EQUALS, 3, 1);
    }

    /**
     * Set seekType
     * @param filed  Polje po katerem se išèe
     * @param type  Tip iskanja 
     *    - UPPER_EQUALS (je enako) 
     *    - UPPER_BEGINS_WITH (se zaène z) 
     *    - UPPER_END_WITH (se konèa z) 
     *    - UPPER_CONTAINS (vsebuje) 
     *
     *     * default minimum length = 3 
     *     * default count = 1 
     */
    public SeekType(String field, int i_type) {
      this(field, i_type, 3, 1);
    }

    /**
     * Set seekType
     * @param filed  Polje po katerem se išèe
     * @param type  Tip iskanja 
     *    - UPPER_EQUALS (je enako) 
     *    - UPPER_BEGINS_WITH (se zaène z) 
     *    - UPPER_END_WITH (se konèa z) 
     *    - UPPER_CONTAINS (vsebuje) 
     * @param minimal length - Minimalna dolžina iskanja
     *     
     *    * default count = 1 
     */
    public SeekType(String field, int i_type, int min_length) {
      this(field, i_type, min_length, 1);
    }

    /**
     * Set seekType
     * @param filed  Polje po katerem se išèe
     * @param type  Tip iskanja 
     *    - UPPER_EQUALS (je enako) 
     *    - UPPER_BEGINS_WITH (se zaène z) 
     *    - UPPER_END_WITH (se konèa z) 
     *    - UPPER_CONTAINS (vsebuje) 
     * @param minimal length - Minimalna dolžina iskanja
     * @param count 
     */
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

    /**
     * Set DateSeekType
     * @param filed  Polje po katerem se išèe
     *    * default type = EQUALS
     *    * default count = 1
     */
    public DateSeekType(String field) {
      this(field, EQUALS, 1);
    }

    /**
     * Set DateSeekType
     * @param filed  Polje po katerem se išèe
     * @param type   Tip iskanja
     *    * default count = 1
     */
    public DateSeekType(String field, int i_type) {
      this(field, i_type, 1);
    }

    /**
     * Set DateSeekType
     * @param filed  Polje po katerem se išèe
     * @param type   Tip iskanja
     * @param count
     */
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

    /**
     * Set DateSeekType
     * @param filed  Polje po katerem se išèe
     *  SQL:: filed + " BETWEEN ? AND ? "
     *    * default formatted = true
     */
    public BetweenDateSeekType(String field) {
      this(field + " BETWEEN ? AND ? ", true);
    }

    /**
     * Set DateSeekType
     * @param filed  Polje po katerem se išèe
     *  SQL:: filed + " BETWEEN ? AND ? "
     * @param formatted
     *  * default count = 1
     */
    public BetweenDateSeekType(String field, boolean formatted) {
      super(field, SeekType.PREFORMATTED, 1);
    }

    @Override
    public String getDescription() {
      return this.toString() + " je" + (value.get(0).getTime() > 0 ? " od " + FormatFactory.DATE_FORMAT.format(value.get(0)) : "") + " do " + FormatFactory.DATE_FORMAT.format(value.get(1));
    }

    @Override
    public boolean setValue(List<Date> value) {
      if (!Equals.equals(getValue(), value)) {
        if (value != null && value.size() == 2) {
          java.sql.Timestamp from = value.get(0) == null ? null : new java.sql.Timestamp(value.get(0).getTime());
          java.sql.Timestamp to = value.get(1) == null ? null : new java.sql.Timestamp(value.get(1).getTime());
          value.set(0, from);
          value.set(1, to);

          if (from != null || to != null) {
            this.value = value;
          } else {
            this.value = null;
          }
          return true;
        } else {
          return false;
        }
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

    @Override
    public boolean setValue(java.lang.Integer value) {
      if (!Equals.equals(getValue(), value)) {
        this.value = value;
        return true;
      } else {
        return false;
      }
    }

    @Override
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
  protected HashSet<AbstractSeekType> seek_types = new HashSet<AbstractSeekType>();
  protected HashSet<RequiredFields> required = new HashSet<RequiredFields>();
  protected boolean filterRequired = false;

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

  public void clearRequired() {
    required.clear();
  }

  protected void setParameters(boolean notify) {
    StringBuilder value = new StringBuilder(108);

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
            value.append(value.length() > 0 ? " "+seek.getOperator()+" " : "").append(seek.getSQLSegment());
            for (int p = 1; p <= seek.p_count; p++) {
              if (seek instanceof ValuesList) {
                for (Object v : ((ValuesList) seek).getValues()) {
                  parameters.add(v);
                }
              } else if (seek.getValue() instanceof java.util.Collection) {
                for (Object v : (java.util.Collection) seek.getValue()) {
                  parameters.add(v);
                }
              } else {
                parameters.add(seek.getValue());
              }
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
    if (this.disabled != disabled) {
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

  @Override
  public void setValue(String value) {
    throw new UnsupportedOperationException("Can't set value");
  }

  public boolean hasValue() {
    boolean result = false;
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
      for (AbstractSeekType seek_type : seek_types) {
        result = result || seek_type.hasValue();
      }
    }

    return result;
  }

  /**
   * Setter for property itype.
   * @param itype New value of property itype.
   */
  public void setSeekType(AbstractSeekType seek, int seek_type) {
    if (!seek_types.contains(seek)) {
      seek_types.add(seek);
    }
    int old_value = seek.getSeekType();
    if (seek.setSeekType(seek_type)) {
      firePropertyChange("seek_type", old_value, seek.getSeekType());
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

  public static interface Factory<T extends DataSourceFilters> {

    public T newInstance(String replace);
  }

  public final static class SifrantSeekType extends AbstractSeekType<String> {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    /**
     * UPPER_EQUALS has value 0
     */
    public static final int UPPER_EQUALS = 0;
    private int min_length = 1;
    private FutureTask<DbComboBoxModel> model;

    public SifrantSeekType(String field, final String sifrantSkupina, final String sifrantOpis) {
      this(field, sifrantSkupina, sifrantOpis, null);
    }

    public SifrantSeekType(String field, final String sifrantSkupina, final String sifrantOpis, final String textNotDefined) {
      super(field, UPPER_EQUALS, 1);
      this.def_i_type = 1;
      this.sifrantSkupina = sifrantSkupina;
      this.sifrantOpis = sifrantOpis;
      this.textNotDefined = textNotDefined == null ? "Ni doloèen" : textNotDefined;

      this.model = new FutureTask<DbComboBoxModel>(new Callable<DbComboBoxModel>() {

        @Override
        public DbComboBoxModel call() {
          DbSifrantModel result = null;
          try {
            result = new DbSifrantModel(textNotDefined);
            result.setSifrantOpis(sifrantOpis);
            result.setSifrantSkupina(sifrantSkupina);
          } catch (SQLException ex) {
            Logger.getLogger(DataSourceFilters.class.getName()).log(Level.SEVERE, null, ex);
          }
          return result;
        }
      });

    }

    public SifrantSeekType(String field, Callable<DbComboBoxModel> callable) {
      this(field, new FutureTask<DbComboBoxModel>(callable));
    }

    public SifrantSeekType(String field, FutureTask<DbComboBoxModel> model) {
      super(field, UPPER_EQUALS, 1);
      this.model = model;
      this.sifrantSkupina = null;
      this.sifrantOpis = null;
      this.textNotDefined = null;
    }
    private final String textNotDefined;

    /**
     * Get the value of textNiDolocen
     *
     * @return the value of textNiDolocen
     */
    public String getTextNotDefined() {
      return textNotDefined;
    }
    /**
     * Holds value of property sifrantSkupina.
     */
    private final String sifrantSkupina;

    /**
     * Getter for property sifrantSkupina.
     * @return Value of property sifrantSkupina.
     */
    public String getSifrantSkupina() {
      return this.sifrantSkupina;
    }
    /**
     * Holds value of property sifrantOpis.
     */
    private final String sifrantOpis;

    /**
     * Getter for property sifrantOpis.
     * @return Value of property sifrantOpis.
     */
    public String getSifrantOpis() {
      return this.sifrantOpis;
    }

    public DbComboBoxModel getModel() {
      DbComboBoxModel result = null;
      try {
        executorService.execute(model);
        result = model.get();
      } catch (InterruptedException ex) {
        Logger.getLogger(DataSourceFilters.class.getName()).log(Level.SEVERE, null, ex);
      } catch (ExecutionException ex) {
        Logger.getLogger(DataSourceFilters.class.getName()).log(Level.SEVERE, null, ex);
      }
      return result;
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

  //TODO: FINISH THE EVENT FILTER, USING THE EventQuery and SqlUtilites class
  public final static class EventSeekType extends DataSourceFilters.AbstractSeekType<Event> implements ValuesList {

    private String eventProperty;

    public EventSeekType(String field, String eventProperty) {
      super(field, PREFORMATTED, 1);
      this.eventProperty = eventProperty;
    }

    @Override
    public boolean setValue(Event value) {
      if (!Equals.equals(getValue(), value)) {
        this.value = value;
        if (value != null) {
        }
        return true;
      } else {
        return false;
      }
    }

    @Override
    public List getValues() {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }

  public abstract static class InnerJoinSeekType<T> extends AbstractSeekType<T> {

    private static final String pattern = " INNER JOIN {0}\n " + " ON ( {1} {2} )";
    private String joinTable;
    private String joinCondition;

    public InnerJoinSeekType(String joinTable, String joinCondition, String field) {
//    super(" INNER JOIN " + tableName + " " + alias +
//            " \nON ( " + alias + ".PPVrednostID = " + joinON + " " +
//            " \nAND " + alias + "." + valueType + " = ?", PREFORMATTED, 1);
      super(field, EQUALS, 1);
      this.joinTable = joinTable;
      this.joinCondition = joinCondition;
    }

    @Override
    public StringBuilder getSQLSegment() {
      return new StringBuilder(MessageFormat.format(pattern, joinTable, joinCondition, super.getSQLSegment()));
    }
    private boolean caseSensitive = false;

    /**
     * Getter for property caseSensitive.
     * @return Value of property caseSensitive.
     */
    public boolean isCaseSensitive() {
      return this.caseSensitive;
    }

    @Override
    public String getOperator() {
      return "";
    }

    @Override
    public void setOperator(String operator) {
      throw new UnsupportedOperationException("InnerJoinSeekType ne podpira dodatnih operatorjev");
    }

    /**
     * Setter for property caseSensitive.
     * @param caseSensitive New value of property caseSensitive.
     */
    public void setCaseSensitive(boolean caseSensitive) {
      this.caseSensitive = caseSensitive;
    }
  }
}
