/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.value.events;

import com.openitech.value.VariousValue;
import com.openitech.value.events.Event.EventOperation;
import com.openitech.value.fields.FieldValue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author domenbasic
 */
public class EventPK {

  private long eventId;
  private EventOperation eventOperation = EventOperation.UPDATE;

  public EventPK() {
  }

  
  public EventPK(long eventId, int idSifranta, String idSifre, String primaryKey) {
    this.eventId = eventId;
    this.idSifranta = idSifranta;
    this.idSifre = idSifre;
    this.primaryKey = primaryKey;
  }

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
  private String primaryKey;

  /**
   * Get the value of primaryKey
   *
   * @return the value of primaryKey
   */
  public String getPrimaryKey() {
    return primaryKey;
  }

  /**
   * Set the value of primaryKey
   *
   * @param primaryKey new value of primaryKey
   */
  public void setPrimaryKey(String primaryKey) {
    this.primaryKey = primaryKey;
  }
  private List<FieldValue> fields = new ArrayList<FieldValue>();

  public void addField(FieldValue field) {
    fields.add(field);
  }

  public List<FieldValue> getFields() {
    //TODO potrebno implementirat: primaryKeyToFields
    return fields;
  }

  public EventOperation getEventOperation() {
    return eventOperation;
  }

  public String toHexString() {
    StringBuilder result = new StringBuilder();

    for (FieldValue fieldValue : fields) {
      result.append("{");
      result.append(Integer.toHexString(fieldValue.getIdPolja())).append(";");
      result.append(Integer.toHexString(fieldValue.getFieldIndex())).append(";");
      result.append(((VariousValue) fieldValue.getValue()).getId() == null ? "null" : Long.toHexString(((VariousValue) fieldValue.getValue()).getId())).append(";");
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
      result.append(((VariousValue) fieldValue.getValue()).getId() == null ? "null" : Long.toString(((VariousValue) fieldValue.getValue()).getId())).append(";");
      result.append("}");
    }
    return result.toString();
  }

  public String getDebugString() {
    StringBuilder result = new StringBuilder();

    for (FieldValue fieldValue : fields) {
      result.append("{");
      result.append(Integer.toString(fieldValue.getIdPolja())).append(";");
      result.append(Integer.toString(fieldValue.getFieldIndex())).append(";");
      result.append(((VariousValue) fieldValue.getValue()).getId() == null ? "null" : Long.toString(((VariousValue) fieldValue.getValue()).getId())).append(";");
      if (fieldValue.getValue() != null) {
        if (fieldValue.getValue() instanceof VariousValue) {
          result.append(((VariousValue) fieldValue.getValue()).getValue() == null ? "null" : (((VariousValue) fieldValue.getValue()).getValue().toString()));
        } else {
          result.append(fieldValue.getValue());
        }
      }
      result.append(";");
      result.append("}\n");
    }
    return result.toString();
  }

  public void setEventOperation(EventOperation eventOperation) {
    this.eventOperation = eventOperation;
  }
}
