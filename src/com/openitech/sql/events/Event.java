/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.sql.events;

import com.openitech.sql.Field;
import com.openitech.sql.FieldValue;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author uros
 */
public class Event {

  public Event(int sifrant, String sifra) {
    this.sifrant = sifrant;
    this.sifra = sifra;
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


  java.util.Map<Field, java.util.List<FieldValue>> eventValues = new java.util.LinkedHashMap<Field, java.util.List<FieldValue>> ();

  public void addValue(FieldValue value) {
    getFieldValues(value).add(value);
  }

  public void removeAll(FieldValue value) {
    eventValues.remove(value);
  }

  public Map<Field, List<FieldValue>> getEventValues() {
    return eventValues;
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
}