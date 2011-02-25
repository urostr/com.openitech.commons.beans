/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.value.events;

import com.openitech.sql.util.SqlUtilities;
import com.openitech.text.CaseInsensitiveString;
import com.openitech.util.Equals;
import com.openitech.value.VariousValue;
import com.openitech.value.fields.Field;
import com.openitech.value.fields.Field.LookupType;
import com.openitech.value.fields.FieldValue;
import com.openitech.value.fields.ValueType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class Event {

  public static final Field EVENT_SOURCE = new Field("EVENT_SOURCE", java.sql.Types.INTEGER);
  public static final Field EVENT_DATE = new Field("EVENT_DATE", java.sql.Types.DATE);

  public Event(int sifrant, String sifra) {
    this(sifrant, sifra, Integer.MIN_VALUE);
  }

  public Event(int sifrant, String sifra, int eventSource) {
    this.sifrant = sifrant;
    this.sifra = sifra;
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

  private int sifrant;

  /**
   * Get the value of sifrant
   *
   * @return the value of sifrant
   */
  public int getSifrant() {
    return sifrant;
  }

  /**
   * Set the value of sifrant
   *
   * @param sifrant new value of sifrant
   */
  public void setSifrant(int sifrant) {
    this.sifrant = sifrant;
  }
  private String sifra;

  /**
   * Get the value of sifra
   *
   * @return the value of sifra
   */
  public String getSifra() {
    return sifra;
  }

  /**
   * Set the value of sifra
   *
   * @param sifra new value of sifra
   */
  public void setSifra(String sifra) {
    this.sifra = sifra;
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
  protected boolean versioned = false;

  /**
   * Get the value of versioned
   *
   * @return the value of versioned
   */
  public boolean isVersioned() {
    return versioned;
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

  /**
   * Set the value of versioned
   *
   * @param versioned new value of versioned
   */
  public void setVersioned(boolean versioned) {
    this.versioned = versioned;
  }
  java.util.Map<Field, java.util.List<FieldValue>> eventValues = new java.util.LinkedHashMap<Field, java.util.List<FieldValue>>();

  public void addValue(FieldValue value) {
    getFieldValues(value).add(value);
  }

  public void addValues(ResultSet rs, final java.util.Map<CaseInsensitiveString, Field> fieldsMap, String... fields) throws SQLException {
    for (String field : fields) {
      addValue(FieldValue.createFieldValue(rs, field, 1, field, fieldsMap));
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

  public void removeAll(FieldValue value) {
    eventValues.remove(value);
  }

  public Map<Field, List<FieldValue>> getEventValues() {
    return eventValues;
  }

  public void setEventValues(Map<Field, List<FieldValue>> eventValues) {
    this.eventValues = eventValues;
  }

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
  protected List<AfterUpdateEvent> afterUpdateEvent;

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
    Map<Field, List<FieldValue>> a = getEventValues();
    Map<Field, List<FieldValue>> b = other.getEventValues();
    if ((a.size() == b.size()) && a.keySet().containsAll(b.keySet())) {
      boolean result = true;
      Iterator<Map.Entry<Field, List<FieldValue>>> aiterator = a.entrySet().iterator();

      while (aiterator.hasNext() && result) {
        Map.Entry<Field, List<FieldValue>> anext = aiterator.next();
        result = compareList(anext.getValue(), b.get(anext.getKey()));
      }
      result = result && Equals.equals(getOpomba(), other.getOpomba());
      return result;
    } else {
      return false;

    }
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
    if (getClass() != obj.getClass()) {
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
    EventPK eventPK = null;
    try {
      eventPK = getEventPK(this.preparedFields==null?SqlUtilities.getInstance().getPreparedFields():preparedFields);
    } catch (SQLException ex) {
      Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
    }
    return eventPK;
  }

  public EventPK getEventPK(final Map<CaseInsensitiveString, Field> preparedFields) {
    EventPK eventPK = null;
    if (getId() != null) {
      try {
        eventPK = new EventPK(id, sifrant, sifra);
        eventPK.setEventOperation(operation);
        for (Field field : eventValues.keySet()) {
          List<FieldValue> fieldValues = eventValues.get(field);

          for (int i = 0; i < fieldValues.size(); i++) {
            FieldValue fieldValue = fieldValues.get(i);
            String fieldNameWithIndex = field.getName();
            int fieldValueIndex = field.getFieldIndex();
            LookupType lookupType = field.getLookupType();
            CaseInsensitiveString fieldName_ci = new CaseInsensitiveString(field.getNonIndexedField().getName());
            int idPolja = preparedFields.containsKey(fieldName_ci) ? preparedFields.get(fieldName_ci).getIdPolja() : field.getIdPolja();

            if (getPrimaryKey() != null && getPrimaryKey().length > 0) {
              FieldValue fieldValuePK = new FieldValue(idPolja, fieldNameWithIndex, field.getType(), fieldValueIndex, VariousValue.newVariousValue(ValueType.getType(fieldValue.getType()), fieldValue.getValue()));
              fieldValuePK.setLookupType(lookupType);
              for (Field field1 : getPrimaryKey()) {
                if (field1.equals(fieldValuePK)) {
                  eventPK.addPrimaryKeyField(fieldValuePK);
                  break;
                }
              }
            }
          }
        }
      } catch (SQLException ex) {
        Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
      }
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

  public static enum EventOperation {

    UPDATE,
    DELETE,
    IGNORE
  }
}
