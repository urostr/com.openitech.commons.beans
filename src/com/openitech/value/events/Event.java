/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.value.events;

import com.openitech.sql.util.SqlUtilities;
import com.openitech.text.CaseInsensitiveString;
import com.openitech.util.Equals;
import com.openitech.value.fields.Field;
import com.openitech.value.fields.FieldValue;
import com.openitech.value.fields.ValueType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class Event extends EventType implements Cloneable {

  public static final Field EVENT_SOURCE = new Field("EVENT_SOURCE", java.sql.Types.INTEGER);
  public static final Field EVENT_DATE = new Field("EVENT_DATE", java.sql.Types.DATE);

  public Event(int sifrant, String sifra) {
    this(sifrant, sifra, Integer.MIN_VALUE);
  }

  public Event(int sifrant, String sifra, int eventSource) {
    super(sifrant, sifra);
    this.eventSource = eventSource;
  }
  private Long id;

  /**
   * Get the value of id
   *
   * @return the value of id
   */
  public Long getId() {
    return id;
  }

  /**
   * Set the value of id
   *
   * @param id new value of id
   */
  public void setId(Long id) {
    this.id = id;
  }
  private Date datum = java.util.Calendar.getInstance().getTime();

  /**
   * Get the value of datum
   *
   * @return the value of datum
   */
  public Date getDatum() {
    return datum;
  }

  /**
   * Set the value of datum
   *
   * @param datum new value of datum
   */
  public void setDatum(Date datum) {
    this.datum = datum;
  }
  private Long veljavnost;

  /**
   * Get the value of veljavnost
   *
   * @return the value of veljavnost
   */
  public Long getVeljavnost() {
    return veljavnost;
  }

  /**
   * Set the value of veljavnost
   *
   * @param veljavnost new value of veljavnost
   */
  public void setVeljavnost(Long veljavnost) {
    this.veljavnost = veljavnost;
  }
  private String opomba;

  /**
   * Get the value of opomba
   *
   * @return the value of opomba
   */
  public String getOpomba() {
    return opomba;
  }

  /**
   * Set the value of opomba
   *
   * @param opomba new value of opomba
   */
  public void setOpomba(String opomba) {
    this.opomba = opomba;
  }
  private int eventSource = Integer.MIN_VALUE;

  /**
   * Get the value of eventSource
   *
   * @return the value of eventSource
   */
  public int getEventSource() {
    return eventSource;
  }

  /**
   * Set the value of eventSource
   *
   * @param eventSource new value of eventSource
   */
  public void setEventSource(int eventSource) {
    this.eventSource = eventSource;
  }
  protected Map<CaseInsensitiveString, Field> preparedFields;

  /**
   * Get the value of preparedFields
   *
   * @return the value of preparedFields
   */
  public Map<CaseInsensitiveString, Field> getPreparedFields() {
    return preparedFields;
  }

  /**
   * Set the value of preparedFields
   *
   * @param preparedFields new value of preparedFields
   */
  public void setPreparedFields(Map<CaseInsensitiveString, Field> preparedFields) {
    this.preparedFields = preparedFields;
    for (Event event : getChildren()) {
      event.setPreparedFields(preparedFields);
    }
  }
  protected boolean versioned = false;

  /**
   * Get the value of versioned
   *
   * @return the value of versioned
   */
  public boolean isVersioned() {
    return versioned;
  }

  /**
   * Set the value of versioned
   *
   * @param versioned new value of versioned
   */
  public void setVersioned(boolean versioned) {
    this.versioned = versioned;
  }
  java.util.Map<Field, java.util.List<FieldValue>> eventValues = new java.util.LinkedHashMap<Field, java.util.List<FieldValue>>();
  List<FieldValue> values = new LinkedList<FieldValue>();

  public List<FieldValue> getValues() {
    return values;
  }

  public void addValue(FieldValue value) {
    getFieldValues(value).add(value);
    values.add(value);
  }

  public void addValues(ResultSet rs, final java.util.Map<CaseInsensitiveString, Field> fieldsMap, String... fields) throws SQLException {
    for (String field : fields) {
      addValue(FieldValue.createFieldValue(rs, field, 1, field, fieldsMap));
    }
  }

  public void addValues(List<FieldValue> fieldValues) {
    for (FieldValue fieldValue : fieldValues) {
      addValue(fieldValue);
    }
  }

  public void addValues(ResultSet rs, final java.util.Map<CaseInsensitiveString, Field> fieldsMap, String[][] fields) throws SQLException {
    for (String[] field : fields) {
      if (field.length > 0) {
        final String fieldName = field[0];
        final String columnName = field.length > 1 ? field[1] : fieldName;
        FieldValue fv = FieldValue.createFieldValue(rs, fieldName, 1, columnName, fieldsMap);


        addValue(fv);
      }
    }
  }

  /**
   * Removes the first occurrence of the specified element from this list,
   * if it is present (optional operation).  If this list does not contain
   * the element, it is unchanged.  More formally, removes the element with
   * the lowest index <tt>i</tt> such that
   * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
   * (if such an element exists).  Returns <tt>true</tt> if this list
   * contained the specified element (or equivalently, if this list changed
   * as a result of the call).
   *
   * @param o element to be removed from this list, if present
   * @return <tt>true</tt> if this list contained the specified element
   * @throws ClassCastException if the type of the specified element
   *         is incompatible with this list (optional)
   * @throws NullPointerException if the specified element is null and this
   *         list does not permit null elements (optional)
   * @throws UnsupportedOperationException if the <tt>remove</tt> operation
   *         is not supported by this list
   */
  public boolean removeValue(FieldValue fieldValue) {
    getFieldValues(fieldValue).remove(fieldValue);
    eventValues.remove(fieldValue);
    return values.remove(fieldValue);
  }

  /**
   * @deprecated use getValues instead.
   *
   **/
  @Deprecated
  public Map<Field, List<FieldValue>> getEventValues() {
    return eventValues;
  }

  public void setEventValues(Map<Field, List<FieldValue>> eventValues) {
    this.eventValues = eventValues;
    for (Field field : eventValues.keySet()) {
      List<FieldValue> lfv = eventValues.get(field);
      for (FieldValue fieldValue : lfv) {
        values.add(fieldValue);
      }
    }
  }

  /**
   * @deprecated use getValues instead.
   *
   **/
  @Deprecated
  public List<FieldValue> getFieldValues() {
    List<FieldValue> result = new java.util.ArrayList<FieldValue>();

    for (java.util.Map.Entry<Field, java.util.List<FieldValue>> entry : eventValues.entrySet()) {
      result.addAll(entry.getValue());
    }

    return Collections.unmodifiableList(result);
  }

  private List<FieldValue> getFieldValues(FieldValue value) {
    List<FieldValue> fieldValues;
    if (eventValues.containsKey(value)) {
      fieldValues = eventValues.get(value);
    } else {
      fieldValues = new java.util.ArrayList<FieldValue>();
      eventValues.put(value, fieldValues);
    }
    return fieldValues;
  }
  private Field[] primaryKey;

  /**
   * Get the value of primaryKey
   *
   * @return the value of primaryKey
   */
  public Field[] getPrimaryKey() {
    return primaryKey;
  }

  /**
   * Set the value of primaryKey
   *
   * @param primaryKey new value of primaryKey
   */
  public void setPrimaryKey(Field... primaryKey) {
    this.primaryKey = primaryKey;
  }

  public void setPrimaryKey(final java.util.Map<CaseInsensitiveString, Field> fieldsMap, String... primaryKeys) {
    List<Field> fields = new ArrayList<Field>(primaryKeys.length);
    for (String pk : primaryKeys) {
      final Field field = FieldValue.getField(pk, 1, fieldsMap);
      fields.add(new Field(pk, field.getType(), field.getFieldIndex()));
    }
    this.primaryKey = fields.toArray(new Field[fields.size()]);
  }
  private EventOperation operation = EventOperation.UPDATE;

  /**
   * Get the value of operation
   *
   * @return the value of operation
   */
  public EventOperation getOperation() {
    return operation;
  }

  /**
   * Set the value of operation
   *
   * @param operation new value of operation
   */
  public void setOperation(EventOperation operation) {
    this.operation = operation;
  }
  private List<Event> children;

  /**
   * Get the value of children
   *
   * 
   * @return the value of children
   */
  public List<Event> getChildren() {
    if (children == null) {
      children = new ArrayList<Event>();
    }
    return children;
  }
  protected List<UpdateEventFields> updateEventFields;

  /**
   * Get the value of updateEventFields
   *
   * @return the value of updateEventFields
   */
  public List<UpdateEventFields> getUpdateEventFields() {
    if (updateEventFields == null) {
      updateEventFields = new ArrayList<UpdateEventFields>();
    }
    return updateEventFields;
  }
  private List<AfterUpdateEvent> afterUpdateEvent;

  /**
   * Get the value of updateEventFields
   *
   * @return the value of updateEventFields
   */
  public List<AfterUpdateEvent> getAfterUpdateEvent() {
    if (afterUpdateEvent == null) {
      afterUpdateEvent = new ArrayList<AfterUpdateEvent>();
    }
    return afterUpdateEvent;
  }

  public boolean equalEventValues(Event other) {

    List<FieldValue> a = getValues();
    List<FieldValue> b = other.getValues();
    boolean result = compareList(a, b);
    result = result && Equals.equals(getOpomba(), other.getOpomba());
    return result;
  }

  private boolean compareList(List<FieldValue> a, List<FieldValue> b) {
    if (a == null && b == null) {
      return true;
    } else if (a != null && b != null) {
      if (a.size() == b.size()) {
        boolean result = true;
        for (int i = 0; i < a.size() && result; i++) {
          result = Equals.equals(a.get(i), b.get(i)) && Equals.equals(a.get(i).getValue(), b.get(i).getValue());
        }

        return result;
      } else {
        return false;

      }
    } else {
      return false;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Event)) {
      return false;
    }
    final Event other = (Event) obj;
    if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
      return false;
    }
    if (this.sifrant != other.sifrant) {
      return false;
    }
    if ((this.sifra == null) ? (other.sifra != null) : !this.sifra.equals(other.sifra)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    if (this.id != null) {
      hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
    }
    hash = 97 * hash + (int) (sifrant ^ (sifrant >>> 32)) + (int) (sifra.hashCode() ^ (sifra.hashCode() >>> 32));
    return hash;
  }

  @Override
  public String toString() {
    return "E:" + sifrant + "-" + sifra + ":" + id + ":" + operation;
  }

  public EventPK getEventPK() {
    long start = System.currentTimeMillis();
    EventPK eventPK = null;
    try {
      eventPK = getEventPK(this.preparedFields == null ? SqlUtilities.getInstance().getPreparedFields() : preparedFields);
    } catch (SQLException ex) {
      Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
    }
    long end = System.currentTimeMillis();
    System.out.println("CAS:getEventPK=" + (end - start));
    return eventPK;
  }

  public EventPK getEventPK(final Map<CaseInsensitiveString, Field> preparedFields) {
    EventPK eventPK = null;
    try {
      eventPK = new EventPK(id, sifrant, sifra);
      eventPK.setEventOperation(operation);
      eventPK.setVersioned(versioned);
      final Field[] eventPrimaryKey = getPrimaryKey();
      if (eventPrimaryKey != null && eventPrimaryKey.length > 0) {
        for (Field pkField : eventPrimaryKey) {
          for (FieldValue fieldValue : values) {
            if (pkField.equals(fieldValue)) {
              if (fieldValue.getIdPolja() == null) {
                CaseInsensitiveString fieldName_ci = new CaseInsensitiveString(fieldValue.getNonIndexedField().getName());
                Integer idPolja = preparedFields.containsKey(fieldName_ci) ? preparedFields.get(fieldName_ci).getIdPolja() : fieldValue.getIdPolja();
                fieldValue.setIdPolja(idPolja);
              }
              if (fieldValue.getValueId() == null) {
                fieldValue.setValueId(SqlUtilities.getInstance().storeValue(ValueType.getType(fieldValue.getType()), fieldValue.getValue()));
              }
              eventPK.addPrimaryKeyField(fieldValue);
              break;
            }
          }
        }
      }
    } catch (SQLException ex) {
      Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
    }
    return eventPK;

  }

  public static Object getValue(Event event, String imePolja, int type) {
    Object result = null;
    if (event != null && event.getEventValues().containsKey(new Field(imePolja, type))) {
      Object value = event.getEventValues().get(new Field(imePolja, type)).get(0).getValue();

      switch (type) {
        case java.sql.Types.INTEGER:
          if (value instanceof Number) {
            result = ((Number) value).intValue();
          } else if (value instanceof String && value != null) {
            result = Integer.parseInt((String) value);
          } else {
            result = (Integer) null;
          }
          break;
        case java.sql.Types.VARCHAR:
          result = (String) value;
          break;
        case java.sql.Types.DATE:
          if (value != null) {
            result = new java.sql.Date(((java.util.Date) value).getTime());
          } else {
            result = (java.sql.Date) null;
          }
          break;
        case java.sql.Types.BIT:
          if (value != null) {
            if (value instanceof Integer) {
              result = ((Integer) value) > 0;
            } else if (value instanceof Boolean) {
              result = (Boolean) value;
            }
          } else {
            result = (Boolean) null;
          }
          break;
      }
    }
    return result;
  }
  private Event versionParent;

  /**
   * Get the value of versionParent
   *
   * @return the value of versionParent
   */
  public Event getVersionParent() {
    return versionParent;
  }

  /**
   * Set the value of versionParent
   *
   * @param versionParent new value of versionParent
   */
  public void setVersionParent(Event versionParent) {
    this.versionParent = versionParent;
  }

  @Override
  public Event clone() {
    Event result = new Event(sifrant, sifra);
    result.setDatum(datum);
    result.setEventSource(eventSource);

    java.util.Map<Field, java.util.List<FieldValue>> eventValuesCopy = new java.util.LinkedHashMap<Field, java.util.List<FieldValue>>();
    for (Field field : this.eventValues.keySet()) {
      List<FieldValue> fieldValues = this.eventValues.get(field);
      List<FieldValue> fieldValuesCopy = new ArrayList<FieldValue>(fieldValues.size());
      for (FieldValue fieldValue : fieldValues) {
        fieldValuesCopy.add(fieldValue.clone());
      }
      eventValuesCopy.put(field.clone(), fieldValuesCopy);
    }

    result.setEventValues(eventValuesCopy);
    result.setId(id);
    result.setOperation(operation);
    result.setOpomba(opomba);
    result.setPreparedFields(preparedFields);
    Field[] primaryKeyCopy = null;
    if (this.primaryKey != null) {
      primaryKeyCopy = new Field[this.primaryKey.length];
      for (int i = 0; i < this.primaryKey.length; i++) {
        Field field = this.primaryKey[i];
        primaryKeyCopy[i] = field.clone();
      }
    }
    result.setPrimaryKey(primaryKeyCopy);
    result.setVeljavnost(veljavnost);
    result.setVersionParent(versionParent);
    result.setVersioned(versioned);
    if (this.updateEventFields != null) {
      List<UpdateEventFields> updateEventFieldsesCopy = new ArrayList<UpdateEventFields>(this.updateEventFields);
      result.updateEventFields = updateEventFieldsesCopy;
    }
    for (Event child : getChildren()) {
      result.addChild(child.clone());
    }

    return result;
  }

  public boolean contains(FieldValue fv) {
    if (fv == null) {
      return false;
    }
    for (FieldValue fieldValue : values) {
      if (fieldValue != null) {
        if (fieldValue.equals(fv)) {
          return true;
        }
      }
    }
    return false;
  }

  public void addChild(Event event) {
    getChildren().add(event);
  }

  public void removeAllChildren() {
    children = new ArrayList<Event>();
  }

  public static enum EventOperation {

    UPDATE {

      @Override
      public boolean isUpdateCache() {
        return !isDisableEventCaching();
      }
    },
    DELETE {

      @Override
      public boolean isUpdateCache() {
        return !isDisableEventCaching();
      }
    },
    IGNORE {

      @Override
      public boolean isUpdateCache() {
        return false;
      }
    };
    private boolean disableEventCaching = false;

    /**
     * Get the value of disableEventCaching
     *
     * @return the value of disableEventCaching
     */
    public boolean isDisableEventCaching() {
      return disableEventCaching;
    }

    /**
     * Set the value of disableEventCaching
     *
     * @param disableEventCaching new value of disableEventCaching
     */
    public void setDisableEventCaching(boolean disableEventCaching) {
      this.disableEventCaching = disableEventCaching;
    }

    public abstract boolean isUpdateCache();
  }
}
