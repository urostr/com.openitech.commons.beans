/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.util.mssql;

import com.openitech.db.model.sql.SQLPrimaryKeyException;
import com.openitech.db.model.xml.config.MaterializedView;
import com.openitech.db.model.xml.config.TemporaryTable;
import com.openitech.text.CaseInsensitiveString;
import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.components.DbNaslovDataModel;
import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSource.SqlParameter;
import com.openitech.db.model.DbDataSourceIndex;
import com.openitech.db.model.factory.DataSourceFactory;
import com.openitech.db.model.sql.SQLDataSource;
import com.openitech.db.model.sql.SQLNotificationException;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.events.concurrent.ObjectSemaphore;
import com.openitech.value.fields.Field;
import com.openitech.value.events.Event;
import com.openitech.value.events.EventQuery;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.fields.FieldValue;
import com.openitech.value.fields.ValueType;
import com.openitech.value.events.ActivityEvent;
import com.openitech.io.ReadInputStream;
import com.openitech.sql.cache.CachedTemporaryTablesManager;
import com.openitech.value.StringValue;
import com.openitech.value.VariousValue;
import com.openitech.value.events.EventQueryParameter;
import com.openitech.value.events.EventPK;
import com.openitech.value.events.SqlEventPK;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author uros
 */
public class SqlUtilitesImpl extends SqlUtilities {

  PreparedStatement logChanges;
  PreparedStatement logValues;
  PreparedStatement logChangedValues;
  PreparedStatement updateEvents;
  PreparedStatement delete_event;
  PreparedStatement insertEvents;
  PreparedStatement findEventValue;
  PreparedStatement findEventById;
  PreparedStatement insertEventValues;
  PreparedStatement updateEventValues;
  PreparedStatement deleteEventValues;
  PreparedStatement find_datevalue;
  PreparedStatement find_intvalue;
  PreparedStatement find_realvalue;
  PreparedStatement find_stringvalue;
  PreparedStatement find_clobvalue;
  PreparedStatement get_field;
  PreparedStatement get_fields;
  PreparedStatement insertNeznaniNaslov;
  PreparedStatement findHsNeznanaId;
  PreparedStatement insertEventsOpombe;
  PreparedStatement findOpomba;
  PreparedStatement insertVersion;
  PreparedStatement insertEventVersion;
  PreparedStatement findVersion;
  String getEventVersionSQL;
  PreparedStatement storeCachedTemporaryTable;
  PreparedStatement delete_eventPK;
  PreparedStatement insert_eventPK;
  PreparedStatement find_eventPK;
  PreparedStatement update_eventPK;
  PreparedStatement insert_eventPK_versions;
  PreparedStatement find_eventPK_versions;
  PreparedStatement find_eventPK_versions_byValues;
  PreparedStatement update_eventPK_versions;
  PreparedStatement insert_eventLookupKeys;
  PreparedStatement update_eventLookupKeys;
  PreparedStatement find_eventLookupKeys;
  PreparedStatement findValue;
  CallableStatement callStoredValue;
  PreparedStatement insertScheduler;

  @Override
  public long getScopeIdentity() throws SQLException {
    Statement statement = ConnectionManager.getInstance().getTxConnection().createStatement();

    ResultSet result = statement.executeQuery("SELECT SCOPE_IDENTITY() AS ScopeIdentity");
    result.next();

    return result.getLong(1);
  }

  @Override
  public long getCurrentIdentity(String tableName) throws SQLException {
    Statement statement = ConnectionManager.getInstance().getTxConnection().createStatement();

    ResultSet result = statement.executeQuery("SELECT IDENT_CURRENT(" + tableName + ") AS CurrentIdentity");
    result.next();

    return result.getLong(1);
  }

  @Override
  public long getLastIdentity() throws SQLException {
    Statement statement = ConnectionManager.getInstance().getTxConnection().createStatement();

    ResultSet result = statement.executeQuery("SELECT @@IDENTITY AS [Identity]");
    result.next();

    return result.getLong(1);
  }

  @Override
  protected void logChanges(String application, String database, String tableName, Operation operation, List<FieldValue> newValues, List<FieldValue> oldValues) throws SQLException {
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (logChanges == null) {
      logChanges = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insert_change_log.sql", "cp1250"));
    }
    if (logValues == null) {
      logValues = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insert_values.sql", "cp1250"));
    }

    if (logChangedValues == null) {
      logChangedValues = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insert_changed_values.sql", "cp1250"));
    }

    try {
      ObjectSemaphore.aquire(logChanges, logValues, logChangedValues);

      FieldValue[] fieldValues = new FieldValue[]{
        new FieldValue("application", java.sql.Types.VARCHAR, application),
        new FieldValue("database", java.sql.Types.VARCHAR, database),
        new FieldValue("tableName", java.sql.Types.VARCHAR, tableName),
        new FieldValue("operation", java.sql.Types.VARCHAR, operation.toString())
      };

      executeUpdate(logChanges, fieldValues);

      long changeId = getLastIdentity();

      for (int fieldno = 0; fieldno < newValues.size(); fieldno++) {
        FieldValue newValue = newValues.get(fieldno);
        FieldValue oldValue = oldValues.get(fieldno);

        final Object value = newValue.getValue();
        final int fieldType = newValue.getValueType().getTypeIndex();

        Long newValueId = storeValue(fieldType, value);
        Long oldValueId = storeValue(fieldType, oldValue.getValue());

        fieldValues = new FieldValue[]{
                  new FieldValue("ChangeId", Types.BIGINT, changeId),
                  new FieldValue("FieldName", Types.VARCHAR, newValue.getName()),
                  new FieldValue("NewValueId", Types.BIGINT, newValueId),
                  new FieldValue("OldValueId", Types.BIGINT, oldValueId)
                };

        executeUpdate(logChangedValues, fieldValues);
      }
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      ObjectSemaphore.release(logChanges, logValues, logChangedValues);
    }
  }

  @Override
  protected Integer assignEventVersion(List<EventPK> eventPKs) throws SQLException {
    if (!eventPKs.isEmpty()) {
      //najprej dodaj verzijo (tabela Versions)
      Integer versionId = getVersion(eventPKs);

      if (versionId == null) {
        versionId = new Integer((int) storeVersion());
        //nato v tabelo EventVersions vpisi z gornjo verzijo vse podane eventId-je
        List<Long> storedEventIds = new ArrayList<Long>();
        for (EventPK eventPK : eventPKs) {
          if (!storedEventIds.contains(eventPK.getEventId())) {
            storedEventIds.add(eventPK.getEventId());
            storeEventVersion(versionId, eventPK);
            eventPK.setVersionID(versionId);
            storePrimaryKeyVersions(eventPK);
          }

        }
      }
      return versionId;
    } else {
      return null;
    }
  }

  private Integer getVersion(List<EventPK> eventPKs) throws SQLException {

    final Connection connection = ConnectionManager.getInstance().getTxConnection();


    if (getEventVersionSQL == null) {
      getEventVersionSQL = com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "getEventVersion.sql", "cp1250");
    }

    try {
      ObjectSemaphore.aquire(getEventVersionSQL);

      StringBuilder sb = new StringBuilder();

      List<Long> eventIds = new ArrayList<Long>(eventPKs.size());
      for (EventPK eventPK : eventPKs) {
        sb.append(sb.length() > 0 ? ", " : "").append("?");
        eventIds.add(eventPK.getEventId());
      }

      CachedRowSet versions = new com.sun.rowset.CachedRowSetImpl();

      versions.populate(SQLDataSource.executeQuery(getEventVersionSQL.replaceAll("<%EVENTS_LIST%>", sb.toString()).replaceAll("<%EVENT_LIST_SIZE%>", Integer.toString(eventIds.size())),
              eventIds,
              connection));

      if ((versions.size() == 1) && (versions.first())) {
        return versions.getInt(1);
      } else {
        return null;
      }
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      ObjectSemaphore.release(getEventVersionSQL);
    }
    return null;
  }

  private long storeVersion() throws SQLException {
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (insertVersion == null) {
      insertVersion = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insertVersion.sql", "cp1250"));
    }
    try {
      ObjectSemaphore.aquire(insertVersion);

      insertVersion.executeUpdate();
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      ObjectSemaphore.release(insertVersion);
    }
    return getLastIdentity();
  }

  private void storeEventVersion(long versionId, EventPK eventPK) throws SQLException {
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (insertEventVersion == null) {
      insertEventVersion = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insertEventVersion.sql", "cp1250"));
    }
    try {
      ObjectSemaphore.aquire(insertEventVersion);

      int param = 1;
      insertEventVersion.clearParameters();
      insertEventVersion.setLong(param++, versionId);
      insertEventVersion.setLong(param++, eventPK.getEventId());
      System.out.println("versionId = " + versionId + ", eventId = " + eventPK.getEventId());
      insertEventVersion.executeUpdate();
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      ObjectSemaphore.release(insertEventVersion);
    }
  }

  @Override
  public Integer findVersion(Long eventId) throws SQLException {
    Integer result = null;
    if (eventId != null) {
      final Connection connection = ConnectionManager.getInstance().getTxConnection();
      if (findVersion == null) {
        findVersion = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_version.sql", "cp1250"));
      }
      try {
        ObjectSemaphore.aquire(findVersion);

        int param = 1;
        findVersion.clearParameters();
        findVersion.setLong(param++, eventId);
        ResultSet rs_findVersion = findVersion.executeQuery();
        if (rs_findVersion.next()) {
          result = rs_findVersion.getInt(1);
          if (rs_findVersion.wasNull()) {
            result = null;
          }
        }
      } catch (InterruptedException ex) {
        Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
        ObjectSemaphore.release(findVersion);
      }
    }

    return result;
  }

  @Override
  public EventPK storeEvent(Event event, Event oldEvent) throws SQLException {
    if ((oldEvent != null) && oldEvent.equalEventValues(event)) {
      return oldEvent.getEventPK();
    } else {
      final Connection connection = ConnectionManager.getInstance().getTxConnection();
      if (insertEvents == null) {
        insertEvents = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insertEvents.sql", "cp1250"));
//      insertEvents.setQueryTimeout(15);
      }
      if (updateEvents == null) {
        updateEvents = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "updateEvents.sql", "cp1250"));
//      updateEvents.setQueryTimeout(15);
      }
      if (findEventValue == null) {
        findEventValue = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_eventvalue.sql", "cp1250"));
      }
      if (insertEventValues == null) {
        insertEventValues = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insertEventValues.sql", "cp1250"));
//      insertEventValues.setQueryTimeout(15);
      }
      if (updateEventValues == null) {
        updateEventValues = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "updateEventValue.sql", "cp1250"));
//      updateEventValues.setQueryTimeout(15);
      }
      if (deleteEventValues == null) {
        deleteEventValues = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "deleteEventValue.sql", "cp1250"));
      }
      if (get_field == null) {
        get_field = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "get_field.sql", "cp1250"));
      }
      if (delete_eventPK == null) {
        delete_eventPK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "delete_eventPK.sql", "cp1250"));
      }
      int param;
      boolean success = true;
      boolean commit = false;
      boolean isTransaction = isTransaction();
      // <editor-fold defaultstate="collapsed" desc="Shrani">

      connection.clearWarnings();
      Long events_ID = null;
      try {
        ObjectSemaphore.aquire(insertEvents, updateEvents, findEventValue, insertEventValues, updateEventValues, deleteEventValues, get_field, delete_eventPK);
        if (!isTransaction) {
          beginTransaction();
        }

        if (event.getOperation() == Event.EventOperation.UPDATE) {
          boolean insert = (event.getId() == null) || (event.getId() == -1) || event.isVersioned();

          if (insert) {
//        System.out.println("event:" + event.getSifrant() + "-" + event.getSifra() + ":inserting");

            if (event.isVersioned() && (oldEvent != null)) {
              //updataj stari event
              //oz.deletej valid = false
              param = 1;
              updateEvents.clearParameters();
              updateEvents.setInt(param++, oldEvent.getSifrant());
              updateEvents.setString(param++, oldEvent.getSifra());
              if (oldEvent.getEventSource() == Integer.MIN_VALUE) {
                updateEvents.setNull(param++, java.sql.Types.INTEGER);
              } else {
                updateEvents.setInt(param++, oldEvent.getEventSource());
              }
              updateEvents.setTimestamp(param++, new java.sql.Timestamp(oldEvent.getDatum().getTime()));
              updateEvents.setString(param++, oldEvent.getOpomba());
              updateEvents.setTimestamp(param++, new java.sql.Timestamp(System.currentTimeMillis()));
              updateEvents.setBoolean(param++, false);
              updateEvents.setTimestamp(param++, new Timestamp(System.currentTimeMillis()));
              updateEvents.setLong(param++, oldEvent.getId());

              success = success && updateEvents.executeUpdate() > 0;

              //
              param = 1;
              delete_eventPK.clearParameters();
              delete_eventPK.setLong(param++, oldEvent.getId());
              delete_eventPK.executeUpdate();
              //success = success && delete_eventPK.executeUpdate() > 0;
            }

            //insertaj event
            param = 1;
            insertEvents.clearParameters();
            insertEvents.setInt(param++, event.getSifrant());
            insertEvents.setString(param++, event.getSifra());
            if (event.getEventSource() == Integer.MIN_VALUE) {
              insertEvents.setNull(param++, java.sql.Types.INTEGER);
            } else {
              insertEvents.setInt(param++, event.getEventSource());
            }
            Date eventDatum = event.getDatum();
            if (eventDatum != null) {
              insertEvents.setTimestamp(param++, new java.sql.Timestamp(event.getDatum().getTime()));
            } else {
              insertEvents.setTimestamp(param++, new java.sql.Timestamp(System.currentTimeMillis()));
            }
            insertEvents.setString(param++, event.getOpomba());
            success = success && insertEvents.executeUpdate() > 0;

            if (success) {
              events_ID = getLastIdentity();
            } else {
              throw new SQLException("Neuspešno dodajanje dogodka!");
            }


          } else {
            events_ID = event.getId();
//        System.out.println("event:" + event.getSifrant() + "-" + event.getSifra() + ":updating:" + events_ID);

            param = 1;
            updateEvents.clearParameters();
            updateEvents.setInt(param++, event.getSifrant());
            updateEvents.setString(param++, event.getSifra());
            if (event.getEventSource() == Integer.MIN_VALUE) {
              updateEvents.setNull(param++, java.sql.Types.INTEGER);
            } else {
              updateEvents.setInt(param++, event.getEventSource());
            }
            long time = System.currentTimeMillis();
            if (event != null && event.getDatum() != null) {
              time = event.getDatum().getTime();
            }
            updateEvents.setTimestamp(param++, new java.sql.Timestamp(time));
            updateEvents.setString(param++, event.getOpomba());
            updateEvents.setTimestamp(param++, new java.sql.Timestamp(System.currentTimeMillis()));
            updateEvents.setBoolean(param++, (event.getOperation() != null && event.getOperation() == Event.EventOperation.DELETE) ? false : true);
            updateEvents.setTimestamp(param++, (event.getOperation() != null && event.getOperation() == Event.EventOperation.DELETE) ? new Timestamp(System.currentTimeMillis()) : null);
            updateEvents.setLong(param++, events_ID);

            success = success && updateEvents.executeUpdate() > 0;
          }

          if (success) {
            success = success && storeOpomba(events_ID, event.getOpomba());
          }

          if (success) {
            if (event.getVeljavnost() != null) {
              success = success && storeVeljavnost(events_ID, event.getVeljavnost());
            }
          }

          if (success) {
            List<FieldValue> fieldValuesList = new ArrayList<FieldValue>(event.getEventValues().size());

            EventPK eventPK = new EventPK();

            Map<Field, List<FieldValue>> eventValues = event.getEventValues();
            for (Field field : eventValues.keySet()) {
              List<FieldValue> fieldValues = eventValues.get(field);
              for (int i = 0; i < fieldValues.size(); i++) {
                FieldValue value = fieldValues.get(i);
                Long valueId = storeValue(value.getValueType(), value.getValue());

                fieldValuesList.add(value);

                String fieldName = field.getName();
                String fieldNameWithIndex = field.getName();
                int fieldValueIndex = field.getFieldIndex();

                int field_id;
                if ((field.getIdPolja() == null)
                        || (field.getIdPolja() < 0)) {
                  if (fieldValueIndex > 1) {
                    Field nonIndexed = field.getNonIndexedField();
                    fieldName = nonIndexed.getName();
                  }
                  param = 1;
                  get_field.setString(param, fieldName);

                  ResultSet rs_field = get_field.executeQuery();
                  if (!rs_field.next()) {
                    throw new SQLException("Cannot find IDPolja! FieldName=" + fieldName);
                  }

                  field_id = rs_field.getInt("Id");
                } else {
                  field_id = field.getIdPolja();
                }

                if (valueId != null) {
                  if (field.getLookupType() == null) {

                    param = 1;
                    findEventValue.clearParameters();
                    findEventValue.setLong(param++, events_ID);
                    findEventValue.setInt(param++, field_id);
                    findEventValue.setInt(param++, fieldValueIndex);  //indexPolja

                    ResultSet rs = findEventValue.executeQuery();
                    rs.next();

                    if (rs.getInt(1) == 0) {
                      //insertaj event value
                      param = 1;
                      insertEventValues.clearParameters();
                      insertEventValues.setLong(param++, events_ID);
                      insertEventValues.setInt(param++, field_id);
                      insertEventValues.setInt(param++, fieldValueIndex);  //indexPolja
                      if (valueId == null) {
                        insertEventValues.setNull(param++, java.sql.Types.BIGINT);
                      } else {
                        insertEventValues.setLong(param++, valueId);
                      }

                      success = success && insertEventValues.executeUpdate() > 0;
                    } else {
                      //updataj event value
                      param = 1;
                      updateEventValues.clearParameters();
                      if (valueId == null) {
                        updateEventValues.setNull(param++, java.sql.Types.BIGINT);
                      } else {
                        updateEventValues.setLong(param++, valueId);
                      }
                      updateEventValues.setLong(param++, events_ID);
                      updateEventValues.setInt(param++, field_id);
                      updateEventValues.setInt(param++, fieldValueIndex);  //indexPolja

                      success = success && updateEventValues.executeUpdate() > 0;
                    }
                  }

                  if (event.getPrimaryKey() != null && event.getPrimaryKey().length > 0) {
                    FieldValue fieldValuePK = new FieldValue(field_id, fieldNameWithIndex, field.getType(), fieldValueIndex, new VariousValue(valueId, value.getType(), value.getValue()));
                    for (Field field1 : event.getPrimaryKey()) {
                      if (field1.equals(fieldValuePK)) {
                        eventPK.addPrimaryKeyField(fieldValuePK);
                        break;
                      }
                    }
                  }
                } else {
                  if (!event.isVersioned()) {
                    //delete eventValue
                    param = 1;
                    deleteEventValues.clearParameters();
                    deleteEventValues.setLong(param++, events_ID);
                    deleteEventValues.setInt(param++, field_id);
                    deleteEventValues.setInt(param++, fieldValueIndex);
                    deleteEventValues.executeUpdate();
                  }
                }
              }
            }
            if (event.getPrimaryKey() != null && event.getPrimaryKey().length > 0) {
              eventPK.setEventId(events_ID);
              eventPK.setIdSifranta(event.getSifrant());
              eventPK.setIdSifre(event.getSifra());
              eventPK.setEventOperation(event.getOperation());
              success = success && storePrimaryKey(eventPK);
            }

            success = success && storeEventLookUpKeys(events_ID, fieldValuesList);
          }

          commit = success;
        } else if (event.getOperation() == Event.EventOperation.DELETE) {
          if ((event.getId() != null) && (event.getId() > 0)) {
            events_ID = event.getId();
            //updataj stari event
            //oz.deletej valid = false
            param = 1;
            updateEvents.clearParameters();
            updateEvents.setInt(param++, event.getSifrant());
            updateEvents.setString(param++, event.getSifra());
            if (oldEvent.getEventSource() == Integer.MIN_VALUE) {
              updateEvents.setNull(param++, java.sql.Types.INTEGER);
            } else {
              updateEvents.setInt(param++, event.getEventSource());
            }
            updateEvents.setTimestamp(param++, new java.sql.Timestamp(oldEvent.getDatum().getTime()));
            updateEvents.setString(param++, event.getOpomba());
            updateEvents.setTimestamp(param++, new java.sql.Timestamp(System.currentTimeMillis()));
            updateEvents.setBoolean(param++, false);
            updateEvents.setTimestamp(param++, new Timestamp(System.currentTimeMillis()));
            updateEvents.setLong(param++, event.getId());

            success = success && updateEvents.executeUpdate() > 0;

            //
            param = 1;
            delete_eventPK.clearParameters();
            delete_eventPK.setLong(param++, event.getId());
            delete_eventPK.executeUpdate();
            //success = success && delete_eventPK.executeUpdate() > 0;

            commit = success;
          }
        } else {
          events_ID = event.getId();
        }
      } catch (InterruptedException ex) {
        Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
        ObjectSemaphore.release(insertEvents, updateEvents, findEventValue, insertEventValues, updateEventValues, deleteEventValues, get_field, delete_eventPK);
        if (!isTransaction) {
          endTransaction(commit);
        }
        event.setId(events_ID);
      }

      return event.getEventPK();
    }
  }

  @Override
  public boolean storeEventLookUpKeys(Long eventId, List<FieldValue> fieldValues) throws SQLException {
    if (insert_eventLookupKeys == null) {
      insert_eventLookupKeys = ConnectionManager.getInstance().getTxConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "insert_eventLookupKeys.sql", "cp1250"));
    }
    if (update_eventLookupKeys == null) {
      update_eventLookupKeys = ConnectionManager.getInstance().getTxConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "update_eventLookupKeys.sql", "cp1250"));
    }
    Map<Integer, List<FieldValue>> lookupFields = new HashMap<Integer, List<FieldValue>>();
    for (FieldValue fieldValue : fieldValues) {
      if (fieldValue.getLookupType() != null) {
        Integer idPolja = fieldValue.getIdPolja();
        if (!lookupFields.containsKey(idPolja)) {
          lookupFields.put(idPolja, new ArrayList<FieldValue>());
        }
        lookupFields.get(idPolja).add(fieldValue);
      }
    }
    boolean success = true;
    int numberOfValues = 0;

    Integer versionId = null;
    int fieldValueIndex = -1;
    Integer idSifranta = null;
    String idSifre = null;
    String primaryKey = null;
    for (Integer lookupIdPolja : lookupFields.keySet()) {
      numberOfValues = 0;
      for (FieldValue fieldValue : lookupFields.get(lookupIdPolja)) {
        fieldValueIndex = fieldValue.getFieldIndex();

        Object value = fieldValue.getValue();
        if (fieldValue.getValue() instanceof VariousValue) {
          value = ((VariousValue) value).getValue();
        }
        switch (fieldValue.getLookupType()) {
          case VERSION_ID:
            versionId = (Integer) value;
            numberOfValues++;
            break;
          case ID_SIFRANTA:
            idSifranta = (Integer) value;
            numberOfValues++;
            break;
          case ID_SIFRE:
            idSifre = (String) value;
            if (idSifre != null) {
              numberOfValues++;
            }
            break;
          case PRIMARY_KEY:
            primaryKey = (String) value;
            numberOfValues++;
            break;
        }
      }
      try {
        ObjectSemaphore.aquire(insert_eventLookupKeys, update_eventLookupKeys);

        //vse vrednosti za lookup morajo biti izpolnjene
        if (Field.LookupType.values().length == numberOfValues) {
          if (findLookupKeys(lookupIdPolja, fieldValueIndex, eventId)) {
            int param = 1;
            update_eventLookupKeys.clearParameters();

            if (versionId != null) {
              update_eventLookupKeys.setInt(param++, versionId);
            } else {
              update_eventLookupKeys.setNull(param++, java.sql.Types.INTEGER);
            }
            update_eventLookupKeys.setInt(param++, idSifranta);
            update_eventLookupKeys.setString(param++, idSifre);
            update_eventLookupKeys.setString(param++, primaryKey);

            update_eventLookupKeys.setLong(param++, eventId);
            update_eventLookupKeys.setInt(param++, lookupIdPolja);
            update_eventLookupKeys.setInt(param++, fieldValueIndex);

            success = success && update_eventLookupKeys.executeUpdate() > 0;
          } else {
            //insert
            int param = 1;
            insert_eventLookupKeys.clearParameters();
            insert_eventLookupKeys.setLong(param++, eventId);
            insert_eventLookupKeys.setInt(param++, lookupIdPolja);
            insert_eventLookupKeys.setInt(param++, fieldValueIndex);
            if (versionId != null) {
              insert_eventLookupKeys.setInt(param++, versionId);
            } else {
              insert_eventLookupKeys.setNull(param++, java.sql.Types.INTEGER);
            }
            insert_eventLookupKeys.setInt(param++, idSifranta);
            insert_eventLookupKeys.setString(param++, idSifre);
            insert_eventLookupKeys.setString(param++, primaryKey);

            Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.WARNING, "Inserting lookupKeys: {0} , {1} , {2} , {3} , {4} , {5} , ", new Object[]{lookupIdPolja, fieldValueIndex, versionId, idSifranta, idSifre, primaryKey});

            success = success && insert_eventLookupKeys.executeUpdate() > 0;
          }
        } else {
          throw new SQLNotificationException("Napaka pri shranjevanju lookup polj! Niso vsa polja izpolnjena");
        }
      } catch (InterruptedException ex) {
        Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
        ObjectSemaphore.release(insert_eventLookupKeys, update_eventLookupKeys);
      }
    }
    return success;
  }

  public boolean findLookupKeys(int idPolja, int fieldValueIndex, Long eventId) throws SQLException {
    boolean result = false;
    if (find_eventLookupKeys == null) {
      find_eventLookupKeys = ConnectionManager.getInstance().getTxConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "find_eventLookupKeys.sql", "cp1250"));
    }
    try {
      ObjectSemaphore.aquire(find_eventLookupKeys);
      int param = 1;
      find_eventLookupKeys.clearParameters();
      find_eventLookupKeys.setLong(param++, eventId);
      find_eventLookupKeys.setInt(param++, idPolja);
      find_eventLookupKeys.setInt(param++, fieldValueIndex);
      ResultSet rs_find_eventLookupKeys = find_eventLookupKeys.executeQuery();
      if (rs_find_eventLookupKeys.next()) {
        result = true;
      }
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      ObjectSemaphore.release(find_eventLookupKeys);
    }
    return result;
  }

  @Override
  public VariousValue findValue(long valueId) throws SQLException {
    VariousValue result = null;
    if (findValue == null) {
      findValue = ConnectionManager.getInstance().getTxConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "findValue.sql", "cp1250"));
    }
    try {
      ObjectSemaphore.aquire(findValue);

      int param = 1;
      findValue.clearParameters();
      findValue.setLong(param++, valueId);
      ResultSet rs_findValue = findValue.executeQuery();
      if (rs_findValue.next()) {
        int fieldType = rs_findValue.getInt("FieldType");
        ValueType type = ValueType.valueOf(fieldType);
        switch (type) {
          case IntValue:
            int intValue = rs_findValue.getInt("IntValue");
            result = new VariousValue(valueId, type.getTypeIndex(), new Integer(intValue));
            break;
          case RealValue:
            double realValue = rs_findValue.getDouble("RealValue");
            result = new VariousValue(valueId, type.getTypeIndex(), new Double(realValue));
            break;
          case StringValue:
            String stringValue = rs_findValue.getString("StringValue");
            result = new VariousValue(valueId, type.getTypeIndex(), stringValue);
            break;
          case DateValue:
            Date dateValue = rs_findValue.getDate("DateValue");
            result = new VariousValue(valueId, type.getTypeIndex(), dateValue);
            break;
          //TODO
          //object, clob, cclob
        }

      }
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      ObjectSemaphore.release(findValue);
    }
    return result;
  }

  @Override
  public Long storeValue(ValueType fieldType, final Object value) throws SQLException {
    if (callStoredValue == null) {
      callStoredValue = ConnectionManager.getInstance().getTxConnection().prepareCall(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "callStoreValue.sql", "cp1250"));
      callStoredValue.setQueryTimeout(15);
    }

    int pos = 0;
    FieldValue[] fieldValues = new FieldValue[7];
    fieldValues[pos++] = new FieldValue("FieldType", Types.INTEGER, new Integer(fieldType.getTypeIndex()));
    java.sql.ResultSet rs;
    Long newValueId = null;
    if (value != null) {
      switch (fieldType) {
        case BitValue:
        case IntValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, value);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.VARCHAR, null);
          break;
        case RealValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, value);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.VARCHAR, null);
          break;
        case StringValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, value);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.VARCHAR, null);
          break;
        case DateTimeValue:
        case MonthValue:
        case TimeValue:
        case DateValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, value);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.VARCHAR, null);
          break;
        case ObjectValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, value);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.VARCHAR, null);
          break;
        case ClobValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.VARCHAR, value);
          break;
      }
      if (newValueId == null) {
        try {
          ObjectSemaphore.aquire(callStoredValue);

          execute(callStoredValue, fieldValues);

          ResultSet resultSet = callStoredValue.getResultSet();
          if (resultSet != null) {
            try {
              if (resultSet.next()) {
                newValueId = resultSet.getLong("Id");
              }
            } finally {
              resultSet.close();
            }
          }
        } catch (InterruptedException ex) {
          Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
          ObjectSemaphore.release(callStoredValue);
        }
      }
    }
    return newValueId;
  }

  public boolean storeOpomba(Long eventId, String opomba) throws SQLException {
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (insertEventsOpombe == null) {
      insertEventsOpombe = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insert_EventsOpombe.sql", "cp1250"));
    }
    if (findOpomba == null) {
      findOpomba = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_opomba.sql", "cp1250"));
    }

    int param = 1;
    boolean success = true;
    boolean saveOpomba = true;

    try {
      ObjectSemaphore.aquire(insertEventsOpombe, findOpomba);

      param = 1;
      findOpomba.clearParameters();
      findOpomba.setLong(param++, eventId);
      ResultSet rsFindOpomba = findOpomba.executeQuery();
      if (rsFindOpomba.next()) {
        Clob oldOpombaClob = rsFindOpomba.getClob("ClobValue");
        if (oldOpombaClob != null) {
          String oldOpomba = oldOpombaClob.getSubString(1L, (int) oldOpombaClob.length());
          if (oldOpomba.equals(opomba)) {
            //opomba se ni spemenila
            saveOpomba = false;
          } else {
            //update event in opomba se je spremenila
          }
        }
      }

      if (saveOpomba) {
        if (opomba == null) {
          return true;
        }
        Long opombaId = storeValue(ValueType.ClobValue, opomba);
        param = 1;
        insertEventsOpombe.clearParameters();
        if (eventId == null) {
          return false;
        } else {
          insertEventsOpombe.setLong(param++, eventId.longValue());
        }
        if (opombaId == null) {
          //null ni dovoljeno, zato bo vrgel sql napako
          return false;
        } else {
          insertEventsOpombe.setLong(param++, opombaId.longValue());
        }
        success = success && insertEventsOpombe.executeUpdate() > 0;
      }
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      ObjectSemaphore.release(insertEventsOpombe, findOpomba);
    }
    return success;
  }

  @Override
  public DbDataSource getDsSifrantModel(String dataBase, List<Object> parameters) throws SQLException {
    DbDataSource dsSifrant = new DbDataSource();

    dsSifrant.setCanAddRows(false);
    dsSifrant.setCanDeleteRows(false);
    dsSifrant.setReadOnly(true);

    java.util.List params = new java.util.ArrayList();
    params.add(parameters.get(0));

    DbDataSource.SubstSqlParameter tb_sifranti = new DbDataSource.SubstSqlParameter("<%tb_sifranti%>");
    if ((dataBase == null) || (dataBase.length() == 0)) {
      tb_sifranti.setValue("");
    } else {
      tb_sifranti.setValue(dataBase + ".[Sifranti] AS ");
    }
    params.add(tb_sifranti);
    DbDataSource.SubstSqlParameter tb_seznam_sifrantov = new DbDataSource.SubstSqlParameter("<%tb_seznam_sifrantov%>");
    if ((dataBase == null) || (dataBase.length() == 0)) {
      tb_seznam_sifrantov.setValue("");
    } else {
      tb_seznam_sifrantov.setValue(dataBase + ".[SeznamSifrantov] AS ");
    }
    params.add(tb_seznam_sifrantov);
    for (int i = 1; i < parameters.size(); i++) {
      params.add(parameters.get(i));
    }

    dsSifrant.setParameters(params);
    dsSifrant.setCountSql(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "sifrant_c.sql", "cp1250"));
    dsSifrant.setSelectSql(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "sifrant.sql", "cp1250"));
    dsSifrant.setQueuedDelay(0);

    return dsSifrant;
  }

  @Override
  public DbNaslovDataModel.Naslov storeAddress(DbNaslovDataModel.Naslov address) throws SQLException {

    SqlUtilities sqlUtility = SqlUtilities.getInstance();
    Connection connection = ConnectionManager.getInstance().getTxConnection();
    int param;

    if (address.getHsMID() == null || address.getHsMID().getValue() == null) {
      param = 1;
      if (findHsNeznanaId == null) {
        findHsNeznanaId = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "findHsNeznanaId.sql", "cp1250"));
      }

//      findHsNeznanaId.clearParameters();
//      findHsNeznanaId.setInt(param++, (Integer.valueOf(address.getPostnaStevilka().getValue().toString())));
//      findHsNeznanaId.setString(param++, (String) address.getPosta().getValue());
//      findHsNeznanaId.setString(param++, (String) address.getNaselje().getValue());
//      findHsNeznanaId.setString(param++, (String) address.getUlica().getValue());
//      findHsNeznanaId.setInt(param++, (Integer.valueOf(address.getHisnaStevilka().getValue().toString())));
//      findHsNeznanaId.setString(param++, (String) address.getHisnaStevilkaDodatek().getValue());
//
//
//      findHsNeznanaId.executeQuery();
      try {
        ObjectSemaphore.aquire(findHsNeznanaId, insertNeznaniNaslov);

        ResultSet rsFindHsNeznanaId = executeQuery(findHsNeznanaId,
                address.getPostnaStevilka(),
                address.getPosta(),
                address.getNaselje(),
                address.getUlica(),
                address.getHisnaStevilka(),
                address.getHisnaStevilkaDodatek());

        Long hs_neznana_id = null;

        if (rsFindHsNeznanaId.next()) {
          hs_neznana_id = rsFindHsNeznanaId.getLong(1);
          if (rsFindHsNeznanaId.wasNull()) {
            hs_neznana_id = null;
          }
        } else {
          if (insertNeznaniNaslov == null) {
            insertNeznaniNaslov = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insertNeznaniNaslov.sql", "cp1250"));
          }
          boolean commit = false;
          boolean isTransaction = isTransaction();
          if (!isTransaction) {
            sqlUtility.beginTransaction();
          }
          try {
            param = 1;
            insertNeznaniNaslov.clearParameters();


            insertNeznaniNaslov.setObject(param++, address.getPostnaStevilkaMID().getValue(), java.sql.Types.INTEGER);
            insertNeznaniNaslov.setObject(param++, address.getPostnaStevilka().getValue(), java.sql.Types.INTEGER);
            insertNeznaniNaslov.setObject(param++, address.getPosta().getValue().toString().toUpperCase(), java.sql.Types.VARCHAR); //pt_ime
            insertNeznaniNaslov.setObject(param++, address.getPosta().getValue(), java.sql.Types.VARCHAR);  //pt_uime

            insertNeznaniNaslov.setObject(param++, address.getNaseljeMID().getValue(), java.sql.Types.INTEGER);
            if ((address.getNaselje() == null) || (address.getNaselje().getValue() == null)) {
              insertNeznaniNaslov.setNull(param++, java.sql.Types.VARCHAR);
              insertNeznaniNaslov.setNull(param++, java.sql.Types.VARCHAR);
            } else {
              insertNeznaniNaslov.setObject(param++, address.getNaselje().getValue().toString().toUpperCase(), java.sql.Types.VARCHAR); //na_ime
              insertNeznaniNaslov.setObject(param++, address.getNaselje().getValue(), java.sql.Types.VARCHAR); //na_uime
            }

            insertNeznaniNaslov.setObject(param++, address.getUlicaMID().getValue(), java.sql.Types.INTEGER);
            insertNeznaniNaslov.setObject(param++, address.getUlica().getValue().toString().toUpperCase(), java.sql.Types.VARCHAR); //ul_ime
            insertNeznaniNaslov.setObject(param++, address.getUlica().getValue(), java.sql.Types.VARCHAR); //ul_uime

            if (address.getHisnaStevilka() == null) {
              insertNeznaniNaslov.setNull(param++, java.sql.Types.INTEGER);
            } else {
              insertNeznaniNaslov.setObject(param++, address.getHisnaStevilka().getValue(), java.sql.Types.INTEGER); //ul_uime
            }
            if (address.getHisnaStevilkaDodatek() == null) {
              insertNeznaniNaslov.setNull(param++, java.sql.Types.VARCHAR);
            } else {
              insertNeznaniNaslov.setObject(param++, address.getHisnaStevilkaDodatek().getValue(), java.sql.Types.VARCHAR); //ul_uime
            }
            insertNeznaniNaslov.setInt(param++, address.getIzvor());


            commit = insertNeznaniNaslov.executeUpdate() > 0;

          } finally {
            if (commit) {
              if (isTransaction) {
                sqlUtility.endTransaction(commit);
              }

              hs_neznana_id = sqlUtility.getLastIdentity();
            } else {
              JOptionPane.showMessageDialog(null, "Napaka pri shranjevanju neznanega naslova!", "Napaka", JOptionPane.ERROR_MESSAGE);
            }
          }
        }
        address.setHsNeznanaMID(hs_neznana_id);
      } catch (InterruptedException ex) {
        Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
        ObjectSemaphore.release(findHsNeznanaId, insertNeznaniNaslov);
      }
    }

    return address;
  }

  public Event findEvent(Long eventId) throws SQLException {
    if (findEventById == null) {
      findEventById = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_event_by_id.sql", "cp1250"));
    }
    try {
      ObjectSemaphore.aquire(findEventById);

      ResultSet rs = executeQuery(findEventById, new FieldValue("ID", java.sql.Types.BIGINT, eventId));
      try {
        if (rs.next()) {
          Event result = new Event(rs.getInt("IdSifranta"),
                  rs.getString("IdSifre"));
          List<Field> primaryKey = new ArrayList<Field>();
          result.setId(eventId);
          result.setEventSource(rs.getInt("IdEventSource"));
          result.setDatum(rs.getTimestamp("Datum"));
          java.sql.Clob opomba = rs.getClob("Opomba");
          if (!rs.wasNull()) {
            if (opomba.length() > 0) {
              result.setOpomba(opomba.getSubString(1L, (int) opomba.length()));
            } else {
              result.setOpomba(null);
            }
          }
          do {
            FieldValue fv = null;
            switch (rs.getInt("FieldType")) {
              case 1:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.INTEGER, rs.getInt("FieldValueIndex"), rs.getInt("IntValue")));
                break;
              case 2:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.DOUBLE, rs.getInt("FieldValueIndex"), rs.getDouble("RealValue")));
                break;
              case 3:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.VARCHAR, rs.getInt("FieldValueIndex"), rs.getString("StringValue")));
                break;
              case 4:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.TIMESTAMP, rs.getInt("FieldValueIndex"), rs.getTimestamp("DateValue")));
                break;
              case 5:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.BLOB, rs.getInt("FieldValueIndex"), rs.getBlob("ObjectValue")));
                break;
              case 6:
                java.sql.Clob value = rs.getClob("ClobValue");
                if ((value != null) && (value.length() > 0)) {
                  result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.VARCHAR, rs.getInt("FieldValueIndex"), value.getSubString(1L, (int) value.length())));
                } else {
                  result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.VARCHAR, rs.getInt("FieldValueIndex"), ""));
                }
                break;
              case 7:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.BOOLEAN, rs.getInt("FieldValueIndex"), rs.getInt("IntValue") != 0));
                break;
              case 8:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.TIMESTAMP, rs.getInt("FieldValueIndex"), rs.getTimestamp("DateValue")));
                break;
              case 9:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.TIME, rs.getInt("FieldValueIndex"), rs.getTime("DateValue")));
                break;
              case 10:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.DATE, rs.getInt("FieldValueIndex"), rs.getDate("DateValue")));
                break;
            }
            if (fv != null && rs.getBoolean("PrimaryKey")) {
              primaryKey.add(new Field(fv));
            }
          } while (rs.next());
          if (primaryKey.size() > 0) {
            result.setPrimaryKey(primaryKey.toArray(new Field[primaryKey.size()]));
          }
          return result;
        } else {
          return null;
        }
      } finally {
        rs.close();
      }
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    } finally {
      ObjectSemaphore.release(findEventById);
    }

  }

  @Override
  public MaterializedView getCacheDefinition(String table) {
    MaterializedView result = new MaterializedView();
    result.setValue("[MViewCache].[dbo].[" + table + "]");
    result.setIsViewValidSql(com.openitech.text.Document.identText(
            "\nSELECT [MViewCache].[DBO].[isValidCachedObject]\n"
            + "    ('" + table + "'\n"
            + "     ,NULL\n"
            + "     ,NULL)", 15));
    result.setSetViewVersionSql("EXECUTE [MViewCache].[dbo].[updateRefreshDate] '" + table + "'");
    return result;
  }

  @Override
  public Map<String, TemporaryTable> getCachedTemporaryTables() {
    Map<String, TemporaryTable> result = new HashMap<String, TemporaryTable>();
    try {
      Statement statement = ConnectionManager.getInstance().getConnection().createStatement();
      ResultSet cachedObjects = statement.executeQuery(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "getCachedTemporaryTables.sql", "cp1250"));

      while (cachedObjects.next()) {
        if (cachedObjects.getObject("CachedObjectXML") != null) {
          String object = cachedObjects.getString("Object");
          com.openitech.db.model.xml.config.CachedTemporaryTable temporaryTable;

          try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(com.openitech.db.model.xml.config.CachedTemporaryTable.class).createUnmarshaller();
            temporaryTable = (com.openitech.db.model.xml.config.CachedTemporaryTable) unmarshaller.unmarshal(cachedObjects.getClob("CachedObjectXML").getCharacterStream());

            result.put(object, temporaryTable.getTemporaryTable());
          } catch (JAXBException ex) {
            Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.WARNING, ex.getMessage());
          }
        }
      }
    } catch (SQLException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    }

    return result;
  }

  @Override
  public void storeCachedTemporaryTable(TemporaryTable tt) {
    if (tt.getMaterializedView() != null) {
      try {
        JAXBContext context = JAXBContext.newInstance(com.openitech.db.model.xml.config.CachedTemporaryTable.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();

        com.openitech.db.model.xml.config.CachedTemporaryTable ctt = new com.openitech.db.model.xml.config.CachedTemporaryTable();
        ctt.setTemporaryTable(tt);

        marshaller.marshal(ctt, sw);
        final Connection connection = ConnectionManager.getInstance().getTxConnection();
        if (storeCachedTemporaryTable == null) {
          storeCachedTemporaryTable = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "storeCachedTemporaryTable.sql", "cp1250"));
        }

        try {
          ObjectSemaphore.aquire(storeCachedTemporaryTable);
          storeCachedTemporaryTable.setString(1, tt.getMaterializedView().getValue());
          storeCachedTemporaryTable.setString(2, sw.toString());
          storeCachedTemporaryTable.executeUpdate();
        } catch (InterruptedException ex) {
          Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
          ObjectSemaphore.release(storeCachedTemporaryTable);
        }
      } catch (SQLException ex) {
        Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
      } catch (JAXBException ex) {
        Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
      }

    }
  }

  @Override
  public boolean storePrimaryKey(EventPK eventPK) throws SQLException {
    boolean success = false;
    int param = 1;

    final long eventId = eventPK.getEventId();
    final int idSifranta = eventPK.getIdSifranta();
    final String idSifre = eventPK.getIdSifre();
    final String primaryKey = eventPK.toHexString();
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (insert_eventPK == null) {
      insert_eventPK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insert_eventPK.sql", "cp1250"));
    }
    if (update_eventPK == null) {
      update_eventPK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "update_eventPK.sql", "cp1250"));
    }
    if (delete_eventPK == null) {
      delete_eventPK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "delete_eventPK.sql", "cp1250"));
    }


    try {
      ObjectSemaphore.aquire(insert_eventPK, update_eventPK, delete_eventPK);

      try {
        if (findEventPK(eventId) != null) {
          if (eventPK.getEventOperation().equals(Event.EventOperation.DELETE)) {
            param = 1;
            delete_eventPK.clearParameters();
            delete_eventPK.setLong(param++, eventId);
            success = delete_eventPK.executeUpdate() > 0;
          } else {
            //update
            param = 1;
            update_eventPK.clearParameters();
            update_eventPK.setInt(param++, idSifranta);
            update_eventPK.setString(param++, idSifre);
            update_eventPK.setString(param++, primaryKey);
            update_eventPK.setLong(param++, eventId);

            success = update_eventPK.executeUpdate() > 0;
          }
        } else {
          //insert
          param = 1;

          insert_eventPK.clearParameters();
          insert_eventPK.setLong(param++, eventId);
          insert_eventPK.setInt(param++, idSifranta);
          insert_eventPK.setString(param++, idSifre);
          insert_eventPK.setString(param++, primaryKey);
          success = insert_eventPK.executeUpdate() > 0;
        }
      } catch (SQLException ex) {
        // System.out.println("Najden je podvojeni zapis za dogodek E:" + idSifranta + '-' + idSifre + ".\nShranjevanje neuspešno!\n\nPK:" + eventPK.toNormalString());
        throw new SQLPrimaryKeyException("Najden je podvojeni zapis za dogodek E:" + eventId + "-" + idSifranta + '-' + idSifre + ".\nShranjevanje neuspešno!\n\nPK:" + eventPK.toHexString(), ex, eventPK);
      }
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      ObjectSemaphore.release(insert_eventPK, update_eventPK, delete_eventPK);
    }
    return success;
  }

  @Override
  public boolean storePrimaryKeyVersions(EventPK eventPK) throws SQLException {

    boolean success = true;
    int param = 1;

    final long eventId = eventPK.getEventId();
    final int idSifranta = eventPK.getIdSifranta();
    final String idSifre = eventPK.getIdSifre();
    final String primaryKey = eventPK.toHexString();
    final Integer versionId = eventPK.getVersionID();

    final Connection connection = ConnectionManager.getInstance().getTxConnection();

    if (primaryKey != null && primaryKey.length() > 0) {
      ///////////versions
      if (insert_eventPK_versions == null) {
        insert_eventPK_versions = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insert_eventPK_versions.sql", "cp1250"));
      }

      if (update_eventPK_versions == null) {
        update_eventPK_versions = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "update_eventPK_versions.sql", "cp1250"));
      }

      try {
        ObjectSemaphore.aquire(insert_eventPK_versions, update_eventPK_versions);

        if (findEventPKVersions(eventId, versionId) != null) {
          //if (versionId == null) {
          param = 1;
          update_eventPK_versions.clearParameters();

          update_eventPK_versions.setInt(param++, idSifranta);
          update_eventPK_versions.setString(param++, idSifre);
          update_eventPK_versions.setString(param++, primaryKey);
          update_eventPK_versions.setLong(param++, eventId);
          if (versionId == null) {
            update_eventPK_versions.setInt(param++, -1);
            update_eventPK_versions.setInt(param++, 1);

          } else {
            update_eventPK_versions.setInt(param++, versionId.intValue());
            update_eventPK_versions.setInt(param++, 0);
          }
          update_eventPK_versions.executeUpdate();
          success = update_eventPK_versions.executeUpdate() > 0;
          // }
        } else {

          //insert
          param = 1;

          insert_eventPK_versions.clearParameters();
          insert_eventPK_versions.setLong(param++, eventId);
          if (versionId == null) {
            insert_eventPK_versions.setNull(param++, java.sql.Types.INTEGER);
          } else {
            insert_eventPK_versions.setInt(param++, versionId.intValue());
          }
          insert_eventPK_versions.setInt(param++, idSifranta);
          insert_eventPK_versions.setString(param++, idSifre);
          insert_eventPK_versions.setString(param++, primaryKey);
          System.out.println(eventId + "," + versionId + "," + idSifranta + "," + idSifre + "," + primaryKey);
          success = success && insert_eventPK_versions.executeUpdate() > 0;
        }
      } catch (InterruptedException ex) {
        Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
        ObjectSemaphore.release(insert_eventPK_versions, update_eventPK_versions);
      }
    }
    return success;
  }

  @Override
  public EventPK findEventPK(long eventId) throws SQLException {
    EventPK result = null;
    final Connection connection = ConnectionManager.getInstance().getTxConnection();

    if (find_eventPK == null) {
      find_eventPK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_eventPK.sql", "cp1250"));
    }

    try {
      ObjectSemaphore.aquire(find_eventPK);

      int param = 1;
      find_eventPK.clearParameters();
      find_eventPK.setLong(param++, eventId);
      ResultSet rs_findEventPK = find_eventPK.executeQuery();
      if (rs_findEventPK.next()) {
        int idSifranta = rs_findEventPK.getInt("IdSifranta");
        String idSifre = rs_findEventPK.getString("IdSifre");
        String primaryKey = rs_findEventPK.getString("PrimaryKey");
        result = new SqlEventPK(eventId, idSifranta, idSifre, primaryKey);
      }
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      ObjectSemaphore.release(find_eventPK);
    }
    return result;
  }

  @Override
  public EventPK findEventPKVersions(long eventId, Integer versionId) throws SQLException {
    EventPK result = null;
    final Connection connection = ConnectionManager.getInstance().getTxConnection();

    if (find_eventPK_versions == null) {
      find_eventPK_versions = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_eventPK_versions.sql", "cp1250"));
    }

    try {
      ObjectSemaphore.aquire(find_eventPK_versions);


      int param = 1;
      find_eventPK_versions.clearParameters();
      find_eventPK_versions.setLong(param++, eventId);
      find_eventPK_versions.setInt(param++, versionId == null ? 1 : 0);
      find_eventPK_versions.setInt(param++, versionId != null ? versionId.intValue() : -1);
      ResultSet rs_findEventPK = find_eventPK_versions.executeQuery();
      if (rs_findEventPK.next()) {
        Integer eventsPK_versionId = rs_findEventPK.getInt("VersionId");
        if (rs_findEventPK.wasNull()) {
          eventsPK_versionId = null;
        }
        int eventsPK_idSifranta = rs_findEventPK.getInt("IdSifranta");
        String eventsPK_idSifre = rs_findEventPK.getString("IdSifre");
        String eventsPK_primaryKey = rs_findEventPK.getString("PrimaryKey");

        result = new SqlEventPK(eventId, eventsPK_idSifranta, eventsPK_idSifre, eventsPK_primaryKey, eventsPK_versionId);
      }
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      ObjectSemaphore.release(find_eventPK_versions);
    }
    return result;
  }

  @Override
  public EventPK findEventPKVersions(Integer versionId, Integer idSifranta, String idSifre, String primaryKey) throws SQLException {
    EventPK result = null;
    final Connection connection = ConnectionManager.getInstance().getTxConnection();

    if (find_eventPK_versions_byValues == null) {
      find_eventPK_versions_byValues = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_eventPK_versions_byValues.sql", "cp1250"));
    }
    try {
      ObjectSemaphore.aquire(find_eventPK_versions_byValues);

      int param = 1;
      find_eventPK_versions_byValues.clearParameters();
      find_eventPK_versions_byValues.setInt(param++, versionId == null ? 1 : 0);
      find_eventPK_versions_byValues.setInt(param++, versionId != null ? versionId.intValue() : -1);
      find_eventPK_versions_byValues.setInt(param++, idSifranta);
      find_eventPK_versions_byValues.setString(param++, idSifre);
      find_eventPK_versions_byValues.setString(param++, primaryKey);

      ResultSet rs_findEventPK = find_eventPK_versions_byValues.executeQuery();
      if (rs_findEventPK.next()) {
        Integer eventsPK_eventId = rs_findEventPK.getInt("EventId");
        if (rs_findEventPK.wasNull()) {
          eventsPK_eventId = null;
        }

        result = new SqlEventPK(eventsPK_eventId, idSifranta, idSifre, primaryKey, versionId);
      }
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      ObjectSemaphore.release(find_eventPK_versions_byValues);
    }
    return result;
  }

  @Override
  public boolean deleteEvent(long eventId) throws SQLException {
    boolean success = false;
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (delete_event == null) {
      delete_event = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "delete_event.sql", "cp1250"));
    }
    try {
      ObjectSemaphore.aquire(delete_event);

      int param = 1;
      delete_event.clearParameters();
      delete_event.setLong(param++, eventId);
      success = delete_event.executeUpdate() > 0;
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      ObjectSemaphore.release(delete_event);
    }
    return success;
  }

  @Override
  public String getPPJoinFields() {
    String result = "";
    try {
      PreparedStatement findPPPolja = ConnectionManager.getInstance().getConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "findPPPolja.sql", "cp1250"), java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
      ResultSet rsFindPPPolja = findPPPolja.executeQuery();

      StringBuilder sbSelect = new StringBuilder(200);
      rsFindPPPolja.beforeFirst();
      while (rsFindPPPolja.next()) {
        int idPolja = rsFindPPPolja.getInt("IdPolja");
        String imePolja = rsFindPPPolja.getString("ImePolja");
        final String ev_alias = "[ev_" + imePolja + "]";
        final String val_alias = "[val_" + imePolja + "]";
        sbSelect.append("\nLEFT OUTER JOIN ").append(SqlUtilities.getDataBase()).append(".[dbo].[ValuesPP] ").append(ev_alias).append(" ON (");
        sbSelect.append("PP.[PPID] = ").append(ev_alias).append(".[PPId]");
        sbSelect.append(" AND ").append(ev_alias).append(".[IdPolja] = ").append(idPolja).append(")");
        sbSelect.append("\nLEFT OUTER JOIN ").append(SqlUtilities.getDataBase()).append(".[dbo].[VariousValues] ").append(val_alias).append(" ON (");
        sbSelect.append(ev_alias).append(".[ValueId] = ").append(val_alias).append(".[Id] )");
      }
      result = sbSelect.toString();
    } catch (SQLException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);

    }
    return result;
  }

  @Override
  public String getPPSelectFields() {
    String result = "";
    try {
      StringBuilder sbresult = new StringBuilder(200);
      PreparedStatement findPPPolja = ConnectionManager.getInstance().getConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "findPPPolja.sql", "cp1250"), java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
      ResultSet rsFindPPPolja = findPPPolja.executeQuery();
      while (rsFindPPPolja.next()) {
        String imePolja = rsFindPPPolja.getString("ImePolja");
        final String val_alias = "[val_" + imePolja + "]";
        int tipPolja = rsFindPPPolja.getInt("TipPolja");
        switch (tipPolja) {
          case 1:
            sbresult.append(",\n").append(val_alias).append(".IntValue AS [").append(imePolja).append("]");
            break;
          case 2:
            //Real
            sbresult.append(",\n").append(val_alias).append(".RealValue AS [").append(imePolja).append("]");
            break;
          case 3:
            //String
            sbresult.append(",\n").append(val_alias).append(".StringValue AS [").append(imePolja).append("]");
            break;
          case 4:
            //Date
            sbresult.append(",\n").append(val_alias).append(".DateValue AS [").append(imePolja).append("]");
            break;
          case 6:
            //Clob
            sbresult.append(",\n").append(val_alias).append(".ClobValue AS [").append(imePolja).append("]");
            break;
          case 7:
            //Boolean
            sbresult.append(",\n").append("CAST(").append(val_alias).append(".IntValue AS BIT) AS [").append(imePolja).append("]");
        }
      }
      result = sbresult.toString();
    } catch (SQLException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  private boolean storeVeljavnost(Long events_ID, Long veljavnost) throws SQLException {
    boolean success = true;

    if (insertScheduler == null) {
      insertScheduler = ConnectionManager.getInstance().getConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "insertScheduler.sql", "cp1250"));
    }

    try {
      ObjectSemaphore.aquire(insertScheduler);

      int param = 1;
      insertScheduler.clearParameters();

      insertScheduler.setLong(param++, veljavnost);
      insertScheduler.setLong(param++, events_ID);
      success = success && insertScheduler.executeUpdate() > 0;
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      ObjectSemaphore.release(insertScheduler);
    }
    return success;
  }

  private static class EventQueryKey {

    public EventQueryKey(Event event) {
      this.sifrant = event.getSifrant();
      this.sifra = event.getSifra();
      java.util.Set<Field> pk = new java.util.HashSet<Field>();
      if (event.getPrimaryKey() != null) {
        pk.addAll(Arrays.asList(event.getPrimaryKey()));
      } else {
        pk.addAll(event.getEventValues().keySet());
        //primaryKey.add(Event.EVENT_DATE);
        pk.add(Event.EVENT_SOURCE);
      }
      primaryKey = pk.toArray(new Field[pk.size()]);
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
    private PreparedStatement query;

    /**
     * Get the value of query
     *
     * @return the value of query
     */
    public PreparedStatement getQuery() {
      return query;
    }

    /**
     * Set the value of query
     *
     * @param query new value of query
     */
    public void setQuery(PreparedStatement query) {
      this.query = query;
    }
    private EventQuery eventQuery;

    /**
     * Get the value of eventQuery
     *
     * @return the value of eventQuery
     */
    public EventQuery getEventQuery() {
      return eventQuery;
    }

    /**
     * Set the value of eventQuery
     *
     * @param eventQuery new value of eventQuery
     */
    public void setEventQuery(EventQuery eventQuery) {
      this.eventQuery = eventQuery;
    }

    public ResultSet executeQuery(Event event) throws SQLException {
      List<FieldValue> parameters = new ArrayList<FieldValue>();
      for (Field f : this.primaryKey) {
        List<FieldValue> values;
        if (event.getEventValues().containsKey(f)) {
          values = event.getEventValues().get(f);
        } else {
          values = new java.util.ArrayList<FieldValue>();
          values.add(new FieldValue(f));
        }

        parameters.addAll(values);
      }

      return executeQuery(parameters);
    }

    public ResultSet executeQuery(List<FieldValue> fields) throws SQLException {
      for (FieldValue field : fields) {
        if (eventQuery.getNamedParameters().containsKey(field)) {
          eventQuery.getNamedParameters().get(field).setValue(field.getValue());
        }
      }

      return SQLDataSource.executeQuery(this.query, eventQuery.getParameters());
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final EventQueryKey other = (EventQueryKey) obj;
      if (this.sifrant != other.sifrant) {
        return false;
      }
      if ((this.sifra == null) ? (other.sifra != null) : !this.sifra.equals(other.sifra)) {
        return false;
      }
      if (!Arrays.deepEquals(this.primaryKey, other.primaryKey)) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 3;
      hash = 97 * hash + this.sifrant;
      hash = 97 * hash + (this.sifra != null ? this.sifra.hashCode() : 0);
      hash = 97 * hash + Arrays.deepHashCode(this.primaryKey);
      return hash;
    }
  }
  private Map<EventQueryKey, EventQueryKey> findEventStatements = new HashMap<EventQueryKey, EventQueryKey>();

  @Override
  public Event findEvent(Event event) throws SQLException {
    if ((event.getId() == null) || (event.getId() <= 0)) {
      EventQueryKey eqk = new EventQueryKey(event);
      boolean seek = false;

      if (findEventStatements.containsKey(eqk)) {
        eqk = findEventStatements.get(eqk);
        seek = true;
      } else {
        java.util.Set<Field> primaryKey = new java.util.HashSet<Field>();
        if (event.getPrimaryKey() != null) {
          primaryKey.addAll(Arrays.asList(event.getPrimaryKey()));
        } else {
          primaryKey.addAll(event.getEventValues().keySet());
          //primaryKey.add(Event.EVENT_DATE);
          primaryKey.add(Event.EVENT_SOURCE);
        }

        EventQuery eq = prepareEventQuery(event, primaryKey, new java.util.HashSet<Field>());
        int valuesSet = eq.getValuesSet();


        if (event.getPrimaryKey() != null) {
          //vedno iscemo po PK, cetudi se dogodek pojavlja v polju z vec vrednostmi
          //ce niso bile nastavljene vse vrednosti PK-ja potem ne iscemo
          seek = valuesSet == primaryKey.size();
        } else {
          java.util.List parametersVecVrednosti = new java.util.ArrayList<Object>();
          parametersVecVrednosti.add(event.getSifrant());
          parametersVecVrednosti.add(event.getSifra());

          ResultSet rsVecVrednosti = SQLDataSource.executeQuery(getCheckVecVrednostiEventSQL(), parametersVecVrednosti, ConnectionManager.getInstance().getTxConnection());
          if (rsVecVrednosti.next()) {
            if (rsVecVrednosti.getInt(1) > 0) {
              seek = false;
            }
          }
        }

        if (seek) {
          String sql = SQLDataSource.substParameters(eq.getQuery(), eq.getParameters());
          Logger.getLogger(getClass().getName()).log(Level.INFO, "Searching event : \n {0}", sql);
          PreparedStatement statement = ConnectionManager.getInstance().getTxConnection().prepareStatement(sql,
                  ResultSet.TYPE_SCROLL_INSENSITIVE,
                  ResultSet.CONCUR_READ_ONLY,
                  ResultSet.HOLD_CURSORS_OVER_COMMIT);

          statement.setQueryTimeout(1800);

          eqk.setEventQuery(eq);
          eqk.setQuery(statement);

          findEventStatements.put(eqk, eqk);
        }
      }

      if (seek) {
        ResultSet rs = eqk.executeQuery(event);
        try {
          if (rs.next()) {
            event.setId(rs.getLong("Id"));
            return findEvent(event.getId());
          } else {
            return null;
          }
        } finally {
          rs.close();
        }
      } else {
        return null;
      }
    } else {
      return findEvent(event.getId());
    }
  }

  private static class GeneratedFieldFactory extends DataSourceFactory {

    private static final boolean CACHED_GGF = true;
    private static final boolean USE_INDEXED_CACHE = true;
    private final boolean DEVELOPMENT;
    private static GeneratedFieldFactory instance;
    public PreparedStatement getGeneratedFields;
    private DbDataSource dsGeneratedFields = null;
    private DbDataSourceIndex dbxAllVisibleGeneratedFields = null;
    private DbDataSourceIndex dbxAllGeneratedFields = null;
    private DbDataSourceIndex dbxAllVisibleGeneratedFieldsSifrant = null;
    private DbDataSourceIndex dbxAllGeneratedFieldsSifrant = null;
    private CachedRowSet crsAllGeneratedFields = null;
    private DataSourceFilters dsGeneratedFieldsFilter = new DataSourceFilters("<%GENERATED_FILTERS%>");
    private TemporarySubselectSqlParameter ttGeneratedFields;
    private java.util.List<Object> ttGeneratedFieldsParameters = new ArrayList<Object>();
    private DataSourceFilters.IntegerSeekType I_TYPE_ID_SIFANTA = new DataSourceFilters.IntegerSeekType("GeneratedFields.IdSifranta", DataSourceFilters.IntegerSeekType.EQUALS);
    private DataSourceFilters.SeekType I_TYPE_ID_SIFRE = new DataSourceFilters.SeekType("GeneratedFields.IdSifre", DataSourceFilters.SeekType.EQUALS, 1);
    private DataSourceFilters.IntegerSeekType I_TYPE_HIDDEN = new DataSourceFilters.IntegerSeekType("GeneratedFields.Hidden", DataSourceFilters.IntegerSeekType.EQUALS);
    private DbDataSource.SubstSqlParameter S_ACTIVITY = new DbDataSource.SubstSqlParameter("<%ACTIVITY_ID%>");

    public GeneratedFieldFactory() {
      super(null);
      DEVELOPMENT = Boolean.parseBoolean(ConnectionManager.getInstance().getProperty("application.mode.development", "false"));
    }

    private static GeneratedFieldFactory getInstance() throws JAXBException, UnsupportedEncodingException, IOException, SQLException {
      if (instance == null) {
        instance = (new GeneratedFieldFactory()).init();
      }

      return instance;
    }

    private GeneratedFieldFactory init() throws JAXBException, UnsupportedEncodingException, IOException, SQLException {
      if (CACHED_GGF) {
        dsGeneratedFields = new DbDataSource();
        dsGeneratedFields.setQueuedDelay(0);
        dsGeneratedFields.setCacheRowSet(true);

        dsGeneratedFieldsFilter.setOperator("WHERE");
        dsGeneratedFieldsFilter.addRequired(I_TYPE_ID_SIFANTA);

        ttGeneratedFields = CachedTemporaryTablesManager.getInstance().getCachedTemporarySubselectSqlParameter(this.getClass(), "generatedFields.xml");

        java.util.List<Object> parameters = new ArrayList<Object>();

        parameters.add(ttGeneratedFields);
        parameters.add(dsGeneratedFieldsFilter);
        parameters.add(S_ACTIVITY);

        ttGeneratedFieldsParameters.add(dsGeneratedFieldsFilter);
        ttGeneratedFieldsParameters.add(S_ACTIVITY);

        dsGeneratedFields.setName("DS:GET_GENERATED_FIELDS");

        dsGeneratedFields.setParameters(parameters);
        dsGeneratedFields.setSelectSql(ReadInputStream.getResourceAsString(getClass(), "getGeneratedFieldsCached.sql", "cp1250"));

        if (USE_INDEXED_CACHE) {
          dsGeneratedFields.loadData();

          dbxAllVisibleGeneratedFields = new DbDataSourceIndex();
          dbxAllVisibleGeneratedFields.addKeys("IdSifranta", "IdSifre", "Hidden", "ActivityId");
          dbxAllVisibleGeneratedFields.setDataSource(dsGeneratedFields);

          dbxAllGeneratedFields = new DbDataSourceIndex();
          dbxAllGeneratedFields.addKeys("IdSifranta", "IdSifre", "ActivityId");
          dbxAllGeneratedFields.setDataSource(dsGeneratedFields);

          dbxAllVisibleGeneratedFieldsSifrant = new DbDataSourceIndex();
          dbxAllVisibleGeneratedFieldsSifrant.addKeys("IdSifranta", "Hidden", "ActivityId");
          dbxAllVisibleGeneratedFieldsSifrant.setDataSource(dsGeneratedFields);

          dbxAllGeneratedFieldsSifrant = new DbDataSourceIndex();
          dbxAllGeneratedFieldsSifrant.addKeys("IdSifranta", "ActivityId");
          dbxAllGeneratedFieldsSifrant.setDataSource(dsGeneratedFields);

          crsAllGeneratedFields = dsGeneratedFields.getCachedRowSet();
        }
      }

      return this;
    }

    public synchronized CachedRowSet getGeneratedFields(int idSifranta, String idSifre, boolean visibleOnly, ActivityEvent activityEvent) throws SQLException {
      if (CACHED_GGF) {
        CachedRowSet rs;
        if (USE_INDEXED_CACHE) {
          long start = System.currentTimeMillis();
          if (DEVELOPMENT && !ttGeneratedFields.getSqlMaterializedView().isViewValid(dsGeneratedFields.getConnection(), ttGeneratedFieldsParameters)) {
            dsGeneratedFields.reload();
            crsAllGeneratedFields = dsGeneratedFields.getCachedRowSet();
          }
          rs = new com.openitech.sql.rowset.CachedRowSetImpl();
          Set<Integer> rows;

          if (idSifre == null) {
            if (visibleOnly) {
              rows = dbxAllVisibleGeneratedFieldsSifrant.findRows(idSifranta, !visibleOnly, (activityEvent == null) ? null : activityEvent.getActivityId());
              if (rows == null || rows.isEmpty()) {
                rows = dbxAllVisibleGeneratedFieldsSifrant.findRows(idSifranta, !visibleOnly, null);
              }
            } else {
              rows = dbxAllGeneratedFieldsSifrant.findRows(idSifranta, (activityEvent == null) ? null : activityEvent.getActivityId());
              if (rows == null || rows.isEmpty()) {
                rows = dbxAllGeneratedFieldsSifrant.findRows(idSifranta, null);
              }
            }
          } else {
            if (visibleOnly) {
              rows = dbxAllVisibleGeneratedFields.findRows(idSifranta, idSifre, !visibleOnly, (activityEvent == null) ? null : activityEvent.getActivityId());
              if (rows == null || rows.isEmpty()) {
                rows = dbxAllVisibleGeneratedFields.findRows(idSifranta, idSifre, !visibleOnly, null);
              }
            } else {
              rows = dbxAllGeneratedFields.findRows(idSifranta, idSifre, (activityEvent == null) ? null : activityEvent.getActivityId());
              if (rows == null || rows.isEmpty()) {
                rows = dbxAllGeneratedFields.findRows(idSifranta, idSifre, null);
              }
            }
          }
          ((com.openitech.sql.rowset.CachedRowSetImpl) rs).populate(crsAllGeneratedFields, rows);

          long end = System.currentTimeMillis();
          System.out.println("getGeneratedFields::" + (end - start) + " ms.");

          return rs;
        } else {
          long start = System.currentTimeMillis();
          dsGeneratedFieldsFilter.setSeekValue(I_TYPE_ID_SIFANTA, idSifranta);
          dsGeneratedFieldsFilter.setSeekValue(I_TYPE_ID_SIFRE, idSifre);
          dsGeneratedFieldsFilter.setSeekValue(I_TYPE_HIDDEN, visibleOnly ? 0 : Integer.MIN_VALUE);
          if (activityEvent == null) {
            S_ACTIVITY.setValue("AND GeneratedFields.[ActivityId] IS NULL");
            S_ACTIVITY.clearParameters();
          } else {
            S_ACTIVITY.setValue("AND GeneratedFields.[ActivityId]=?");
            S_ACTIVITY.addParameter(activityEvent.getActivityId());
          }
          dsGeneratedFields.filterChanged();
          if ((activityEvent != null) && (dsGeneratedFields.getRowCount() == 0)) {
            S_ACTIVITY.setValue("AND GeneratedFields.[ActivityId] IS NULL");
            S_ACTIVITY.clearParameters();
            dsGeneratedFields.filterChanged();
          }

          rs = new com.sun.rowset.CachedRowSetImpl();
          rs.populate(dsGeneratedFields.getResultSet());
          long end = System.currentTimeMillis();
          System.out.println("getGeneratedFields::" + (end - start) + " ms.");


          return rs;
        }
      } else {
        if (getGeneratedFields == null) {
          getGeneratedFields = ConnectionManager.getInstance().getConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "getGeneratedFields.sql", "cp1250"), java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        }
        getGeneratedFields.clearParameters();
        int param = 1;
        if (activityEvent != null) {
          getGeneratedFields.setLong(param++, activityEvent.getActivityId());
          getGeneratedFields.setInt(param++, activityEvent.getIdSifranta());
          getGeneratedFields.setString(param++, activityEvent.getIdSifre());
          getGeneratedFields.setLong(param++, activityEvent.getActivityId());
          getGeneratedFields.setInt(param++, activityEvent.getIdSifranta());
          getGeneratedFields.setString(param++, activityEvent.getIdSifre());
        } else {
          getGeneratedFields.setNull(param++, java.sql.Types.INTEGER);
          getGeneratedFields.setNull(param++, java.sql.Types.INTEGER);
          getGeneratedFields.setNull(param++, java.sql.Types.VARCHAR);
          getGeneratedFields.setNull(param++, java.sql.Types.INTEGER);
          getGeneratedFields.setNull(param++, java.sql.Types.INTEGER);
          getGeneratedFields.setNull(param++, java.sql.Types.VARCHAR);
        }
        getGeneratedFields.setInt(param++, idSifranta);
        getGeneratedFields.setBoolean(param++, idSifre == null);
        getGeneratedFields.setString(param++, idSifre);
        getGeneratedFields.setBoolean(param++, !visibleOnly);
        CachedRowSet rs = new com.sun.rowset.CachedRowSetImpl();
        rs.populate(getGeneratedFields.executeQuery());

        return rs;
      }
    }
  }

  @Override
  public synchronized CachedRowSet getGeneratedFields(int idSifranta, String idSifre, boolean visibleOnly, ActivityEvent activityEvent) throws SQLException {
    CachedRowSet result = null;
    try {
      result = GeneratedFieldFactory.getInstance().getGeneratedFields(idSifranta, idSifre, visibleOnly, activityEvent);
    } catch (JAXBException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } catch (UnsupportedEncodingException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    }

    return result;
  }

  private Map<NamedFieldIds, NamedFieldIds> getEventFields(int idSifranta, String idSifre) throws SQLException {
    Map<NamedFieldIds, NamedFieldIds> result = new java.util.HashMap<NamedFieldIds, NamedFieldIds>();

    ResultSet rs = getGeneratedFields(idSifranta, idSifre);
    try {
      rs.beforeFirst();
      while (rs.next()) {
        result.put(new NamedFieldIds(rs.getString("ImePolja"), Long.MIN_VALUE), new NamedFieldIds(rs.getString("ImePolja"), rs.getLong("IdPolja")));
      }
    } finally {
      rs.close();
    }

    return result;
  }
  private final static Event SYSTEM_IDENTITIES = new Event(0, "ID01", -1);

  @Override
  public FieldValue getNextIdentity(Field field) throws SQLException {
    SYSTEM_IDENTITIES.setPrimaryKey(new Field[]{});
    Event system = findEvent(SYSTEM_IDENTITIES);
    system = system == null ? SYSTEM_IDENTITIES : system;
    List<FieldValue> get = system.getEventValues().get(field);
    if ((get == null) || (get.isEmpty())) {
      ValueType type = ValueType.getType(field.getType());
      FieldValue start = new FieldValue(field);

      switch (type) {
        case RealValue:
        case IntValue:
          start.setValue(1);
          break;
        case StringValue:
          start.setValue("AAA000000001");
          break;
        default:
          start = null;
      }

      if (start != null) {
        system.addValue(start);
        storeEvent(system);
      }

      return start;
    } else {
      FieldValue value = get.get(0);
      ValueType type = ValueType.getType(value.getType());

      switch (type) {
        case RealValue:
          value.setValue(((Number) value.getValue()).doubleValue() + 1);
          break;
        case IntValue:
          value.setValue(((Number) value.getValue()).longValue() + 1);
          break;
        case StringValue:
          value.setValue(StringValue.getNextSifra((String) value.getValue()));
          break;
        default:
          value = null;
      }

      if (value != null) {
        storeEvent(system);
      }

      return value;
    }
  }

  @Override
  public FieldValue getParentIdentity(Field field) throws SQLException {
    SYSTEM_IDENTITIES.setPrimaryKey(new Field[]{});
    Event system = findEvent(SYSTEM_IDENTITIES);
    system = system == null ? SYSTEM_IDENTITIES : system;
    List<FieldValue> get = system.getEventValues().get(field);
    if ((get == null) || (get.isEmpty())) {
      ValueType type = ValueType.getType(field.getType());
      FieldValue start = new FieldValue(field);

      switch (type) {
        case RealValue:
        case IntValue:
          start.setValue(1);
          break;
        case StringValue:
          start.setValue("AAA000000001");
          break;
        default:
          start = null;
      }

      return start;
    } else {
      FieldValue value = get.get(0);
      ValueType type = ValueType.getType(value.getType());

      switch (type) {
        case RealValue:
          value.setValue(((Number) value.getValue()).doubleValue());
          break;
        case IntValue:
          value.setValue(((Number) value.getValue()).longValue());
          break;
        case StringValue:
          value.setValue((String) value.getValue());
          break;
        default:
          value = null;
      }
      return value;
    }
  }

  @Override
  public String getCreateTableSQL(String tableName, ResultSet rs) throws SQLException {
    ResultSetMetaData rsmd = rs.getMetaData();
    StringBuilder sb = new StringBuilder();
    sb.append("CREATE TABLE ").append(tableName).append(" (\n");
    for (int column = 1; column <= rsmd.getColumnCount(); column++) {
      //sb.append("    ").append(column == 1 ? ' ' : ',');
      sb.append("    ,");
      sb.append('[').append(rsmd.getColumnName(column)).append("] ");
      String columnTypeName = rsmd.getColumnTypeName(column);


      //MS SQL-fix
      boolean scaled = true;
      if (columnTypeName.equals("timestamp")) {
        if (rsmd.isNullable(column) == ResultSetMetaData.columnNoNulls) {
          columnTypeName = "binary";
        } else if (rsmd.isNullable(column) == ResultSetMetaData.columnNullable) {
          columnTypeName = "varbinary";
        }
      } else if (columnTypeName.indexOf(" identity") != -1) {
        columnTypeName = columnTypeName.replace(" identity", "");
      } else if (columnTypeName.equals("text")) {
        columnTypeName = "varchar(max)";
        scaled = false;
      }

      if (columnTypeName.equals("datetime")
              || columnTypeName.equals("int")
              || columnTypeName.equals("bigint")) {
        scaled = false;
      }
      sb.append(columnTypeName);


      if (scaled) {
        final int precision = rsmd.getPrecision(column);
        final int scale = rsmd.getScale(column);

        if (precision > 0) {
          sb.append('(').append(precision);
          if (scale > 0) {
            sb.append(',').append(scale);
          }
          sb.append(")");
        }
      }

      if (rsmd.isNullable(column) == ResultSetMetaData.columnNoNulls) {
        sb.append(" NOT NULL");
      } else if (rsmd.isNullable(column) == ResultSetMetaData.columnNullable) {
        sb.append(" NULL");
      }
      sb.append('\n');
    }
    sb.append("    ,CONSTRAINT [<%PK%>_<%TS%>] PRIMARY KEY CLUSTERED\n").append(" (\n").append("   [ID] ASC\n").append(")\n");
    sb.append(")");

    return sb.toString();
  }

  @Override
  public Map<CaseInsensitiveString, Field> getPreparedFields() throws SQLException {
    if (get_fields == null) {
      get_fields = ConnectionManager.getInstance().getConnection().prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "get_fields.sql", "cp1250"));
    }

    Map<CaseInsensitiveString, Field> result = new HashMap<CaseInsensitiveString, Field>();
    ResultSet fields = get_fields.executeQuery();
    try {
      while (fields.next()) {
        CaseInsensitiveString key = new CaseInsensitiveString(fields.getString("ImePolja"));
        Field field = new Field(fields.getInt("Id"), key.toString(), ValueType.valueOf(fields.getInt("TipPolja")).getSqlType(), -1);

        result.put(key, field);
      }
    } finally {
      fields.close();
    }

    return result;
  }

  private static class NamedFieldIds {

    CaseInsensitiveString fieldName;
    Long fieldId;

    public NamedFieldIds(String fieldName, Long fieldId) {
      this.fieldName = CaseInsensitiveString.valueOf(fieldName);
      this.fieldId = fieldId;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final NamedFieldIds other = (NamedFieldIds) obj;
      if (this.fieldName != other.fieldName && (this.fieldName == null || !this.fieldName.equals(other.fieldName))) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 61 * hash + (this.fieldName != null ? this.fieldName.hashCode() : 0);
      return hash;
    }
  }

  private static class EventFilterSearch extends EventQueryParameter {

    private static final String EV_VERSIONED_SUBQUERY = com.openitech.io.ReadInputStream.getResourceAsString(EventFilterSearch.class, "find_event_by_values_versioned.sql", "cp1250");
    private static final String EV_NONVERSIONED_SUBQUERY = com.openitech.io.ReadInputStream.getResourceAsString(EventFilterSearch.class, "find_event_by_values_valid.sql", "cp1250");
    DbDataSource.SubstSqlParameter sqlFindEventVersion = new DbDataSource.SubstSqlParameter("<%ev_version_filter%>");
    DbDataSource.SubstSqlParameter sqlFindEventType = new DbDataSource.SubstSqlParameter("<%ev_type_filter%>");
    DbDataSource.SubstSqlParameter sqlFindEventValid = new DbDataSource.SubstSqlParameter("<%ev_valid_filter%>");
    DbDataSource.SubstSqlParameter sqlFindEventSource = new DbDataSource.SubstSqlParameter("<%ev_source_filter%>");
    DbDataSource.SubstSqlParameter sqlFindEventDate = new DbDataSource.SubstSqlParameter("<%ev_date_filter%>");
    String evVersionedSubquery;
    String evNonVersionedSubquery;
    List<Object> evNonVersionedParameters = new ArrayList<Object>();
    List<Object> evVersionedParameters = new ArrayList<Object>();

    public EventFilterSearch(Map<Field, DbDataSource.SqlParameter<Object>> namedParameters, Integer eventSource, java.util.Date eventDatum, int sifrant, String[] sifra, boolean validOnly) {
      super(namedParameters);

      final SqlParameter<Integer> qpSifrant = new SqlParameter<Integer>(java.sql.Types.INTEGER, sifrant);

      sqlFindEventVersion.setValue("?");
      sqlFindEventVersion.addParameter(this.versionId);

      sqlFindEventValid.setValue(validOnly ? " AND ev.valid = 1 " : "");
      if (sifra == null) {
        sqlFindEventType.setValue("ev.[IdSifranta] = ?");
        sqlFindEventType.addParameter(qpSifrant);
      } else if (sifra.length == 1) {
        sqlFindEventType.setValue("ev.[IdSifranta] = ? AND ev.[IdSifre] = ?");
        sqlFindEventType.addParameter(qpSifrant);
        sqlFindEventType.addParameter(new SqlParameter<String>(java.sql.Types.VARCHAR, sifra[0]));
      } else {
        StringBuilder sbet = new StringBuilder();
        sqlFindEventType.addParameter(qpSifrant);
        for (String s : sifra) {
          sbet.append(sbet.length() > 0 ? ", " : "").append("?");
          sqlFindEventType.addParameter(new SqlParameter<String>(java.sql.Types.VARCHAR, s));
        }
        sbet.insert(0, "ev.[IdSifranta] = ? AND ev.[IdSifre] IN (").append(") ");
        sqlFindEventType.setValue(sbet.toString());
      }

      if (eventSource == null) {
        sqlFindEventSource.setValue("");
      } else {
        sqlFindEventSource.setValue(" AND ev.[IdEventSource] = ?");
        sqlFindEventSource.addParameter(new SqlParameter<Integer>(java.sql.Types.INTEGER, eventSource));
      }
      if (eventDatum == null) {
        sqlFindEventDate.setValue("");
      } else {
        sqlFindEventDate.setValue(" AND ev.DATUM = ?");
        sqlFindEventDate.addParameter(new SqlParameter<Date>(java.sql.Types.TIMESTAMP, eventDatum));
      }

      evVersionedParameters.add(sqlFindEventVersion);
      evVersionedParameters.add(sqlFindEventVersion);
      evVersionedParameters.add(sqlFindEventType);
      evVersionedParameters.add(sqlFindEventSource);
      evVersionedParameters.add(sqlFindEventDate);
      evVersionedSubquery = SQLDataSource.substParameters(EV_VERSIONED_SUBQUERY, evVersionedParameters);

      evNonVersionedParameters.add(sqlFindEventType);
      evNonVersionedParameters.add(sqlFindEventValid);
      evNonVersionedParameters.add(sqlFindEventSource);
      evNonVersionedParameters.add(sqlFindEventDate);
      evNonVersionedSubquery = SQLDataSource.substParameters(EV_NONVERSIONED_SUBQUERY, evNonVersionedParameters);
    }

  @Override
    public boolean hasVersionId() {
      return versionId.getValue() != null && (((Long) versionId.getValue()) > 0);
    }

    @Override
    public List<Object> getParameters() {
      return Collections.unmodifiableList(hasVersionId() ? evVersionedParameters : evNonVersionedParameters);
    }

    @Override
    public String getValue() {
      return hasVersionId() ? evVersionedSubquery : evNonVersionedSubquery;
    }
  }

  @Override
  public EventQueryParameter getEventQueryParameter(Map<Field, SqlParameter<Object>> namedParameters, Integer eventSource, Date eventDatum, int sifrant, String[] sifra, boolean validOnly) {
    return new EventFilterSearch(namedParameters, eventSource, eventDatum, sifrant, sifra, validOnly);
  }

  private int prepareSearchParameters(List parameters, Map<Field, DbDataSource.SqlParameter<Object>> namedParameters, Event event, Set<Field> searchFields, Set<Field> resultFields, int sifrant, String[] sifra, boolean validOnly, boolean lastEntryOnly) {
    StringBuilder sb = new StringBuilder(500);
    StringBuilder sbresult = new StringBuilder(500);
    DbDataSource.SubstSqlParameter sqlResultLimit = new DbDataSource.SubstSqlParameter("<%ev_result_limit%>");
    parameters.add(sqlResultLimit);
    sqlResultLimit.setValue(lastEntryOnly ? " TOP 1 " : " DISTINCT TOP 100 PERCENT ");
    DbDataSource.SubstSqlParameter sqlResultFields = new DbDataSource.SubstSqlParameter("<%ev_field_results%>");
    parameters.add(sqlResultFields);
    EventFilterSearch eventSearchFilter = new EventFilterSearch(namedParameters, searchFields.contains(Event.EVENT_SOURCE) ? event.getEventSource() : null, searchFields.contains(Event.EVENT_DATE) ? event.getDatum() : null, sifrant, sifra, validOnly);
    parameters.add(eventSearchFilter);
    DbDataSource.SubstSqlParameter sqlFind = new DbDataSource.SubstSqlParameter("<%ev_values_filter%>");
    parameters.add(sqlFind);
    DbDataSource.SubstSqlParameter sqlValidOnly = new DbDataSource.SubstSqlParameter("<%ev_valid_filter%>");
    parameters.add(sqlValidOnly);
//    if (validOnly) {
//      sqlValidOnly.setValue("WHERE (ev.[validTo] IS NULL OR ev.[ValidTo] >= GETDATE())");
//    } else {
    sqlValidOnly.setValue("");
//    }

    int valuesSet = 0;
    Map<NamedFieldIds, NamedFieldIds> fieldNames = new java.util.HashMap<NamedFieldIds, NamedFieldIds>();
    try {
      if (sifra == null) {
        fieldNames = getEventFields(sifrant, null);
      } else {
        for (String s : sifra) {
          fieldNames.putAll(getEventFields(sifrant, s));
        }
      }

    } catch (SQLException ex) {
      fieldNames = new java.util.HashMap<NamedFieldIds, NamedFieldIds>();
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    }
    for (Field f : searchFields) {
      if (!(Event.EVENT_SOURCE.equals(f)
              || Event.EVENT_DATE.equals(f))) {
        valuesSet++;

        final String ev_alias = "[ev_" + f.getName() + "]";
        final String vp_alias = "[vp_" + f.getName() + "]";
        final String val_alias = "[val_" + f.getName() + "]";
        sb.append("\nLEFT OUTER JOIN " + SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB) + ".[dbo].[EventValues] ").append(ev_alias).append(" WITH (NOLOCK) ON (");
        sb.append("ev.[Id] = ").append(ev_alias).append(".[EventId]");
        NamedFieldIds fn = new NamedFieldIds(f.getName(), Long.MIN_VALUE);
        if (fieldNames.containsKey(fn)) {
          sb.append(" AND ").append(ev_alias).append(".[IdPolja] = ").append(fieldNames.get(fn).fieldId);
          sb.append(" AND ").append(ev_alias).append(".[FieldValueIndex] = ").append(f.getFieldIndex()).append(" )");
        } else {
          sb.append(" AND ").append(ev_alias).append(".[FieldValueIndex] = ").append(f.getFieldIndex());
          sb.append(") ");
          sb.append("\nLEFT OUTER JOIN " + SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB) + ".[dbo].[SifrantVnosnihPolj] ").append(vp_alias).append(" WITH (NOLOCK) ON (");
          sb.append(ev_alias).append(".[IdPolja] = ").append(vp_alias).append(".[Id]");
          sb.append(" AND ").append(vp_alias).append(".ImePolja= '").append(f.getName()).append("' )");
        }
        sb.append("\nINNER JOIN " + SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB) + ".[dbo].[VariousValues] ").append(val_alias).append(" WITH (NOLOCK) ON (");
        sb.append(ev_alias).append(".[ValueId] = ").append(val_alias).append(".[Id]");
        List<FieldValue> values;
        if (event.getEventValues().containsKey(f)) {
          values = event.getEventValues().get(f);
        } else {
          values = new java.util.ArrayList<FieldValue>();
          values.add(new FieldValue(f));
        }
//        if (values != null) {
        sb.append(" AND (");
        boolean first = true;

        StringBuilder join = new StringBuilder();

        for (FieldValue fv : values) {
          int tipPolja = fv.getValueType().getTypeIndex();
          String value = null;
          if (first) {
            first = false;
          } else {
            sb.append(" OR ");
          }
          switch (tipPolja) {
            case 1:
              //Integer
              value = val_alias + ".IntValue";
              sb.append(val_alias).append(".IntValue = ? ");
              if (resultFields.contains(f)) {
                sbresult.append(",\n").append(val_alias).append(".IntValue AS [").append(f.getName()).append("]");
              }
              break;
            case 2:
              //Real
              value = val_alias + ".RealValue";
              sb.append(val_alias).append(".RealValue = ? ");
              if (resultFields.contains(f)) {
                sbresult.append(",\n").append(val_alias).append(".RealValue AS [").append(f.getName()).append("]");
              }
              break;
            case 3:
              //String
              value = val_alias + ".StringValue";
              sb.append(val_alias).append(".StringValue = ? ");
              if (resultFields.contains(f)) {
                sbresult.append(",\n").append(val_alias).append(".StringValue AS [").append(f.getName()).append("]");
              }
              break;
            case 4:
            case 8:
            case 9:
            case 10:
              //Date
              value = val_alias + ".DateValue";
              sb.append(val_alias).append(".DateValue = ? ");
              if (resultFields.contains(f)) {
                sbresult.append(",\n").append(val_alias).append(".DateValue AS [").append(f.getName()).append("]");
              }
              break;
            case 6:
              //Clob
              value = val_alias + ".ClobValue";
              sb.append(val_alias).append(".ClobValue = ? ");
              if (resultFields.contains(f)) {
                sbresult.append(",\n").append(val_alias).append(".ClobValue AS [").append(f.getName()).append("]");
              }
              break;
            case 7:
              //Boolean
              value = val_alias + ".IntValue";
              sb.append(val_alias).append(".IntValue = ? ");
              if (resultFields.contains(f)) {
                sbresult.append(",\n").append("CAST(").append(val_alias).append(".IntValue AS BIT) AS [").append(f.getName()).append("]");
              }
          }
          if (resultFields.contains(f)
                  && (f.getModel().getQuery() != null)) {
            if (f.getModel().getQuery().getSelect() != null) {
              for (String sql : f.getModel().getQuery().getSelect().getSQL()) {
                sql = sql.replaceAll("<%ChangeLog%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB));
                sql = sql.replaceAll("<%RPP%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPP_DB, SqlUtilities.RPP_DB));
                sql = sql.replaceAll("<%RPE%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPE_DB, SqlUtilities.RPE_DB));
                sql = sql.replaceAll(f.getModel().getReplace(), value);

                sbresult.append(",\n").append(sql);
              }
            }
            if (f.getModel().getQuery().getJoin() != null) {
              for (String sql : f.getModel().getQuery().getJoin().getSQL()) {
                sql = sql.replaceAll("<%ChangeLog%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB));
                sql = sql.replaceAll("<%RPP%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPP_DB, SqlUtilities.RPP_DB));
                sql = sql.replaceAll("<%RPE%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPE_DB, SqlUtilities.RPE_DB));
                sql = sql.replaceAll(f.getModel().getReplace(), value);

                join.append("\nLEFT OUTER JOIN ").append(sql);
              }
            }
          }
          DbDataSource.SqlParameter<Object> parameter = new DbDataSource.SqlParameter<Object>();
          parameter.setType(fv.getType());
          parameter.setValue(fv.getValue());
          parameters.add(parameter);
          namedParameters.put(f, parameter);
        }
        sb.append(")");
//        }
        sb.append(") ");
        sb.append(join);
      } else if (Event.EVENT_SOURCE.equals(f)
              || Event.EVENT_DATE.equals(f)) {
        valuesSet++;
      }
    }
    for (Field f : resultFields) {
      if (!searchFields.contains(f)) {
        String fieldValueIndex = f.getFieldIndex() > 1 ? Integer.toString(f.getFieldIndex()) : "";
        if (f.getName().endsWith(fieldValueIndex)) {
          fieldValueIndex = "";
        }
        final String ev_alias = "[ev_" + f.getName() + fieldValueIndex + "]";
        final String vp_alias = "[vp_" + f.getName() + fieldValueIndex + "]";
        final String val_alias = "[val_" + f.getName() + fieldValueIndex + "]";
        sb.append("\nLEFT OUTER JOIN " + SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB) + ".[dbo].[EventValues] ").append(ev_alias).append(" WITH (NOLOCK) ON (");
        sb.append("ev.[Id] = ").append(ev_alias).append(".[EventId]");
        NamedFieldIds fn = new NamedFieldIds(f.getName(), Long.MIN_VALUE);
        //TODO to ni uredu, ker ne da pravilnega rezultata, ce ne iscem po id polja
        if (fieldNames.containsKey(fn) || f.getIdPolja() != null) {
          sb.append(" AND ").append(ev_alias).append(".[IdPolja] = ").append(fieldNames.get(fn) != null ? fieldNames.get(fn).fieldId : f.getIdPolja().intValue());
          sb.append(" AND ").append(ev_alias).append(".[FieldValueIndex] = ").append(f.getFieldIndex()).append(" )");
        } else {
          sb.append(" AND ").append(ev_alias).append(".[IdPolja] = ").append("(SELECT Id FROM " + SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB) + ".[dbo].[SifrantVnosnihPolj] WHERE ImePolja = '").append(f.getName()).append("' ) ");
          sb.append(" AND ").append(ev_alias).append(".[FieldValueIndex] = ").append(f.getFieldIndex()).append(" )");
          sb.append(" AND ").append(ev_alias).append(".[FieldValueIndex] = ").append(f.getFieldIndex());
//          sb.append(") ");
//          sb.append("\nLEFT OUTER JOIN " + SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB) + ".[dbo].[SifrantVnosnihPolj] ").append(vp_alias).append(" WITH (NOLOCK) ON (");
//          sb.append(ev_alias).append(".[IdPolja] = ").append(vp_alias).append(".[Id]");
//          sb.append(" AND ").append(vp_alias).append(".ImePolja= '").append(f.getName()).append("' ) ");
        }
        sb.append("\nLEFT OUTER JOIN " + SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB) + ".[dbo].[VariousValues] ").append(val_alias).append(" WITH (NOLOCK) ON (");
        sb.append(ev_alias).append(".[ValueId] = ").append(val_alias).append(".[Id] )");

        int tipPolja = ValueType.getType(f.getType()).getTypeIndex();
        String value = null;
        switch (tipPolja) {
          case 1:
            value = val_alias + ".IntValue";
            sbresult.append(",\n").append(val_alias).append(".IntValue AS [").append(f.getName() + fieldValueIndex).append("]");
            break;
          case 2:
            //Real
            value = val_alias + ".RealValue";
            sbresult.append(",\n").append(val_alias).append(".RealValue AS [").append(f.getName() + fieldValueIndex).append("]");
            break;
          case 3:
            //String
            value = val_alias + ".StringValue";
            sbresult.append(",\n").append(val_alias).append(".StringValue AS [").append(f.getName() + fieldValueIndex).append("]");
            break;
          case 4:
          case 8:
          case 9:
          case 10:
            //Date
            value = val_alias + ".DateValue";
            sbresult.append(",\n").append(val_alias).append(".DateValue AS [").append(f.getName() + fieldValueIndex).append("]");
            break;
          case 6:
            //Clob
            value = val_alias + ".ClobValue";
            sbresult.append(",\n").append(val_alias).append(".ClobValue AS [").append(f.getName() + fieldValueIndex).append("]");
            break;
          case 7:
            //Boolean
            value = val_alias + ".IntValue";
            sbresult.append(",\n").append("CAST(").append(val_alias).append(".IntValue AS BIT) AS [").append(f.getName() + fieldValueIndex).append("]");
        }
        if ((f.getModel().getQuery() != null)
                && (f.getModel().getQuery() != null)) {
          if (f.getModel().getQuery().getSelect() != null) {
            for (String sql : f.getModel().getQuery().getSelect().getSQL()) {
              sql = sql.replaceAll("<%ChangeLog%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB));
              sql = sql.replaceAll("<%RPP%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPP_DB, SqlUtilities.RPP_DB));
              sql = sql.replaceAll("<%RPE%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPE_DB, SqlUtilities.RPE_DB));
              sql = sql.replaceAll(f.getModel().getReplace(), value);

              sbresult.append(",\n").append(sql);
            }
          }
          if (f.getModel().getQuery().getJoin() != null) {
            for (String sql : f.getModel().getQuery().getJoin().getSQL()) {
              sql = sql.replaceAll("<%ChangeLog%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB));
              sql = sql.replaceAll("<%RPP%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPP_DB, SqlUtilities.RPP_DB));
              sql = sql.replaceAll("<%RPE%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPE_DB, SqlUtilities.RPE_DB));
              sql = sql.replaceAll(f.getModel().getReplace(), value);

              sb.append("\nLEFT OUTER JOIN ").append(sql);
            }
          }
        }
      }
    }
    sqlFind.setValue(sb.toString());
    sqlResultFields.setValue(sbresult.toString());


    return valuesSet;
  }

  private static String getFindEventSQL() {
    return com.openitech.io.ReadInputStream.getResourceAsString(SqlUtilitesImpl.class, "find_event_by_values.sql", "cp1250");
  }

  private static String getCheckVecVrednostiEventSQL() {
    return com.openitech.io.ReadInputStream.getResourceAsString(SqlUtilitesImpl.class, "event_vecVrednosti.sql", "cp1250");
  }

  @Override
  public EventQuery prepareEventQuery(Event parent, Set<Field> searchFields, Set<Field> resultFields, int sifrant, String[] sifra, boolean validOnly, boolean lastEntryOnly) {
    EventQueryImpl result = new EventQueryImpl(parent);

    result.sifrant = sifrant;
    result.sifra = sifra;
    result.resultFields = Collections.unmodifiableSet(resultFields);
    result.searchFields = Collections.unmodifiableSet(searchFields);
    result.valuesSet = prepareSearchParameters(result.parameters, result.namedParameters, parent, searchFields, resultFields, sifrant, sifra, validOnly, lastEntryOnly);

    return result;
  }

  public static class EventQueryImpl implements EventQuery {

    private Event parent;
    private int valuesSet = 0;
    private List<Object> parameters = new ArrayList<Object>();
    private Map<Field, DbDataSource.SqlParameter<Object>> namedParameters = new HashMap<Field, DbDataSource.SqlParameter<Object>>();
    private String query = getFindEventSQL();
    private int sifrant;
    private String[] sifra;
    private Set<Field> searchFields;
    private Set<Field> resultFields;

    public EventQueryImpl(Event parent) {
      this.parent = parent;
    }

    /**
     * Get the value of valuesSet
     *
     * @return the value of valuesSet
     */
    @Override
    public int getValuesSet() {
      return valuesSet;
    }

    /**
     * Get the value of sifra
     *
     * @return the value of sifra
     */
    @Override
    public String[] getSifra() {
      return sifra;
    }

    /**
     * Get the value of sifrant
     *
     * @return the value of sifrant
     */
    @Override
    public int getSifrant() {
      return sifrant;
    }

    /**
     * Get the value of query
     *
     * @return the value of query
     */
    @Override
    public String getQuery() {
      return query;
    }

    /**
     * Get the value of event
     *
     * @return the value of event
     */
    public Event getParent() {
      return parent;
    }

    /**
     * Get the value of parameters
     *
     * @return the value of parameters
     */
    @Override
    public List<Object> getParameters() {
      return Collections.unmodifiableList(parameters);
    }

    /**
     * Get the value of namedParameters
     *
     * @return the value of namedParameters
     */
    @Override
    public Map<Field, SqlParameter<Object>> getNamedParameters() {
      return namedParameters;
    }

    /**
     * Get the value of resultFields
     *
     * @return the value of resultFields
     */
    @Override
    public Set<Field> getResultFields() {
      return resultFields;
    }

    /**
     * Get the value of searchFields
     *
     * @return the value of searchFields
     */
    @Override
    public Set<Field> getSearchFields() {
      return searchFields;
    }
  }
}
