package com.openitech.db.filters;

import com.openitech.db.model.xml.config.SeekLayout;
import com.openitech.value.ValuesList;
import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.*;
import com.openitech.events.concurrent.DataSourceEvent;
import com.openitech.text.FormatFactory;
import com.openitech.util.Equals;
import com.openitech.util.Telefon;
import com.openitech.util.TelefonskeStevilke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;

public class DataSourceFilters extends DbDataSource.SubstSqlParameter {

  private static final String EMPTY = " 0=1 ";

  //Uporabljamo za locevanje med web in sql dostopi
  public interface Reader {

    public DataSourceFilters getDataSourceFilter(String name);
  }

  public AbstractSeekType getSeekType(String name) {
    AbstractSeekType result = null;
    try {
      java.lang.reflect.Field field = this.getClass().getField(name);
      result = (AbstractSeekType) field.get(this);
    } catch (IllegalArgumentException ex) {
      Logger.getLogger(DataSourceFilters.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(DataSourceFilters.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NoSuchFieldException ex) {
      Logger.getLogger(DataSourceFilters.class.getName()).log(Level.SEVERE, null, ex);
    } catch (SecurityException ex) {
      Logger.getLogger(DataSourceFilters.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  public abstract static class AbstractSeekType<E> {

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
    /**
     * BEGINS_WITH has value 8
     */
    public static final int BEGINS_WITH = 8;
    /**
     * ENDS_WITH has value 9
     */
    public static final int ENDS_WITH = 9;
    /**
     * CONTAINS has value 10
     */
    public static final int CONTAINS = 10;
    /**
    /**
     * Equals has value 11
     */
    public static final int CI_VARCHAR_EQUALS = 11;
    /**
     * BEGINS_WITH has value 12
     */
    public static final int CI_VARCHAR_BEGINS_WITH = 12;
    /**
     * ENDS_WITH has value 13
     */
    public static final int CI_VARCHAR_ENDS_WITH = 13;
    /**
     * CONTAINS has value 14
     */
    public static final int CI_VARCHAR_CONTAINS = 14;
    /**
     * GREATER_OR_EQUALS has value 5
     */
    public static final int CI_GREATER_OR_EQUALS = 15;
    /**
     * LESS_OR_EQUALS has value 6
     */
    public static final int CI_LESS_OR_EQUALS = 16;
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
      new MessageFormat(" ({0} = CAST(? AS VARCHAR(700))) "), //-11
      new MessageFormat(" ({0} like (CAST(? AS VARCHAR(700))+''%'')) "), //-12
      new MessageFormat(" ({0} like (''%''+CAST(? AS VARCHAR(700)))) "), //-13
      new MessageFormat(" ({0} like (''%''+CAST(? AS VARCHAR(700))+''%'')) "), //-14
      new MessageFormat(" ({0} >= CAST(? AS VARCHAR(700))) "), //-15
      new MessageFormat(" ({0} <= CAST(? AS VARCHAR(700))) "), //-16
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
      "vsebuje",
      "je",
      "se zaène z",
      "se konèa z",
      "vsebuje",
      "je veèji ali enak",
      "je manjši ali enak"
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

    public void setParameter_count(int parameter_count) {
      this.p_count = parameter_count;
    }

    public String getField() {
      return field;
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
      int seekType = i_type;

      if (isCaseInsensitive()) {
        if (!isConvertToVarchar()) {
          switch (i_type) {
            case UPPER_EQUALS:
              seekType = EQUALS;
              break;
            case UPPER_BEGINS_WITH:
              seekType = BEGINS_WITH;
              break;
            case UPPER_END_WITH:
              seekType = ENDS_WITH;
              break;
            case UPPER_CONTAINS:
              seekType = CONTAINS;
              break;
          }
        } else {
          switch (i_type) {
            case EQUALS:
            case UPPER_EQUALS:
              seekType = CI_VARCHAR_EQUALS;
              break;
            case UPPER_BEGINS_WITH:
              seekType = CI_VARCHAR_BEGINS_WITH;
              break;
            case UPPER_END_WITH:
              seekType = CI_VARCHAR_ENDS_WITH;
              break;
            case UPPER_CONTAINS:
              seekType = CI_VARCHAR_CONTAINS;
              break;
            case GREATER_OR_EQUALS:
              seekType = CI_GREATER_OR_EQUALS;
              break;
            case LESS_OR_EQUALS:
              seekType = CI_LESS_OR_EQUALS;
              break;
          }
        }
      }

      StringBuffer sb = formati[seekType].format(new Object[]{field}, new StringBuffer(27), null);
      return new StringBuilder(sb);
    }

    @Override
    public String toString() {
      return name != null ? name : super.toString();
    }
    private boolean caseInsensitive = ConnectionManager.getInstance().isCaseInsensitive();

    /**
     * Get the value of caseInsensitive
     *
     * @return the value of caseInsensitive
     */
    public boolean isCaseInsensitive() {
      return caseInsensitive;
    }

    /**
     * Set the value of caseInsensitive
     *
     * @param caseInsensitive new value of caseInsensitive
     */
    public void setCaseInsensitive(boolean caseInsensitive) {
      this.caseInsensitive = caseInsensitive;
    }
    private boolean convertToVarchar = ConnectionManager.getInstance().isConvertToVarchar();

    /**
     * Get the value of convertToVarchar
     *
     * @return the value of convertToVarchar
     */
    public boolean isConvertToVarchar() {
      return convertToVarchar;
    }

    /**
     * Set the value of convertToVarchar
     *
     * @param convertToVarchar new value of convertToVarchar
     */
    public void setConvertToVarchar(boolean convertToVarchar) {
      this.convertToVarchar = convertToVarchar;
    }
    protected SeekLayout layout = null;

    /**
     * Get the value of layout
     *
     * @return the value of layout
     */
    public SeekLayout getLayout() {
      return layout;
    }

    /**
     * Set the value of layout
     *
     * @param layout new value of layout
     */
    public void setLayout(SeekLayout layout) {
      this.layout = layout;
    }
  }

  public final static class SeekType extends AbstractSeekType<String> {

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

    public void setMin_length(int min_length) {
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
    BetweenDateDocumentListener betweenDateDocumentListener;

    @Override
    public void setDocuments(DataSourceFilters filter, Document... documents) {
      if (betweenDateDocumentListener != null) {
        betweenDateDocumentListener.from.removeDocumentListener(betweenDateDocumentListener);
        betweenDateDocumentListener.to.removeDocumentListener(betweenDateDocumentListener);
      }
      if ((documents != null) && (documents.length >= 2)) {
        if (betweenDateDocumentListener == null) {
          betweenDateDocumentListener = new BetweenDateDocumentListener(filter, this, documents[0], documents[1]);
        } else {
          betweenDateDocumentListener.from = documents[0];
          betweenDateDocumentListener.to = documents[1];
        }
        betweenDateDocumentListener.from.addDocumentListener(betweenDateDocumentListener);
        betweenDateDocumentListener.to.addDocumentListener(betweenDateDocumentListener);
      }
      super.setDocuments(filter, documents);
    }

    private static class BetweenDateDocumentListener extends FilterDocumentListener {

      javax.swing.text.Document from;
      javax.swing.text.Document to;

      public BetweenDateDocumentListener(DataSourceFilters filter, DataSourceFilters.BetweenDateSeekType seek_type, javax.swing.text.Document from, javax.swing.text.Document to) {
        super(filter, seek_type);
        this.from = from;
        this.to = to;
      }

      @Override
      protected void setSeekValue(DocumentEvent e) {
        java.util.Date from_date;
        try {
          from_date = FormatFactory.DATE_FORMAT.parse(getText(from));
        } catch (ParseException ex) {
          //from_date = Calendar.getInstance().getTime();
          from_date = null;
        }
        java.util.Date to_date;
        try {
          to_date = FormatFactory.DATE_FORMAT.parse(getText(to));
        } catch (ParseException ex) {
          if (from_date == null) {
            to_date = null;
          } else {
            to_date = Calendar.getInstance().getTime();
          }
        }

        if (to_date != null) {
          java.util.Calendar calendar = Calendar.getInstance();
          calendar.setTime(to_date);
          calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
          calendar.set(java.util.Calendar.MINUTE, 59);
          calendar.set(java.util.Calendar.SECOND, 59);
          calendar.set(java.util.Calendar.MILLISECOND, 0);
          to_date = calendar.getTime();
        }

        if (from_date == null && to_date != null) {
          from_date = new java.util.Date(0);
        }

        java.util.List<java.util.Date> value = new ArrayList<java.util.Date>(2);
        value.add(from_date);
        value.add(to_date);

        schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, value));
      }
    }
  }

  public final static class BooleanSeekType extends AbstractSeekType<java.lang.Boolean> {

    public BooleanSeekType(String field) {
      this(field, EQUALS, 1);
    }

    public BooleanSeekType(String field, int i_type) {
      this(field, i_type, 1);
    }

    public BooleanSeekType(String field, int i_type, int p_count) {
      super(field, i_type, p_count);
    }

    @Override
    public boolean setValue(java.lang.Boolean value) {
      if (!Equals.equals(getValue(), value)) {
        this.value = value;
        return true;
      } else {
        return false;
      }
    }

    @Override
    public boolean hasValue() {
      return value != null;
    }
  }

  public final static class ValueSeekType extends AbstractSeekType<String> {

    public ValueSeekType() {
      super("", PREFORMATTED, 0);
    }

    @Override
    public StringBuilder getSQLSegment() {
      return new StringBuilder(value);
    }

    @Override
    public boolean setValue(String value) {
      if (!Equals.equals(getValue(), value)) {
        this.value = value;
        return true;
      } else {
        return false;
      }
    }
  }

  public final static class RezultatiKlicaSeekType extends AbstractSeekType<List<DataSourceFilters.RezultatiKlicaSeekType.RezultatKlica>> {

    private RezultatKlica vsi = new RezultatKlica("Vsi", "", false);
    private List<RezultatKlica> rezultati = new ArrayList<RezultatKlica>();
    private final DataSourceFilters filters;

    public RezultatiKlicaSeekType(String field, DataSourceFilters filters) {
      super(field, PREFORMATTED, 0);
      this.filters = filters;
      addRezultat(vsi);
    }

    @Override
    public StringBuilder getSQLSegment() {
      StringBuilder sbValues = new StringBuilder();
      boolean addIsnull = false;
      if (value != null) {
        for (RezultatKlica rezultatKlica : value) {
          if (rezultatKlica.isChecked()) {
            final String rezultatValue = rezultatKlica.getValue();
            if (rezultatValue != null && rezultatValue.toLowerCase().equals("nullvalue")) {
              addIsnull = true;
            } else {
              sbValues.append(sbValues.length() > 0 ? ", " : " ").append(" '").append(rezultatValue).append("' ");
            }
          }
        }
      }
      if (sbValues.length() > 0) {
        return new StringBuilder("(" + (addIsnull ? (" " + field + " is null " + " OR ") : "") + field + " IN (" + sbValues.toString() + " ))");
      } else {
        return new StringBuilder(" " + field + " is null ");
      }
    }

    @Override
    public boolean setValue(List<RezultatKlica> value) {
      if (value != null && value.isEmpty()) {
        value = null;
      }
      this.value = value;
      return true;

    }

    public void addRezultat(RezultatKlica rezultatKlica) {
      if (!rezultati.contains(rezultatKlica)) {
        rezultati.add(rezultatKlica);
      } else {
        for (RezultatKlica rk : rezultati) {
          if (rk.equals(rezultatKlica)) {
            rk.setChecked(rezultatKlica.isChecked());
            break;
          }
        }
      }
    }

    public List<RezultatKlica> getRezultati() {
      return rezultati;
    }

    public void reload() {
      boolean setNull = false;
      for (RezultatKlica rezultatKlica : rezultati) {
        if (rezultatKlica.equals(vsi)) {
          if (rezultatKlica.isChecked()) {
            setNull = true;
          }
          break;
        }
      }
      setNull = setNull || rezultati.isEmpty();

      filters.setSeekValue(this, setNull ? null : rezultati);
    }

    public RezultatKlica getRezultatVsi() {
      return vsi;
    }

    public static class RezultatKlica {

      private String opis;
      private String value;
      private boolean isChecked;

      public RezultatKlica(String opis, String value, boolean isChecked) {
        this.opis = opis;
        this.value = value;
        this.isChecked = isChecked;
      }

      public String getOpis() {
        return opis;
      }

      public String getValue() {
        return value;
      }

      public boolean isChecked() {
        return isChecked;
      }

      public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
      }

      @Override
      public boolean equals(Object obj) {
        if (obj == null) {
          return false;
        }

        final RezultatKlica other = (RezultatKlica) obj;
        if ((this.opis == null) ? (other.opis != null) : !this.opis.equals(other.opis)) {
          return false;
        }
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
          return false;
        }

        return true;
      }

      @Override
      public int hashCode() {
        int hash = 7;
        hash = hash * (opis != null ? opis.hashCode() : 1) + (value != null ? value.hashCode() : 0);
        return hash;
      }
    }
  }

  public final static class RezultatExistsSeekType extends AbstractSeekType<List<DataSourceFilters.RezultatExistsSeekType.RezultatExistsValue>> {

    private RezultatExistsValue vsi = new RezultatExistsValue("Vsi", "", false, false);
    private List<RezultatExistsValue> rezultati = new ArrayList<RezultatExistsValue>();
    private final DataSourceFilters filters;
    private final String select;
    private final String replace;

    public RezultatExistsSeekType(String select, String replace, String field, DataSourceFilters filters) {
      super(field, PREFORMATTED, 0);
      this.filters = filters;
      this.select = select;
      this.replace = replace;
      addRezultat(vsi);
    }

    @Override
    public StringBuilder getSQLSegment() {
      //ce je izpolnjeno "vsi", potem sploh ne pride do sem
      StringBuilder sbValues = new StringBuilder();
      boolean addIsNull = false;
      boolean addIsNotNull = false;
      if (value != null) {
        for (RezultatExistsValue rezultatKlica : value) {
          if (rezultatKlica.isChecked()) {
            final String rezultatValue = rezultatKlica.getValue();
            if (rezultatValue != null && rezultatValue.toLowerCase().equals("nullvalue")) {
              addIsNull = true;
            } else if (rezultatValue != null && rezultatValue.toLowerCase().equals("notnullvalue")) {
              addIsNotNull = true;
            } else {
              sbValues.append(sbValues.length() > 0 ? ", " : " ").append(rezultatKlica.isAsString() ? " '" : "").append(rezultatValue).append(rezultatKlica.isAsString() ? "' " : "");
            }
          }
        }
      }
      if (sbValues.length() > 0) {
        StringBuilder sbResult = new StringBuilder(200);
        sbResult.append(" ( ").append("\n");
        sbResult.append((addIsNull ? (" " + field + " is null " + " OR ") : "")).append("\n");
        sbResult.append((addIsNotNull ? (" " + field + " is NOT null " + " OR ") : "")).append("\n");
        sbResult.append(" EXISTS ( ").append("\n").append(select.replaceAll(replace, " IN (" + sbValues.toString() + " )")).append(" ) ").append("\n");
        sbResult.append(" ) \n");
        return sbResult;
      } else {
        return new StringBuilder(" " + field + " is null ");
      }
    }

    @Override
    public boolean setValue(List<RezultatExistsValue> value) {
      if (value != null && value.isEmpty()) {
        value = null;
      }
      this.value = value;
      return true;

    }

    public void addRezultat(RezultatExistsValue rezultatKlica) {
      if (!rezultati.contains(rezultatKlica)) {
        rezultati.add(rezultatKlica);
      } else {
        for (RezultatExistsValue rk : rezultati) {
          if (rk.equals(rezultatKlica)) {
            rk.setChecked(rezultatKlica.isChecked());
            break;
          }
        }
      }
    }

    public List<RezultatExistsValue> getRezultati() {
      return rezultati;
    }

    public void reload() {
      boolean setNull = false;
      for (RezultatExistsValue rezultatKlica : rezultati) {
        if (rezultatKlica.equals(vsi)) {
          if (rezultatKlica.isChecked()) {
            setNull = true;
          }
          break;
        }
      }
      setNull = setNull || rezultati.isEmpty();

      filters.setSeekValue(this, setNull ? null : rezultati);
    }

    public RezultatExistsValue getRezultatVsi() {
      return vsi;
    }

    public static class RezultatExistsValue {

      private String opis;
      private String value;
      private boolean isChecked;
      private boolean isAsString;

      public RezultatExistsValue(String opis, String value, boolean isChecked, boolean isAsString) {
        this.opis = opis;
        this.value = value;
        this.isChecked = isChecked;
        this.isAsString = isAsString;
      }

      public String getOpis() {
        return opis;
      }

      public String getValue() {
        return value;
      }

      public boolean isChecked() {
        return isChecked;
      }

      public boolean isAsString() {
        return isAsString;
      }

      public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
      }

      @Override
      public boolean equals(Object obj) {
        if (obj == null) {
          return false;
        }

        final RezultatExistsValue other = (RezultatExistsValue) obj;
        if ((this.opis == null) ? (other.opis != null) : !this.opis.equals(other.opis)) {
          return false;
        }
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
          return false;
        }

        return true;
      }

      @Override
      public int hashCode() {
        int hash = 7;
        hash = hash * (opis != null ? opis.hashCode() : 1) + (value != null ? value.hashCode() : 0);
        return hash;
      }
    }
  }

  public final static class CheckBoxSeekType extends AbstractSeekType<List<DataSourceFilters.CheckBoxSeekType.CheckBoxValue>> {

    private CheckBoxValue vsi = new CheckBoxValue("Vsi", "", true);
    private List<CheckBoxValue> rezultati = new ArrayList<CheckBoxValue>();
    private final DataSourceFilters filters;
    private boolean grouped = false;
    private String comparator;

    public CheckBoxSeekType(DataSourceFilters filters) {
      this("", "AND", filters);
    }

    public CheckBoxSeekType(String field, String operator, DataSourceFilters filters) {
      super(field, PREFORMATTED, 0);
      this.filters = filters;
      this.comparator = operator==null?"AND":operator;
      addRezultat(vsi);
    }

    @Override
    public StringBuilder getSQLSegment() {
      //ce je izpolnjeno "vsi", potem sploh ne pride do sem
      StringBuilder sbValues = new StringBuilder();
      if (value != null) {
        for (CheckBoxValue rezultatKlica : value) {
          if (rezultatKlica.isChecked()) {
            final String rezultatValue = rezultatKlica.getValue();

            if (compareAsList()) {
              sbValues.append(sbValues.length() > 0 ? ", " : " ").append(rezultatValue);
            } else {
              sbValues.append(sbValues.length() > 0 ? " "+comparator+" " : " ").append("(").append(rezultatValue).append(")").append("\n");
            }

          }
        }
      }
      
      if (sbValues.length()>0) {
        sbValues.insert(0, "(").append(")");
      }
      
      if (compareAsList()) {
        sbValues.insert(0, field+" IN ").append("\n");
      }
      return sbValues;

    }

    private boolean compareAsList() {
      return comparator.equalsIgnoreCase("IN");
    }

    @Override
    public boolean setValue(List<CheckBoxValue> value) {
      if (value != null && value.isEmpty()) {
        value = null;
      }
      this.value = value;
      return true;

    }

    public void addRezultat(CheckBoxValue rezultatKlica) {
      if (!rezultati.contains(rezultatKlica)) {
        rezultati.add(rezultatKlica);
      } else {
        for (CheckBoxValue rk : rezultati) {
          if (rk.equals(rezultatKlica)) {
            rk.setChecked(rezultatKlica.isChecked());
            break;
          }
        }
      }
    }

    public List<CheckBoxValue> getRezultati() {
      return rezultati;
    }

    public void reload() {
      boolean setNull = false;
      for (CheckBoxValue rezultatKlica : rezultati) {
        if (rezultatKlica.equals(vsi)) {
          if (rezultatKlica.isChecked()) {
            setNull = true;
          }
          break;
        }
      }
      setNull = setNull || rezultati.isEmpty();

      filters.setSeekValue(this, setNull ? null : rezultati);
    }

    public CheckBoxValue getRezultatVsi() {
      return vsi;
    }

    /**
     * Get the value of grouped
     *
     * @return the value of grouped
     */
    public boolean isGrouped() {
      return grouped;
    }

    /**
     * Set the value of grouped
     *
     * @param grouped new value of grouped
     */
    public void setGrouped(boolean grouped) {
      this.grouped = grouped;
    }

    public static class CheckBoxValue {

      private String opis;
      private String value;
      private boolean isChecked;

      public CheckBoxValue(String opis, String value, boolean isChecked) {
        this.opis = opis;
        this.value = value;
        this.isChecked = isChecked;
      }

      public String getOpis() {
        return opis;
      }

      public String getValue() {
        return value;
      }

      public boolean isChecked() {
        return isChecked;
      }

      public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
      }

      @Override
      public boolean equals(Object obj) {
        if (obj == null) {
          return false;
        }

        final CheckBoxValue other = (CheckBoxValue) obj;
        if ((this.opis == null) ? (other.opis != null) : !this.opis.equals(other.opis)) {
          return false;
        }
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
          return false;
        }

        return true;
      }

      @Override
      public int hashCode() {
        int hash = 7;
        hash = hash * (opis != null ? opis.hashCode() : 1) + (value != null ? value.hashCode() : 0);
        return hash;
      }
    }
  }

  public final static class IntegerSeekType extends AbstractSeekType<java.lang.Integer> {

    private int min_length = 1;

    public IntegerSeekType(String field) {
      this(field, EQUALS, 1);
    }

    public IntegerSeekType(String field, int i_type) {
      this(field, i_type, 1);
    }

    public IntegerSeekType(String field, int i_type, int p_count) {
      super(field, i_type, p_count);
    }

    public void setMinLength(int min_len) {
      this.min_length = min_len;
    }

    @Override
    public boolean setValue(java.lang.Integer value) {
      try {
        String convert = value == null ? null : value.toString();
        if (convert != null & convert.length() < min_length) {
          value = null;
        }
      } catch (Exception ex) {
        //ignore
      }
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
            value.append(value.length() > 0 ? " " + seek.getOperator() + " " : "").append(seek.getSQLSegment());
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
  protected boolean useSearchButton = false;

  /**
   * Get the value of useSearchButton
   *
   * @return the value of useSearchButton
   */
  public boolean isUseSearchButton() {
    return useSearchButton;
  }

  /**
   * Set the value of useSearchButton
   *
   * @param useSearchButton new value of useSearchButton
   */
  public void setUseSearchButton(boolean useSearchButton) {
    this.useSearchButton = useSearchButton;
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
  public <E> void setSeekValue(AbstractSeekType<E> seek, E seek_value, boolean setParameters) {
    if (!seek_types.contains(seek)) {
      seek_types.add(seek);
    }
    if (seek.setValue(seek_value) && setParameters) {
      setParameters(true);
    }
  }

  /**
   * Setter for property itype.
   * @param itype New value of property itype.
   */
  public <E> void setSeekValue(AbstractSeekType<E> seek, E seek_value) {
    setSeekValue(seek, seek_value, true);
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

  public static class SifrantSeekType extends AbstractSeekType<String> implements ValuesList {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private FutureTask<DbComboBoxModel> model;
    private AbstractSeekType<String> seekType;
    private String dataBase;
    private List<String> allowedValues;
    private List<String> excludedValues;
    private DbSifrantModel sifrantModel;

    public SifrantSeekType(String field, final String sifrantSkupina, final String sifrantOpis) {
      this(field, sifrantSkupina, sifrantOpis, null);
    }

    public SifrantSeekType(String field, final String sifrantSkupina, final String sifrantOpis, final String textNotDefined) {
      this(new SeekType(field, UPPER_EQUALS, 1), sifrantSkupina, sifrantOpis, textNotDefined);
    }

    public SifrantSeekType(AbstractSeekType<String> seekType, final String sifrantSkupina, final String sifrantOpis, final String textNotDefined) {
      this(seekType, sifrantSkupina, sifrantOpis, textNotDefined, null, null);
    }

    public SifrantSeekType(AbstractSeekType<String> seekType, final String sifrantSkupina, final String sifrantOpis, final String textNotDefined, final List<String> allowedValues, final List<String> excludedValues) {
      this(seekType, sifrantSkupina, sifrantOpis, textNotDefined, "", allowedValues, excludedValues);
    }

    public SifrantSeekType(AbstractSeekType<String> seekType, final String sifrantSkupina, final String sifrantOpis, final String textNotDefined, final String dataBase, final List<String> allowedValues, final List<String> excludedValues) {
      super("", PREFORMATTED, 1);

      this.seekType = seekType;
      this.def_i_type = 1;
      this.sifrantSkupina = sifrantSkupina;
      this.sifrantOpis = sifrantOpis;
      this.textNotDefined = textNotDefined == null ? "Ni doloèen" : textNotDefined;

      this.model = null;
      this.dataBase = dataBase;
      this.allowedValues = allowedValues;
      this.excludedValues = excludedValues;
      /*this.model = new FutureTask<DbComboBoxModel>(new Callable<DbComboBoxModel>() {

      @Override
      public DbComboBoxModel call() {
      DbSifrantModel result = null;
      try {
      result = new DbSifrantModel(textNotDefined, dataBase, allowedValues, excludedValues);
      result.setSifrantOpis(sifrantOpis);
      result.setSifrantSkupina(sifrantSkupina);
      } catch (SQLException ex) {
      Logger.getLogger(DataSourceFilters.class.getName()).log(Level.SEVERE, null, ex);
      }
      return result;
      }
      });
       */
    }

    public SifrantSeekType(String field, Callable<DbComboBoxModel> callable) {
      this(field, new FutureTask<DbComboBoxModel>(callable));
    }

    public SifrantSeekType(String field, FutureTask<DbComboBoxModel> model) {
      this(new SeekType(field, UPPER_EQUALS, 1), model);
    }

    public SifrantSeekType(AbstractSeekType<String> seekType, FutureTask<DbComboBoxModel> model) {
      super("", PREFORMATTED, 1);
      this.seekType = seekType;
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
        if (model == null) {
          if (sifrantModel == null) {
            try {
              sifrantModel = new DbSifrantModel(textNotDefined, dataBase, allowedValues, excludedValues);
              DbDataSource dataSource = sifrantModel.getDataSource();
              DataSourceEvent.suspend(dataSource);
              DataSourceEvent.cancel(dataSource);
              sifrantModel.setSifrantOpis(sifrantOpis);
              sifrantModel.setSifrantSkupina(sifrantSkupina);
              DataSourceEvent.resume(dataSource);
              dataSource.reload(true);
            } catch (SQLException ex) {
              Logger.getLogger(DataSourceFilters.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
          result = sifrantModel;
        } else {
          executorService.execute(model);
          result = model.get();
        }

      } catch (InterruptedException ex) {
        Logger.getLogger(DataSourceFilters.class.getName()).log(Level.SEVERE, null, ex);
      } catch (ExecutionException ex) {
        Logger.getLogger(DataSourceFilters.class.getName()).log(Level.SEVERE, null, ex);
      }
      return result;
    }

    @Override
    public boolean setValue(String value) {
      boolean result = seekType.setValue(value);

      this.value = seekType.value;
      return result;
    }

    @Override
    public boolean hasValue() {
      return seekType.hasValue();
    }

    @Override
    public StringBuilder getSQLSegment() {
      return seekType.getSQLSegment();
    }

    @Override
    public void setName(String name) {
      seekType.setName(name);
      super.setName(name);
    }

    @Override
    public void setOperator(String operator) {
      seekType.setOperator(operator);
      super.setOperator(operator);
    }

    @Override
    public boolean setSeekType(int i) {
      seekType.setSeekType(i);
      return super.setSeekType(i);
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

    @Override
    public List getValues() {
      if (seekType instanceof ValuesList) {
        return ((ValuesList) seekType).getValues();
      } else {
        List result = new ArrayList();
        result.add(getValue());

        return result;
      }
    }
  }

  public static class SeekTypeWrapper<T, V extends AbstractSeekType<T>> extends AbstractSeekType<T> {

    protected V seekType;
    protected String pattern;

    public SeekTypeWrapper(String pattern, V seekType) {
      super("", PREFORMATTED, 1);
      if (seekType == null) {
        throw new NullPointerException("Wrapped AbstractSeekType can't be null");
      }
      this.pattern = pattern;
      this.seekType = seekType;

      this.i_type = seekType.getSeekType();
      this.p_count = seekType.p_count;
    }

    @Override
    public boolean setValue(T value) {

      boolean result = seekType.setValue(value);

      this.value = seekType.value;
      return result;
    }

    @Override
    public void setLayout(SeekLayout layout) {
      seekType.setLayout(layout);
      super.setLayout(layout);
    }

    @Override
    public boolean hasValue() {
      return seekType.hasValue();
    }

    @Override
    public void setName(String name) {
      seekType.setName(name);
      super.setName(name);
    }

    @Override
    public void setOperator(String operator) {
      seekType.setOperator(operator);
      super.setOperator(operator);
    }

    @Override
    public boolean setSeekType(int i) {
      seekType.setSeekType(i);
      return super.setSeekType(i);
    }

    @Override
    public int getSeekType() {
      return super.getSeekType();
    }

    /**
     * Get the value of pattern
     *
     * @return the value of pattern
     */
    public String getPattern() {
      return pattern;
    }

    /**
     * Set the value of pattern
     *
     * @param pattern new value of pattern
     */
    public void setPattern(String pattern) {
      this.pattern = pattern;
    }

    @Override
    public StringBuilder getSQLSegment() {
      if (pattern == null) {
        return seekType.getSQLSegment();
      } else {
        return new StringBuilder(MessageFormat.format(pattern, seekType.getSQLSegment()));
      }

    }

    public V getWrapperFor() {
      return seekType;
    }
  }

  public static class SeekFilterWrapper<T extends AbstractSeekType> extends AbstractSeekType<java.util.List<T>> implements ValuesList {

    private DataSourceFilters filter = new DataSourceFilters("");
    protected List<T> seekTypes = new ArrayList<T>();
    protected String pattern;

    public SeekFilterWrapper(final DataSourceFilters owner, String pattern, T... seekTypes) {
      super("", PREFORMATTED, 1);
      this.pattern = pattern;
      this.seekTypes.addAll(Arrays.asList(seekTypes));


      filter.addPropertyChangeListener("query", new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          owner.setSeekValue(SeekFilterWrapper.this, null);
        }
      });
    }

    public DataSourceFilters getFilter() {
      return filter;
    }

    @Override
    public boolean hasValue() {
      boolean result = false;
      for (AbstractSeekType abstractSeekType : filter.seek_types) {
        result = result || abstractSeekType.hasValue();
      }
      return result;
    }

    @Override
    public StringBuilder getSQLSegment() {
      return new StringBuilder(MessageFormat.format(pattern, filter.getValue()));
    }

    @Override
    public boolean setValue(List<T> value) {
      if (value != null) {
        this.seekTypes.clear();
        this.seekTypes.addAll(value);
      }
      return true;
    }

    @Override
    public List getValues() {
      return filter.getParameters();
    }
  }

  public abstract static class InnerJoinSeekType<T> extends AbstractSeekType<T> {

    protected static final String pattern = " {0} {1}\n " + " ON ( {2} {3} )";
    protected String joinType;
    protected String joinTable;
    protected String joinCondition;

    public InnerJoinSeekType(String joinTable, String joinCondition, String field) {
      this("INNER JOIN", joinTable, joinCondition, field);
    }

    public InnerJoinSeekType(String joinType, String joinTable, String joinCondition, String field) {
      super(field, EQUALS, 1);
      this.joinType = joinType;
      this.joinTable = joinTable;
      this.joinCondition = joinCondition;
    }

    @Override
    public StringBuilder getSQLSegment() {
      return new StringBuilder(MessageFormat.format(pattern, getJoinType(), getJoinTable(), getJoinCondition(), getJoinSearchCondition()));
    }

    public CharSequence getJoinSearchCondition() {
      return super.getSQLSegment();
    }

    /**
     * Get the value of joinType
     *
     * @return the value of joinType
     */
    public String getJoinType() {
      return joinType;
    }

    /**
     * Get the value of joinCondition
     *
     * @return the value of joinCondition
     */
    public String getJoinCondition() {
      return joinCondition;
    }

    /**
     * Get the value of joinTable
     *
     * @return the value of joinTable
     */
    public String getJoinTable() {
      return joinTable;
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

  public static class TelefonSeekType<V extends AbstractSeekType<String>> extends AbstractSeekType<String> {

    DataSourceFiltersSeek<V> omrezna;
    DataSourceFiltersSeek<V> telefonska;

    public TelefonSeekType(DataSourceFiltersSeek<V> omrezna, DataSourceFiltersSeek<V> telefonska) {
      super("", telefonska.seek.getSeekType(), 1);

      this.omrezna = omrezna;
      this.telefonska = telefonska;
    }

    @Override
    public boolean setValue(String value) {
      if (value == null) {
        value = "";
      } else {
        value = value.trim();
      }

      if (!Equals.equals(this.value, value)) {
        if (value.length() == 0) {
          omrezna.filter.setSeekValue(omrezna.seek, "");
          telefonska.filter.setSeekValue(telefonska.seek, "");
        } else {
          StringBuilder sb_omrezna = new StringBuilder(10);
          StringBuilder sb_telefonska = new StringBuilder(10);

          int pos = 0;

          while ((pos < value.length()) && Character.isDigit(value.charAt(pos))) {
            sb_omrezna.append(value.charAt(pos++));
          }
          while (pos < value.length()) {
            if (Character.isDigit(value.charAt(pos))) {
              sb_telefonska.append(value.charAt(pos));
            }
            pos++;
          }

          if ((sb_omrezna.length() > 0) && (sb_telefonska.length() > 0)) {
            omrezna.filter.setSeekValue(omrezna.seek, sb_omrezna.toString());
            telefonska.filter.setSeekValue(telefonska.seek, sb_telefonska.toString());
          } else {
            Telefon telefon = null;

            if (value.startsWith("0") || value.startsWith("+")) {
              telefon = TelefonskeStevilke.getTelefon(value);
            }

            if (telefon != null) {
              omrezna.filter.setSeekValue(omrezna.seek, telefon.getOmrezna());
              telefonska.filter.setSeekValue(telefonska.seek, telefon.getTelefonska());
            } else {
              omrezna.filter.setSeekValue(omrezna.seek, "");
              telefonska.filter.setSeekValue(telefonska.seek, value);
            }
          }
        }

        this.value = value;

        return true;
      } else {
        return false;
      }
    }

    @Override
    public boolean setSeekType(int i_type) {
      if (telefonska.seek.getSeekType() != i_type) {
        telefonska.filter.setSeekType(telefonska.seek, i_type);
        return true;
      } else {
        return false;
      }
    }

    @Override
    public int getSeekType() {
      return telefonska.seek.getSeekType();
    }

    @Override
    public boolean hasValue() {
      return false;
    }
  }

  public static class VariousValuesSeekType extends InnerJoinSeekType<String> {

    private int min_length = 1;

    public VariousValuesSeekType(String tableName, String valueIdField, String alias, String joinON, String valueType, int seekType) {
      super(tableName + " " + alias, alias + "." + valueIdField + " = " + joinON + "\n AND ", alias + "." + valueType);
      setSeekType(seekType);
    }
    private boolean useAlways = false;

    /**
     * Get the value of useAlways
     *
     * @return the value of useAlways
     */
    public boolean isUseAlways() {
      return useAlways;
    }

    /**
     * Set the value of useAlways
     *
     * @param useAlways new value of useAlways
     */
    public void setUseAlways(boolean useAlways) {
      this.useAlways = useAlways;
    }

    @Override
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

    @Override
    public String getJoinType() {
      return (value != null && value.length() > 0) ? "INNER JOIN" : "LEFT OUTER JOIN";
    }

    @Override
    public boolean hasValue() {
      return isUseAlways() || (value != null && value.length() > 0);
    }
    private boolean caseSensitive = false;

    /**
     * Getter for property caseSensitive.
     * @return Value of property caseSensitive.
     */
    @Override
    public boolean isCaseSensitive() {
      return this.caseSensitive;
    }

    /**
     * Setter for property caseSensitive.
     * @param caseSensitive New value of property caseSensitive.
     */
    @Override
    public void setCaseSensitive(boolean caseSensitive) {
      this.caseSensitive = caseSensitive;
    }
  }
}
