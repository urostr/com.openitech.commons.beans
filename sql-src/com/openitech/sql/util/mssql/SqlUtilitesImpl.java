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
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSource.SqlParameter;
import com.openitech.db.model.factory.DataSourceFactory;
import com.openitech.db.model.sql.SQLDataSource;
import com.openitech.db.model.sql.SQLNotificationException;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.value.fields.Field;
import com.openitech.value.events.Event;
import com.openitech.value.events.EventQuery;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.fields.FieldValue;
import com.openitech.value.fields.ValueType;
import com.openitech.value.events.ActivityEvent;
import com.openitech.io.ReadInputStream;
import com.openitech.value.VariousValue;
import com.openitech.value.events.EventQueryParameter;
import com.openitech.value.events.EventPK;
import com.sun.rowset.CachedRowSetImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
  PreparedStatement insertEvents;
  PreparedStatement findEventValue;
  PreparedStatement findEventById;
  PreparedStatement insertEventValues;
  PreparedStatement updateEventValues;
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
  String getEventVersionSQL;
  PreparedStatement storeCachedTemporaryTable;
  PreparedStatement delete_eventPK;
  PreparedStatement insert_eventPK;
  PreparedStatement find_eventPK;
  PreparedStatement update_eventPK;

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
  }

  @Override
  protected Long assignEventVersion(List<Long> eventIds) throws SQLException {
    if (!eventIds.isEmpty()) {
      //najprej dodaj verzijo (tabela Versions)
      Long versionId = getVersion(eventIds);

      if (versionId == null) {
        versionId = storeVersion();
        //nato v tabelo EventVersions vpisi z gornjo verzijo vse podane eventId-je
        for (Long eventId : eventIds) {
          storeEventVersion(versionId, eventId);
        }
      }
      return versionId;
    } else {
      return null;
    }
  }

  private Long getVersion(List<Long> eventIds) throws SQLException {

    final Connection connection = ConnectionManager.getInstance().getTxConnection();


    if (getEventVersionSQL == null) {
      getEventVersionSQL = com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "getEventVersion.sql", "cp1250");
    }

    StringBuilder sb = new StringBuilder();

    for (Long eventId : eventIds) {
      sb.append(sb.length() > 0 ? ", " : "").append("?");
    }

    CachedRowSet versions = new CachedRowSetImpl();

    versions.populate(SQLDataSource.executeQuery(getEventVersionSQL.replaceAll("<%EVENTS_LIST%>", sb.toString()).replaceAll("<%EVENT_LIST_SIZE%>", Integer.toString(eventIds.size())),
            eventIds,
            connection));

    if ((versions.size() == 1) && (versions.first())) {
      return versions.getLong(1);
    } else {
      return null;
    }
  }

  private long storeVersion() throws SQLException {
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (insertVersion == null) {
      insertVersion = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insertVersion.sql", "cp1250"));
    }
    insertVersion.executeUpdate();
    return getLastIdentity();
  }

  private void storeEventVersion(long versionId, long eventId) throws SQLException {
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (insertEventVersion == null) {
      insertEventVersion = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insertEventVersion.sql", "cp1250"));
    }
    int param = 1;
    insertEventVersion.clearParameters();
    insertEventVersion.setLong(param++, versionId);
    insertEventVersion.setLong(param++, eventId);
    insertEventVersion.executeUpdate();

  }

  @Override
  public Long storeEvent(Event event, Event oldEvent) throws SQLException {
    if ((oldEvent != null) && oldEvent.equalEventValues(event)) {
      return oldEvent.getId();
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
        if (!isTransaction) {
          beginTransaction();
        }

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
          updateEvents.setTimestamp(param++, new java.sql.Timestamp(event.getDatum().getTime()));
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
          EventPK eventPK = new EventPK();

          Map<Field, List<FieldValue>> eventValues = event.getEventValues();
          for (Field field : eventValues.keySet()) {
            List<FieldValue> fieldValues = eventValues.get(field);
            for (int i = 0; i < fieldValues.size(); i++) {
              FieldValue value = fieldValues.get(i);
              Long valueId = storeValue(value.getValueType(), value.getValue());

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

              if (event.getPrimaryKey() != null && event.getPrimaryKey().length > 0) {
                FieldValue fieldValuePK = new FieldValue(field_id, fieldNameWithIndex, field.getType(), fieldValueIndex, new VariousValue(valueId, value.getType(), value.getValue()));
                for (Field field1 : event.getPrimaryKey()) {
                  if (field1.equals(fieldValuePK)) {
                    eventPK.addField(fieldValuePK);
                    break;
                  }
                }
              }
            }
          }
          if (event.getPrimaryKey() != null && event.getPrimaryKey().length > 0) {
            eventPK.setEventId(events_ID);
            eventPK.setIdSifranta(event.getSifrant());
            eventPK.setIdSifre(event.getSifra());
            eventPK.setOperation(event.getOperation());
            success = success && storePrimaryKey(eventPK);
          }
        }

        commit = success;
      } finally {
        if (!isTransaction) {
          endTransaction(commit);
        }
        event.setId(events_ID);
      }

      return event.getId();
    }
  }

  @Override
  public Long storeValue(ValueType fieldType, final Object value) throws SQLException {
    if (logValues == null) {
      logValues = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insert_values.sql", "cp1250"));
      logValues.setQueryTimeout(15);
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
          fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);
          if (find_intvalue == null) {
            find_intvalue = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_intvalue.sql", "cp1250"));
          }

          find_intvalue.setObject(1, value, java.sql.Types.BIGINT);
          rs = find_intvalue.executeQuery();
          try {
            if (rs.next()) {
              newValueId = rs.getLong(1);
            }
          } finally {
            rs.close();
          }
          break;
        case RealValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, value);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);
          if (find_realvalue == null) {
            find_realvalue = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_realvalue.sql", "cp1250"));
          }

          find_realvalue.setObject(1, value, java.sql.Types.REAL);
          rs = find_realvalue.executeQuery();
          try {
            if (rs.next()) {
              newValueId = rs.getLong(1);
            }
          } finally {
            rs.close();
          }
          break;
        case StringValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, value);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);
          if (find_stringvalue == null) {
            find_stringvalue = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_stringvalue.sql", "cp1250"));
          }

          find_stringvalue.setObject(1, value, java.sql.Types.VARCHAR);
          rs = find_stringvalue.executeQuery();
          try {
            if (rs.next()) {
              newValueId = rs.getLong(1);
            }
          } finally {
            rs.close();
          }
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
          fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);
          if (find_datevalue == null) {
            find_datevalue = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_datevalue.sql", "cp1250"));
          }

          find_datevalue.setObject(1, value, java.sql.Types.TIMESTAMP);
          rs = find_datevalue.executeQuery();
          try {
            if (rs.next()) {
              newValueId = rs.getLong(1);
            }
          } finally {
            rs.close();
          }
          break;
        case ObjectValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, value);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);
          break;
        case ClobValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.VARCHAR, value);
          if (find_clobvalue == null) {
            find_clobvalue = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_clobvalue.sql", "cp1250"));
          }

          find_clobvalue.setObject(1, value, java.sql.Types.CLOB);
          find_clobvalue.setObject(2, value, java.sql.Types.CLOB);
          rs = find_clobvalue.executeQuery();
          try {
            if (rs.next()) {
              newValueId = rs.getLong(1);
            }
          } finally {
            rs.close();
          }

          break;
      }
      if (newValueId == null) {
        executeUpdate(logValues, fieldValues);
        newValueId = getLastIdentity();
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

    }

    return address;
  }

  public Event findEvent(Long eventId) throws SQLException {
    if (findEventById == null) {
      findEventById = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_event_by_id.sql", "cp1250"));
    }
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

        storeCachedTemporaryTable.setString(1, tt.getMaterializedView().getValue());
        storeCachedTemporaryTable.setString(2, sw.toString());
        storeCachedTemporaryTable.executeUpdate();
      } catch (SQLException ex) {
        Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
      } catch (JAXBException ex) {
        Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
      }

    }
  }

  private boolean storePrimaryKey(EventPK eventPK) throws SQLException {
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
    if (find_eventPK == null) {
      find_eventPK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_eventPK.sql", "cp1250"));
    }
    if (update_eventPK == null) {
      update_eventPK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "update_eventPK.sql", "cp1250"));
    }
    if (delete_eventPK == null) {
      delete_eventPK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "delete_eventPK.sql", "cp1250"));
    }



    try {
      param = 1;
      find_eventPK.clearParameters();
      find_eventPK.setLong(param++, eventPK.getEventId());
      find_eventPK.setInt(param++, idSifranta);
      find_eventPK.setString(param++, idSifre);
      find_eventPK.setString(param++, primaryKey);
      ResultSet rs_findEventPK = find_eventPK.executeQuery();
      if (rs_findEventPK.next()) {
        if (eventPK.getEventOperation().equals(Event.EventOperation.DELETE)) {
          param = 1;
          delete_eventPK.clearParameters();
          delete_eventPK.setLong(param++, eventId);
          success = success && delete_eventPK.executeUpdate() > 0;
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
  public PreparedStatement getGeneratedFields;
  private TemporarySubselectSqlParameter ttGeneratedFields;

  private static class GeneratedFieldFactory extends DataSourceFactory {

    private static GeneratedFieldFactory instance;

    public GeneratedFieldFactory() {
      super(null);
    }

    private static TemporarySubselectSqlParameter getGeneratedFields() throws JAXBException, UnsupportedEncodingException, IOException {
      if (instance == null) {
        instance = new GeneratedFieldFactory();
      }

      LineNumberReader is = new LineNumberReader(new InputStreamReader(SqlUtilitesImpl.class.getResourceAsStream("generatedFields.xml"), "UTF-8"));

//      StringBuilder sb = new StringBuilder(2000);
//      String line;
//
//      while ((line = is.readLine())!=null) {
//        sb.append(line).append('\n');
//      }


      JAXBContext ctx = JAXBContext.newInstance(com.openitech.db.model.xml.config.CachedTemporaryTable.class);
      Unmarshaller um = ctx.createUnmarshaller();
      com.openitech.db.model.xml.config.CachedTemporaryTable ctt = (com.openitech.db.model.xml.config.CachedTemporaryTable) um.unmarshal(is);

      return instance.createTemporaryTable(ctt.getTemporaryTable());
    }
  }
  private static final boolean CACHED_GGF = true;

  @Override
  public synchronized CachedRowSet getGeneratedFields(int idSifranta, String idSifre, boolean visibleOnly, ActivityEvent activityEvent) throws SQLException {
    if (CACHED_GGF) {
      if (ttGeneratedFields == null) {
        try {
          ttGeneratedFields = GeneratedFieldFactory.getGeneratedFields();
        } catch (Exception ex) {
          Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
          throw new SQLException("Error preparing the temporary data source", ex);
        }
      }

      if (getGeneratedFields == null) {
        getGeneratedFields = ConnectionManager.getInstance().getConnection().prepareStatement(
                ReadInputStream.getResourceAsString(getClass(), "getGeneratedFieldsCached.sql", "cp1250").replaceAll(ttGeneratedFields.getReplace(), ttGeneratedFields.getValue()),
                java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
      }

      ttGeneratedFields.executeQuery(ConnectionManager.getInstance().getConnection(), new java.util.ArrayList<Object>());
      setGeneratedFieldsParameters(idSifranta, idSifre, visibleOnly, activityEvent);
      CachedRowSet rs = new CachedRowSetImpl();
      rs.populate(getGeneratedFields.executeQuery());

      if (rs.size() == 0) {
        setGeneratedFieldsParameters(idSifranta, idSifre, visibleOnly, null);
        rs.populate(getGeneratedFields.executeQuery());
      }

      return rs;
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
      CachedRowSet rs = new CachedRowSetImpl();
      rs.populate(getGeneratedFields.executeQuery());

      return rs;
    }
  }

  private void setGeneratedFieldsParameters(int idSifranta, String idSifre, boolean visibleOnly, ActivityEvent activityEvent) throws SQLException {
    getGeneratedFields.clearParameters();
    int param = 1;
    getGeneratedFields.setInt(param++, idSifranta);
    getGeneratedFields.setBoolean(param++, idSifre == null);
    getGeneratedFields.setString(param++, idSifre);
    getGeneratedFields.setBoolean(param++, !visibleOnly);
    if (activityEvent != null) {
      getGeneratedFields.setLong(param++, activityEvent.getActivityId());
      getGeneratedFields.setLong(param++, activityEvent.getActivityId());
      getGeneratedFields.setLong(param++, activityEvent.getActivityId());
    } else {
      getGeneratedFields.setNull(param++, java.sql.Types.INTEGER);
      getGeneratedFields.setNull(param++, java.sql.Types.INTEGER);
      getGeneratedFields.setNull(param++, java.sql.Types.INTEGER);
    }
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
    if ((get == null) || (get.size() == 0)) {
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
          value.setValue(getNextSifra((String) value.getValue()));
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
  private final static Pattern numbers = Pattern.compile("(\\p{Space}*)(\\p{Alpha}*)(\\p{Digit}*)(\\p{Space}*)");

  private String getNextSifra(String sifra) {
    Matcher compare = numbers.matcher(sifra);
    if (compare.matches()) {
      String sdigit = compare.group(3);
      String sstring = compare.group(2);
      if (sdigit.length() > 0) {
        String ndigit = Integer.toString(Integer.parseInt(sdigit) + 1);
        if (ndigit.length() > sdigit.length() && sstring.length() > 0) {
          ndigit = "1";
          try {
            sstring = getNextString(sstring);
          } catch (UnsupportedEncodingException ex) {
            sdigit = "0" + sdigit;
            Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.WARNING, null, ex);
          }
        }
        ndigit = getZero(sdigit.length()) + ndigit;
        sdigit = ndigit.substring(ndigit.length() - sdigit.length());
      }
      sifra = compare.group(1) + sstring + sdigit + compare.group(4);
    }
    return sifra;
  }

  private String getNextString(String sstring) throws UnsupportedEncodingException {
    return getNextString(sstring, sstring.length() - 1);
  }

  private String getNextString(String sstring, int at) throws UnsupportedEncodingException {
    byte[] bytes = sstring.substring(at).getBytes("UTF-8");
    bytes[bytes.length - 1]++;
    sstring = sstring.substring(0, sstring.length() - 1) + (new String(bytes, "UTF-8"));
    return sstring;
  }

  private String getZero(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append("0");
    }
    return sb.toString();
  }

  @Override
  public String getCreateTableSQL(String tableName, ResultSet rs) throws SQLException {
    ResultSetMetaData rsmd = rs.getMetaData();
    StringBuilder sb = new StringBuilder();
    sb.append("CREATE TABLE [").append(tableName).append("] (\n");
    sb.append("     [__RsID] bigint identity NOT NULL\n");
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
    sb.append("    ,CONSTRAINT [<%PK%>_<%TS%>] PRIMARY KEY CLUSTERED\n").append(" (\n").append("   [__RsID] ASC\n").append(")\n");
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
        if (fieldNames.containsKey(fn)) {
          sb.append(" AND ").append(ev_alias).append(".[IdPolja] = ").append(fieldNames.get(fn).fieldId);
          sb.append(" AND ").append(ev_alias).append(".[FieldValueIndex] = ").append(f.getFieldIndex()).append(" )");
        } else {
          sb.append(" AND ").append(ev_alias).append(".[FieldValueIndex] = ").append(f.getFieldIndex());
          sb.append(") ");
          sb.append("\nLEFT OUTER JOIN " + SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB) + ".[dbo].[SifrantVnosnihPolj] ").append(vp_alias).append(" WITH (NOLOCK) ON (");
          sb.append(ev_alias).append(".[IdPolja] = ").append(vp_alias).append(".[Id]");
          sb.append(" AND ").append(vp_alias).append(".ImePolja= '").append(f.getName()).append("' ) ");
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
    public Set<Field> getResultFields() {
      return resultFields;
    }

    /**
     * Get the value of searchFields
     *
     * @return the value of searchFields
     */
    public Set<Field> getSearchFields() {
      return searchFields;
    }
  }
}
