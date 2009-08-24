/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.util.mssql;

import com.openitech.CaseInsensitiveString;
import com.openitech.db.ConnectionManager;
import com.openitech.db.components.JPIzbiraNaslova;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSource.SqlParameter;
import com.openitech.db.model.sql.SQLDataSource;
import com.openitech.sql.Field;
import com.openitech.sql.events.Event;
import com.openitech.sql.events.EventQuery;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.sql.FieldValue;
import com.openitech.sql.ValueType;
import com.openitech.util.ReadInputStream;
import com.sun.rowset.CachedRowSetImpl;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
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
  PreparedStatement get_field;
  PreparedStatement insertNeznaniNaslov;
  PreparedStatement findHsNeznanaId;

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
      logChanges = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "insert_change_log.sql", "cp1250"));
    }
    if (logValues == null) {
      logValues = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "insert_values.sql", "cp1250"));
    }

    if (logChangedValues == null) {
      logChangedValues = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "insert_changed_values.sql", "cp1250"));
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
  public Long storeEvent(Event event) throws SQLException {
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (insertEvents == null) {
      insertEvents = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "insertEvents.sql", "cp1250"));
      insertEvents.setQueryTimeout(15);
    }
    if (updateEvents == null) {
      updateEvents = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "updateEvents.sql", "cp1250"));
      updateEvents.setQueryTimeout(15);
    }
    if (findEventValue == null) {
      findEventValue = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "find_eventvalue.sql", "cp1250"));
    }
    if (insertEventValues == null) {
      insertEventValues = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "insertEventValues.sql", "cp1250"));
      insertEventValues.setQueryTimeout(15);
    }
    if (updateEventValues == null) {
      updateEventValues = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "updateEventValue.sql", "cp1250"));
      updateEventValues.setQueryTimeout(15);
    }
    if (get_field == null) {
      get_field = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "get_field.sql", "cp1250"));
    }
    int param;
    boolean success = true;
    boolean commit = false;
    boolean isTransaction = isTransaction();
    // <editor-fold defaultstate="collapsed" desc="Shrani">

    long events_ID = 0;
    try {
      if (!isTransaction) {
        beginTransaction();
      }

      boolean insert = event.getId() == -1;

      if (insert) {
        System.out.println("event:"+event.getSifrant()+"-"+event.getSifra()+":inserting");
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
        insertEvents.setTimestamp(param++, new java.sql.Timestamp(event.getDatum().getTime()));
        insertEvents.setString(param++, event.getOpomba());
        success = success && insertEvents.executeUpdate() > 0;

        events_ID = getLastIdentity();
      } else {
        events_ID = event.getId();
        System.out.println("event:"+event.getSifrant()+"-"+event.getSifra()+":updating:"+events_ID);

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
        updateEvents.setLong(param++, events_ID);

        success = success && updateEvents.executeUpdate() > 0;
      }

      if (success) {
        Map<Field, List<FieldValue>> eventValues = event.getEventValues();
        for (Field field : eventValues.keySet()) {
          List<FieldValue> fieldValues = eventValues.get(field);
          for (int i = 0; i < fieldValues.size(); i++) {
            FieldValue value = fieldValues.get(i);
            Long valueId = storeValue(value.getValueType(), value.getValue());

            param = 1;
            get_field.setString(param, field.getName());

            ResultSet rs_field = get_field.executeQuery();
            rs_field.next();

            int field_id = rs_field.getInt("Id");

            param = 1;
            findEventValue.clearParameters();
            findEventValue.setLong(param++, events_ID);
            findEventValue.setInt(param++, field_id);
            findEventValue.setInt(param++, i + 1);  //indexPolja

            ResultSet rs = findEventValue.executeQuery();
            rs.next();

            if (rs.getInt(1) == 0) {
              //insertaj event value
              param = 1;
              insertEventValues.clearParameters();
              insertEventValues.setLong(param++, events_ID);
              insertEventValues.setInt(param++, field_id);
              insertEventValues.setInt(param++, i + 1);  //indexPolja
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
              updateEventValues.setInt(param++, i + 1);  //indexPolja

              success = success && updateEventValues.executeUpdate() > 0;
            }
          }
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

  @Override
  public Long storeValue(ValueType fieldType, final Object value) throws SQLException {
    if (logValues == null) {
      logValues = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "insert_values.sql", "cp1250"));
      logValues.setQueryTimeout(15);
    }

    int pos = 0;
    FieldValue[] fieldValues = new FieldValue[7];
    fieldValues[pos++] = new FieldValue("FieldType", Types.INTEGER, fieldType.getTypeIndex());
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
            find_intvalue = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "find_intvalue.sql", "cp1250"));
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
            find_realvalue = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "find_realvalue.sql", "cp1250"));
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
            find_stringvalue = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "find_stringvalue.sql", "cp1250"));
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
            find_datevalue = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "find_datevalue.sql", "cp1250"));
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
          break;
      }
      if (newValueId == null) {
        executeUpdate(logValues, fieldValues);
        newValueId = getLastIdentity();
      }
    }
    return newValueId;
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
    params.add(parameters.get(1));

    dsSifrant.setParameters(params);
    dsSifrant.setCountSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sifrant_c.sql", "cp1250"));
    dsSifrant.setSelectSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sifrant.sql", "cp1250"));
    dsSifrant.setQueuedDelay(0);

    return dsSifrant;
  }

  @Override
  public JPIzbiraNaslova.Naslov storeAddress(JPIzbiraNaslova.Naslov address) throws SQLException {

    SqlUtilities sqlUtility = SqlUtilities.getInstance();
    Connection connection = ConnectionManager.getInstance().getTxConnection();
    int param;

    if (address.getHsMID() == null || address.getHsMID().getValue() == null) {
      param = 1;
      if (findHsNeznanaId == null) {
        findHsNeznanaId = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "findHsNeznanaId.sql", "cp1250"));
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
          insertNeznaniNaslov = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "insertNeznaniNaslov.sql", "cp1250"));
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
      findEventById = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "find_event_by_id.sql", "cp1250"));
    }
    ResultSet rs = executeQuery(findEventById, new FieldValue("ID", java.sql.Types.BIGINT, eventId));
    try {
      if (rs.next()) {
        Event result = new Event(rs.getInt("IdSifranta"),
                rs.getString("IdSifre"));
        result.setId(eventId);
        result.setEventSource(rs.getInt("IdEventSource"));
        result.setDatum(rs.getTimestamp("Datum"));
        java.sql.Clob opomba = rs.getClob("Opomba");
        if (!rs.wasNull()) {
          result.setOpomba(opomba.getSubString(1L, (int) opomba.length()));
        }
        do {
          switch (rs.getInt("FieldType")) {
            case 1:
              result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.INTEGER, rs.getInt("IntValue")));
              break;
            case 2:
              result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.DOUBLE, rs.getDouble("RealValue")));
              break;
            case 3:
              result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.VARCHAR, rs.getString("StringValue")));
              break;
            case 4:
              result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.TIMESTAMP, rs.getTimestamp("DateValue")));
              break;
            case 5:
              result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.BLOB, rs.getBlob("ObjectValue")));
              break;
            case 6:
              java.sql.Clob value = rs.getClob("ClobValue");
              result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.VARCHAR, value.getSubString(1L, (int) value.length())));
              break;
            case 7:
              result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.BOOLEAN, rs.getInt("IntValue") != 0));
              break;
            case 8:
              result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.TIMESTAMP, rs.getTimestamp("DateValue")));
              break;
            case 9:
              result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.TIME, rs.getTime("DateValue")));
              break;
            case 10:
              result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.DATE, rs.getDate("DateValue")));
              break;
          }
        } while (rs.next());
        return result;
      } else {
        return null;
      }
    } finally {
      rs.close();
    }
  }

  @Override
  public Event findEvent(Event event) throws SQLException {
    if (event.getId() <= 0) {
      java.util.Set<Field> primaryKey = new java.util.HashSet<Field>();
      if (event.getPrimaryKey() != null) {
        for (Field pk : event.getPrimaryKey()) {
          primaryKey.add(pk);
        }
      } else {
        primaryKey.addAll(event.getEventValues().keySet());
        //primaryKey.add(Event.EVENT_DATE);
        primaryKey.add(Event.EVENT_SOURCE);
      }
      java.util.List parameters = new java.util.ArrayList<Object>();
      int valuesSet = prepareSearchParameters(parameters, new HashMap<Field, DbDataSource.SqlParameter<Object>>(), event, primaryKey, new java.util.HashSet<Field>(), event.getSifrant(), event.getSifra(), true, false);

      //ce niso bile nastavljene vse vrednosti PK-ja potem ne iscemo 
      boolean seek = true;

      if (event.getPrimaryKey() != null) {
        seek = valuesSet == primaryKey.size();
      }

      if (seek) {
        ResultSet rs = SQLDataSource.executeQuery(getFindEventSQL(), parameters, ConnectionManager.getInstance().getTxConnection());
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

  @Override
  public synchronized CachedRowSet getGeneratedFields(int idSifranta, String idSifre, boolean visibleOnly) throws SQLException {
    if (getGeneratedFields == null) {
      getGeneratedFields = ConnectionManager.getInstance().getConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "getGeneratedFields.sql", "cp1250"), java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
    }
    getGeneratedFields.clearParameters();
    int param = 1;
    getGeneratedFields.setInt(param++, idSifranta);
    getGeneratedFields.setBoolean(param++, idSifre == null);
    getGeneratedFields.setString(param++, idSifre);
    getGeneratedFields.setBoolean(param++, !visibleOnly);
    CachedRowSet rs = new CachedRowSetImpl();
    rs.populate(getGeneratedFields.executeQuery());

    return rs;
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

  private int prepareSearchParameters(List parameters, Map<Field, DbDataSource.SqlParameter<Object>> namedParameters, Event event, Set<Field> searchFields, Set<Field> resultFields, int sifrant, String sifra, boolean validOnly, boolean lastEntryOnly) {
    StringBuilder sb = new StringBuilder(500);
    StringBuilder sbresult = new StringBuilder(500);
    DbDataSource.SubstSqlParameter sqlResultLimit = new DbDataSource.SubstSqlParameter("<%ev_result_limit%>");
    parameters.add(sqlResultLimit);
    sqlResultLimit.setValue(lastEntryOnly ? " TOP 1 ":" DISTINCT TOP 100 PERCENT ");
    DbDataSource.SubstSqlParameter sqlResultFields = new DbDataSource.SubstSqlParameter("<%ev_field_results%>");
    parameters.add(sqlResultFields);
    DbDataSource.SubstSqlParameter sqlFindEventType = new DbDataSource.SubstSqlParameter("<%ev_type_filter%>");
    parameters.add(sqlFindEventType);
//    String validFrom = validOnly ? " AND GETDATE()>=ev.validFrom " : "" ;
    String validFrom = validOnly ? " AND ev.valid = 1 " : "";
    if (sifra == null) {
      sqlFindEventType.setValue("ev.[IdSifranta] = ?" + validFrom);
      parameters.add(sifrant);
    } else {
      sqlFindEventType.setValue("ev.[IdSifranta] = ? AND ev.[IdSifre] = ?" + validFrom);
      parameters.add(sifrant);
      parameters.add(sifra);
    }

    DbDataSource.SubstSqlParameter sqlFindEventSource = new DbDataSource.SubstSqlParameter("<%ev_source_filter%>");
    parameters.add(sqlFindEventSource);
    if (!searchFields.contains(Event.EVENT_SOURCE)) {
      sqlFindEventSource.setValue("");
    } else {
      sqlFindEventSource.setValue(" AND ev.[IdEventSource] = ?");
      parameters.add(event.getEventSource());
    }
    DbDataSource.SubstSqlParameter sqlFindDate = new DbDataSource.SubstSqlParameter("<%ev_date_filter%>");
    parameters.add(sqlFindDate);
    if (searchFields.contains(Event.EVENT_DATE)) {
      sqlFindDate.setValue(" AND ev.DATUM = ?");
      parameters.add(event.getDatum());
    } else {
      sqlFindDate.setValue("");
    }
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
    Map<NamedFieldIds, NamedFieldIds> fieldNames;
    try {
      fieldNames = getEventFields(sifrant, sifra);
    } catch (SQLException ex) {
      fieldNames = new java.util.HashMap<NamedFieldIds, NamedFieldIds>();
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    }
    for (Field f : searchFields) {
      if (!(Event.EVENT_SOURCE.equals(f) ||
              Event.EVENT_DATE.equals(f))) {
        valuesSet++;
        final String ev_alias = "[ev_" + f.getName() + "]";
        final String vp_alias = "[vp_" + f.getName() + "]";
        final String val_alias = "[val_" + f.getName() + "]";
        sb.append("\nLEFT OUTER JOIN [ChangeLog].[dbo].[EventValues] ").append(ev_alias).append(" ON (");
        sb.append("ev.[Id] = ").append(ev_alias).append(".[EventId]");
        NamedFieldIds fn = new NamedFieldIds(f.getName(), Long.MIN_VALUE);
        if (fieldNames.containsKey(fn)) {
          sb.append(" AND ").append(ev_alias).append(".[IdPolja] = ").append(fieldNames.get(fn).fieldId).append(")");
        } else {
          sb.append(") ");
          sb.append("\nLEFT OUTER JOIN [ChangeLog].[dbo].[SifrantVnosnihPolj] ").append(vp_alias).append(" ON (");
          sb.append(ev_alias).append(".[IdPolja] = ").append(vp_alias).append(".[Id]");
          sb.append(" AND ").append(vp_alias).append(".ImePolja= '").append(f.getName()).append("' ) ");
        }
        sb.append("\nINNER JOIN [ChangeLog].[dbo].[VariousValues] ").append(val_alias).append(" ON (");
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
        for (FieldValue fv : values) {
          int tipPolja = fv.getValueType().getTypeIndex();
          if (first) {
            first = false;
          } else {
            sb.append(" OR ");
          }
          switch (tipPolja) {
            case 1:
              //Integer
              sb.append(val_alias).append(".IntValue = ? ");
              if (resultFields.contains(f)) {
                sbresult.append(",\n").append(val_alias).append(".IntValue AS [").append(f.getName()).append("]");
              }
              break;
            case 2:
              //Real
              sb.append(val_alias).append(".RealValue = ? ");
              if (resultFields.contains(f)) {
                sbresult.append(",\n").append(val_alias).append(".RealValue AS [").append(f.getName()).append("]");
              }
              break;
            case 3:
              //String
              sb.append(val_alias).append(".StringValue = ? ");
              if (resultFields.contains(f)) {
                sbresult.append(",\n").append(val_alias).append(".StringValue AS [").append(f.getName()).append("]");
              }
              break;
            case 4:
              //Date
              sb.append(val_alias).append(".DateValue = ? ");
              if (resultFields.contains(f)) {
                sbresult.append(",\n").append(val_alias).append(".DateValue AS [").append(f.getName()).append("]");
              }
              break;
            case 6:
              //Clob
              sb.append(val_alias).append(".ClobValue = ? ");
              if (resultFields.contains(f)) {
                sbresult.append(",\n").append(val_alias).append(".ClobValue AS [").append(f.getName()).append("]");
              }
              break;
            case 7:
              //Boolean
              sb.append(val_alias).append(".IntValue = ? ");
              if (resultFields.contains(f)) {
                sbresult.append(",\n").append("CAST(").append(val_alias).append(".IntValue AS BIT) AS [").append(f.getName()).append("]");
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
      } else if (Event.EVENT_SOURCE.equals(f) ||
                 Event.EVENT_DATE.equals(f)) {
        valuesSet++;
      }
    }
    for (Field f : resultFields) {
      if (!searchFields.contains(f)) {
        final String ev_alias = "[ev_" + f.getName() + "]";
        final String vp_alias = "[vp_" + f.getName() + "]";
        final String val_alias = "[val_" + f.getName() + "]";
        sb.append("\nLEFT OUTER JOIN [ChangeLog].[dbo].[EventValues] ").append(ev_alias).append(" ON (");
        sb.append("ev.[Id] = ").append(ev_alias).append(".[EventId]");
        NamedFieldIds fn = new NamedFieldIds(f.getName(), Long.MIN_VALUE);
        if (fieldNames.containsKey(fn)) {
          sb.append(" AND ").append(ev_alias).append(".[IdPolja] = ").append(fieldNames.get(fn).fieldId).append(")");
        } else {
          sb.append(") ");
          sb.append("\nLEFT OUTER JOIN [ChangeLog].[dbo].[SifrantVnosnihPolj] ").append(vp_alias).append(" ON (");
          sb.append(ev_alias).append(".[IdPolja] = ").append(vp_alias).append(".[Id]");
          sb.append(" AND ").append(vp_alias).append(".ImePolja= '").append(f.getName()).append("' ) ");
        }
        sb.append("\nLEFT OUTER JOIN [ChangeLog].[dbo].[VariousValues] ").append(val_alias).append(" ON (");
        sb.append(ev_alias).append(".[ValueId] = ").append(val_alias).append(".[Id] )");

        int tipPolja = ValueType.getType(f.getType()).getTypeIndex();
        switch (tipPolja) {
          case 1:
            sbresult.append(",\n").append(val_alias).append(".IntValue AS [").append(f.getName()).append("]");
            break;
          case 2:
            //Real
            sbresult.append(",\n").append(val_alias).append(".RealValue AS [").append(f.getName()).append("]");
            break;
          case 3:
            //String
            sbresult.append(",\n").append(val_alias).append(".StringValue AS [").append(f.getName()).append("]");
            break;
          case 4:
            //Date
            sbresult.append(",\n").append(val_alias).append(".DateValue AS [").append(f.getName()).append("]");
            break;
          case 6:
            //Clob
            sbresult.append(",\n").append(val_alias).append(".ClobValue AS [").append(f.getName()).append("]");
            break;
          case 7:
            //Boolean
            sbresult.append(",\n").append("CAST(").append(val_alias).append(".IntValue AS BIT) AS [").append(f.getName()).append("]");
        }
      }
    }
    sqlFind.setValue(sb.toString());
    sqlResultFields.setValue(sbresult.toString());


    return valuesSet;
  }

  private static String getFindEventSQL() {
    return com.openitech.util.ReadInputStream.getResourceAsString(SqlUtilitesImpl.class, "find_event_by_values.sql", "cp1250");
  }

  @Override
  public EventQuery prepareEventQuery(Event parent, Set<Field> searchFields, Set<Field> resultFields, int sifrant, String sifra, boolean lastEntryOnly) {
    EventQueryImpl result = new EventQueryImpl(parent);

    result.sifrant = sifrant;
    result.sifra = sifra;
    result.valuesSet = prepareSearchParameters(result.parameters, result.namedParameters, parent, searchFields, resultFields, sifrant, sifra, true, lastEntryOnly);

    return result;
  }

  public static class EventQueryImpl implements EventQuery {

    private Event parent;
    private int valuesSet = 0;
    private List<Object> parameters = new ArrayList<Object>();
    private Map<Field, DbDataSource.SqlParameter<Object>> namedParameters = new HashMap<Field, DbDataSource.SqlParameter<Object>>();
    private String query = getFindEventSQL();
    private int sifrant;
    private String sifra;

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
    public String getSifra() {
      return sifra;
    }

    /**
     * Get the value of sifrant
     *
     * @return the value of sifrant
     */
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
  }
}
