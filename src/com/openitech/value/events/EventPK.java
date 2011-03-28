/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.value.events;

import com.openitech.value.VariousValue;
import com.openitech.value.events.Event.EventOperation;
import com.openitech.value.fields.FieldValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author domenbasic
 */
public class EventPK {

  private static final Comparator<FieldValue> FIELD_COMPARATOR = new Comparator<FieldValue>() {

    @Override
    public int compare(FieldValue o1, FieldValue o2) {
      if (o1.getIdPolja() != o1.getIdPolja()) {
        return o1.getIdPolja().compareTo(o1.getIdPolja());
      } else {
        return o1.getFieldIndex() < o2.getFieldIndex() ? -1 : (o1.getFieldIndex() == o2.getFieldIndex() ? 0 : 1);
      }
    }
  };
  private Long eventId;
  private EventOperation eventOperation = EventOperation.UPDATE;

  public EventPK() {
  }

  public EventPK(Long eventId, int idSifranta, String idSifre) {
    this(eventId, idSifranta, idSifre, null);
  }

  public EventPK(Long eventId, int idSifranta, String idSifre, Integer versionID) {
    this.eventId = eventId;
    this.idSifranta = idSifranta;
    this.idSifre = idSifre;
    this.versionID = versionID;
  }
  private Long oldEventId;

  /**
   * Get the value of id
   *
   * @return the value of id
   */
  public Long getOldEventId() {
    return oldEventId;
  }

  /**
   * Set the value of id
   *
   * @param id new value of id
   */
  public void setOldEventId(Long oldEventId) {
    this.oldEventId = oldEventId;
  }

  /**
   * Get the value of eventId
   *
   * @return the value of eventId
   */
  public Long getEventId() {
    return eventId;
  }

  /**
   * Set the value of eventId
   *
   * @param eventId new value of eventId
   */
  public void setEventId(Long eventId) {
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

    Collections.sort(fields, FIELD_COMPARATOR);

    for (FieldValue fieldValue : fields) {
      if (fieldValue.getLookupType() == null) {
        result.append("{");
        result.append(Integer.toHexString(fieldValue.getIdPolja())).append(";");
        result.append(Integer.toHexString(fieldValue.getFieldIndex())).append(";");
        result.append(getValueString(fieldValue, StringType.HEX)).append(";");
        result.append("}");
      }
    }
    return result.toString();
  }

  private String getValueString(FieldValue fieldValue, StringType type) {
    Long valueId = null;
    if (fieldValue.getValue() instanceof VariousValue) {
      valueId = ((VariousValue) fieldValue.getValue()).getId();
    } else {
      valueId = fieldValue.getValueId();
    }

    if (valueId == null) {
      return "null";
    } else if (type == StringType.HEX) {
      return Long.toHexString(valueId);
    } else {
      return Long.toString(valueId);
    }
  }

  private enum StringType {

    HEX,
    NORMAL
  }

  public String toNormalString() {
    StringBuilder result = new StringBuilder();

    for (FieldValue fieldValue : fields) {
      result.append("{");
      result.append(Integer.toString(fieldValue.getIdPolja())).append(";");
      result.append(Integer.toString(fieldValue.getFieldIndex())).append(";");
      result.append(getValueString(fieldValue, StringType.NORMAL)).append(";");
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
      result.append(getValueString(fieldValue, StringType.NORMAL)).append(";");
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
