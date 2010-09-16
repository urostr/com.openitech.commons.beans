/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.value.events;

import com.openitech.value.fields.FieldValue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author domenbasic
 */
public class EventPK {

  private long eventId;

  /**
   * Get the value of eventId
   *
   * @return the value of eventId
   */
  public long getEventId() {
    return eventId;
  }

  /**
   * Set the value of eventId
   *
   * @param eventId new value of eventId
   */
  public void setEventId(long eventId) {
    this.eventId = eventId;
  }

  private int idSifranta;

  /**
   * Get the value of idSifranta
   *
   * @return the value of idSifranta
   */
  public int getIdSifranta() {
    return idSifranta;
  }

  /**
   * Set the value of idSifranta
   *
   * @param idSifranta new value of idSifranta
   */
  public void setIdSifranta(int idSifranta) {
    this.idSifranta = idSifranta;
  }

  private String idSifre;

  /**
   * Get the value of idSifre
   *
   * @return the value of idSifre
   */
  public String getIdSifre() {
    return idSifre;
  }

  /**
   * Set the value of idSifre
   *
   * @param idSifre new value of idSifre
   */
  public void setIdSifre(String idSifre) {
    this.idSifre = idSifre;
  }

  private List<FieldValue> fields = new ArrayList<FieldValue>();

  public void addField(FieldValue field) {
    fields.add(field);
  }

  public List<FieldValue> getFields() {
    return fields;
  }

  

  public String toHexString() {
    StringBuilder result = new StringBuilder();

    for (FieldValue fieldValue : fields) {
      result.append("{");
      result.append(Integer.toHexString(fieldValue.getIdPolja())).append(";");
      result.append(Integer.toHexString(fieldValue.getFieldIndex())).append(";");
      result.append(Long.toHexString((Long) fieldValue.getValue())).append(";");
      result.append("}");
    }
    return result.toString();
  }

  public String toNormalString() {
    StringBuilder result = new StringBuilder();

    for (FieldValue fieldValue : fields) {
      result.append("{");
      result.append(Integer.toString(fieldValue.getIdPolja())).append(";");
      result.append(Integer.toString(fieldValue.getFieldIndex())).append(";");
      result.append(Long.toString((Long) fieldValue.getValue())).append(";");
      result.append("}");
    }
    return result.toString();
  }
}
