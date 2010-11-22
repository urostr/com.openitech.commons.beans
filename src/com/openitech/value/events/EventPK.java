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

  public EventPK(long eventId, int idSifranta, String idSifre) {
    this(eventId, idSifranta, idSifre, null);
  }

  public EventPK(long eventId, int idSifranta, String idSifre, Integer versionID) {
    this.eventId = eventId;
    this.idSifranta = idSifranta;
    this.idSifre = idSifre;
    this.versionID = versionID;
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

  /**
   * Get the value of primaryKey
   *
   * @return the value of primaryKey
   */
  public String getPrimaryKey() {
    return toHexString();
  }
  private List<FieldValue> fields = new ArrayList<FieldValue>();

  public void addPrimaryKeyField(FieldValue field) {
    fields.add(field);
  }

  public List<FieldValue> getPrimaryKeyFields() {
    //TODO potrebno implementirat: primaryKeyToFields
    return fields;
  }

  public EventOperation getEventOperation() {
    return eventOperation;
  }

  public String toHexString() {
    StringBuilder result = new StringBuilder();
    List<FieldValue> fieldValues = new ArrayList<FieldValue>();
    for (FieldValue fieldValue : fields) {
      int insertIndex = 0;
      for (FieldValue fieldValue1 : fieldValues) {
        if (fieldValue.getIdPolja() > fieldValue1.getIdPolja()) {
          insertIndex++;
        }
      }
      fieldValues.add(insertIndex, fieldValue);
    }
    for (FieldValue fieldValue : fieldValues) {
      if (fieldValue.getLookupType() == null) {
        result.append("{");
        result.append(Integer.toHexString(fieldValue.getIdPolja())).append(";");
        result.append(Integer.toHexString(fieldValue.getFieldIndex())).append(";");
        result.append(((VariousValue) fieldValue.getValue()).getId() == null ? "null" : Long.toHexString(((VariousValue) fieldValue.getValue()).getId())).append(";");
        result.append("}");
      }
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
  protected Integer versionID = null;

  /**
   * Get the value of versionID
   *
   * @return the value of versionID
   */
  public Integer getVersionID() {
    return versionID;
  }

  /**
   * Set the value of versionID
   *
   * @param versionID new value of versionID
   */
  public void setVersionID(Integer versionID) {
    this.versionID = versionID;
  }
}
