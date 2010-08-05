/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.value.events;

import com.openitech.text.CaseInsensitiveString;
import com.openitech.value.fields.Field;
import com.openitech.value.fields.FieldValue;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author uros
 */
public class Event {

  public static final Field EVENT_SOURCE = new Field("EVENT_SOURCE", java.sql.Types.INTEGER);
  public static final Field EVENT_DATE = new Field("EVENT_DATE", java.sql.Types.DATE);

  public Event(int sifrant, String sifra) {
    this(null, sifrant, sifra);
  }

  public Event(int sifrant, String sifra, int eventSource) {
    this(null, sifrant, sifra, eventSource);
  }

  public Event(Event parent, int sifrant, String sifra) {
    this(parent, sifrant, sifra, Integer.MIN_VALUE);
  }

  public Event(Event parent, int sifrant, String sifra, int eventSource) {
    this.parent = parent;
    this.sifrant = sifrant;
    this.sifra = sifra;
    this.eventSource = eventSource;
  }
  private long id = -1;

  /**
   * Get the value of id
   *
   * @return the value of id
   */
  public long getId() {
    return id;
  }

  /**
   * Set the value of id
   *
   * @param id new value of id
   */
  public void setId(long id) {
    this.id = id;
  }
  private final Event parent;

  /**
   * Get the value of parent
   *
   * @return the value of parent
   */
  public Event getParent() {
    return parent;
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
    Field key = new Field(value);
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
    for (String pk:primaryKeys) {
      fields.add(fieldsMap.get(CaseInsensitiveString.valueOf(pk)));
    }
    this.primaryKey = fields.toArray(new Field[fields.size()]);
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
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
    return hash;
  }

  @Override
  public String toString() {
    return (parent != null ? "P:" + parent.toString() : "E:") + sifrant + "-" + sifra + ":" + id;
  }
}
