/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.util.mssql;

import com.openitech.db.ConnectionManager;
import com.openitech.db.components.JPIzbiraNaslova;
import com.openitech.db.components.JPIzbiraNaslova.Naslov;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.sql.SQLDataSource;
import com.openitech.sql.Field;
import com.openitech.sql.events.Event;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.sql.FieldValue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Map;
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
    Statement statement = ConnectionManager.getInstance().getConnection().createStatement();

    ResultSet result = statement.executeQuery("SELECT SCOPE_IDENTITY() AS ScopeIdentity");
    result.next();

    return result.getLong(1);
  }

  @Override
  public long getCurrentIdentity(String tableName) throws SQLException {
    Statement statement = ConnectionManager.getInstance().getConnection().createStatement();

    ResultSet result = statement.executeQuery("SELECT IDENT_CURRENT(" + tableName + ") AS CurrentIdentity");
    result.next();

    return result.getLong(1);
  }

  @Override
  public long getLastIdentity() throws SQLException {
    Statement statement = ConnectionManager.getInstance().getConnection().createStatement();

    ResultSet result = statement.executeQuery("SELECT @@IDENTITY AS [Identity]");
    result.next();

    return result.getLong(1);
  }

  @Override
  protected void logChanges(String application, String database, String tableName, Operation operation, List<FieldValue> newValues, List<FieldValue> oldValues) throws SQLException {
    if (logChanges == null) {
      logChanges = ConnectionManager.getInstance().getConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "insert_change_log.sql", "cp1250"));
    }
    if (logValues == null) {
      logValues = ConnectionManager.getInstance().getConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "insert_values.sql", "cp1250"));
    }

    if (logChangedValues == null) {
      logChangedValues = ConnectionManager.getInstance().getConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "insert_changed_values.sql", "cp1250"));
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
    final Connection connection = ConnectionManager.getInstance().getConnection();
    if (insertEvents == null) {
      insertEvents = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "insertEvents.sql", "cp1250"));
    }
    if (updateEvents == null) {
      updateEvents = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "updateEvents.sql", "cp1250"));
    }
    if (findEventValue == null) {
      findEventValue = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "find_eventvalue.sql", "cp1250"));
    }
    if (insertEventValues == null) {
      insertEventValues = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "insertEventValues.sql", "cp1250"));
    }
    if (updateEventValues == null) {
      updateEventValues = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "updateEventValue.sql", "cp1250"));
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
        insertEvents.setDate(param++, new java.sql.Date(event.getDatum().getTime()));
        insertEvents.setString(param++, event.getOpomba());
        success = success && insertEvents.executeUpdate() > 0;

        events_ID = getLastIdentity();
      } else {
        events_ID = event.getId();

        param = 1;
        updateEvents.clearParameters();
        updateEvents.setInt(param++, event.getSifrant());
        updateEvents.setString(param++, event.getSifra());
        updateEvents.setString(param++, event.getOpomba());
        if (event.getEventSource() == Integer.MIN_VALUE) {
          updateEvents.setNull(param++, java.sql.Types.INTEGER);
        } else {
          updateEvents.setInt(param++, event.getEventSource());
        }
        updateEvents.setLong(param++, events_ID);

        success = success && updateEvents.executeUpdate() > 0;
      }

      if (success) {
        Map<Field, List<FieldValue>> eventValues = event.getEventValues();
        for (Field field : eventValues.keySet()) {
          List<FieldValue> fieldValues = eventValues.get(field);
          for (int i = 0; i < fieldValues.size(); i++) {
            FieldValue value = fieldValues.get(i);
            Long valueId = storeValue(value.getValueType().getTypeIndex(), value.getValue());

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
  public Long storeValue(int fieldType, final Object value) throws SQLException {
    if (logValues == null) {
      logValues = ConnectionManager.getInstance().getConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "insert_values.sql", "cp1250"));
    }

    int pos = 0;
    FieldValue[] fieldValues = new FieldValue[7];
    fieldValues[pos++] = new FieldValue("FieldType", Types.INTEGER, fieldType);
    java.sql.ResultSet rs;
    Long newValueId = null;
    if (value != null) {
      switch (fieldType) {
        case 1:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, value);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);
          if (find_intvalue == null) {
            find_intvalue = ConnectionManager.getInstance().getConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "find_intvalue.sql", "cp1250"));
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
        case 2:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, value);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);
          if (find_realvalue == null) {
            find_realvalue = ConnectionManager.getInstance().getConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "find_realvalue.sql", "cp1250"));
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
        case 3:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, value);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);
          if (find_stringvalue == null) {
            find_stringvalue = ConnectionManager.getInstance().getConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "find_stringvalue.sql", "cp1250"));
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
        case 4:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, value);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);
          if (find_datevalue == null) {
            find_datevalue = ConnectionManager.getInstance().getConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "find_datevalue.sql", "cp1250"));
          }

          find_datevalue.setObject(1, value, java.sql.Types.DATE);
          rs = find_datevalue.executeQuery();
          try {
            if (rs.next()) {
              newValueId = rs.getLong(1);
            }
          } finally {
            rs.close();
          }
          break;
        case 5:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, value);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);
          break;
        case 6:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, value);
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
  public DbDataSource getDsSifrantModel(List<Object> parameters) throws SQLException {
    DbDataSource dsSifrant = new DbDataSource();

    dsSifrant.setCanAddRows(false);
    dsSifrant.setCanDeleteRows(false);
    dsSifrant.setReadOnly(true);

    dsSifrant.setParameters(parameters);
    dsSifrant.setCountSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sifrant_c.sql", "cp1250"));
    dsSifrant.setSelectSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sifrant.sql", "cp1250"));
    dsSifrant.setQueuedDelay(0);

    return dsSifrant;
  }

  @Override
  public JPIzbiraNaslova.Naslov storeAddress(JPIzbiraNaslova.Naslov address) throws SQLException {

    SqlUtilities sqlUtility = SqlUtilities.getInstance();
    Connection connection = ConnectionManager.getInstance().getConnection();
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
      findEventById = ConnectionManager.getInstance().getConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "find_event_by_id.sql", "cp1250"));
    }
    ResultSet rs = executeQuery(findEventById, new FieldValue("ID", java.sql.Types.BIGINT, eventId));
    try {
      if (rs.next()) {
        Event result = new Event(rs.getInt("IdSifranta"),
                                 rs.getString("IdSifre"));
        result.setEventSource(rs.getInt("IdEventSource"));
        result.setDatum(rs.getDate("Datum"));
        java.sql.Clob opomba = rs.getClob("Opomba");
        if (!rs.wasNull()) {
          result.setOpomba(opomba.getSubString(1L, (int) opomba.length()));
        }
        do {
          switch (rs.getInt("FieldType")) {
            case 1: result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.INTEGER, rs.getInt("IntValue")));
                    break;
            case 2: result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.DOUBLE, rs.getDouble("RealValue")));
                    break;
            case 3: result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.VARCHAR, rs.getString("StringValue")));
                    break;
            case 4: result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.DATE, rs.getDate("DateValue")));
                    break;
            case 5: result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.BLOB, rs.getBlob("ObjectValue")));
                    break;
            case 6: java.sql.Clob value = rs.getClob("ClobValue");
                    result.addValue(new FieldValue(rs.getString("ImePolja"), java.sql.Types.VARCHAR, value.getSubString(1L, (int) value.length())));
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
    if (event.getId() < 0) {
      StringBuilder sb = new StringBuilder(500);
      java.util.List parameters = new java.util.ArrayList<Object>();
      DbDataSource.SubstSqlParameter sqlFind = new DbDataSource.SubstSqlParameter("<%ev_values_filter%>");
      parameters.add(sqlFind);
      for (Field f : event.getEventValues().keySet()) {
        final String ev_alias = "ev_" + f.getName();
        final String vp_alias = "vp_" + f.getName();
        final String val_alias = "val_" + f.getName();
        sb.append("INNER JOIN [ChangeLog].[dbo].[EventValues] ").append(ev_alias).append(" ON (");
        sb.append("ev.[Id] = ").append(ev_alias).append(".[EventId]").append(") ");
        sb.append("INNER JOIN [ChangeLog].[dbo].[SifrantVnosnihPolj] ").append(vp_alias).append(" ON (");
        sb.append(ev_alias).append(".[IdPolja] = ").append(vp_alias).append(".[Id]");
        sb.append(" AND ").append(vp_alias).append(".ImePolja= '").append(f.getName()).append("' ) ");
        sb.append("INNER JOIN [ChangeLog].[dbo].[VariousValues] ").append(val_alias).append(" ON (");
        sb.append(ev_alias).append("[ValueId] = ").append(val_alias).append(".[Id]");
        List<FieldValue> values = event.getEventValues().get(f);
        if (values != null) {
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
              case 1: //Integer
                sb.append(val_alias).append(".IntValue = ? ");
                break;
              case 2: //Real
                sb.append(val_alias).append(".RealValue = ? ");
                break;
              case 3: //String
                sb.append(val_alias).append(".StringValue = ? ");
                break;
              case 4: //Date
                sb.append(val_alias).append(".DateValue = ? ");
                break;
              case 6: //Clob
                sb.append(val_alias).append(".ClobValue = ? ");
                break;
            }
            parameters.add(fv.getValue());
          }
          sb.append(")");
        }
        sb.append(") ");
      }
      sqlFind.setValue(sb.toString());
      parameters.add(event.getSifrant());
      parameters.add(event.getSifra());
      parameters.add(event.getEventSource());
      DbDataSource.SubstSqlParameter sqlFindDate = new DbDataSource.SubstSqlParameter("<%ev_date_filter%>");
      parameters.add(sqlFindDate);
      if (event.getDatum() == null) {
        sqlFindDate.setValue("");
      } else {
        sqlFindDate.setValue(" AND ev.DATUM = ?");
        parameters.add(event.getDatum());
      }

      ResultSet rs = SQLDataSource.executeQuery(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "find_event_by_values.sql", "cp1250"), parameters);

      if (rs.next()) {
        return findEvent(rs.getLong("Id"));
      } else {
        return null;
      }
    } else {
      return findEvent(event.getId());
    }
  }
}
