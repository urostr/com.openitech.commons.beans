/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.util;

import com.openitech.value.events.EventPK;
import com.openitech.value.events.UpdateEvent;
import com.openitech.value.fields.FieldValue;
import com.openitech.value.fields.Field;
import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.components.DbNaslovDataModel;
import com.openitech.db.events.StoreUpdatesEvent;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.text.CaseInsensitiveString;
import com.openitech.value.fields.ValueType;
import com.openitech.value.events.ActivityEvent;
import com.openitech.value.events.Event;
import com.openitech.value.events.EventQuery;
import com.openitech.util.Equals;
import com.openitech.value.events.EventQueryParameter;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author uros
 */
public abstract class SqlUtilities extends TransactionManager implements UpdateEvent {

  private static final Map<String, Class<? extends SqlUtilities>> implementations = new HashMap<String, Class<? extends SqlUtilities>>();
  private static SqlUtilities instance;

  public static Properties DATABASES = new Properties();
  public static final String CHANGE_LOG_DB = "[ChangeLog]";
  public static final String RPP_DB = "[RPP]";
  public static final String RPE_DB = "[RPE]";
  public static final String MVIEW_CACHE_DB = "[MViewCache]";
  private static Map<Class, SqlUtilities> instances = new HashMap<Class, SqlUtilities>();

  protected SqlUtilities() {
  }

  public static void register() {
    if (implementations.isEmpty()) {
      register("mssql", com.openitech.sql.util.mssql.SqlUtilitesImpl.class);
    }
  }

  public static void register(String dialect, Class<? extends SqlUtilities> implementation) {
    implementations.put(dialect, implementation);
  }

  public static SqlUtilities getInstance() {
    if (instance == null) {
      register();
      Class implementation = implementations.get(ConnectionManager.getInstance().getDialect());
      if (implementation != null) {
        try {
          instance = (SqlUtilities) implementation.newInstance();
          try {
            instance.autocommit = ConnectionManager.getInstance().getConnection().getAutoCommit();
          } catch (SQLException ex) {
            //ignore it
            instance.autocommit = true;
          }
        } catch (InstantiationException ex) {
          Logger.getLogger(SqlUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
          Logger.getLogger(SqlUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return instance;
  }

  public static SqlUtilities getInstance(Class clazz) {
    if (!instances.containsKey(clazz)) {
      Class implementation = clazz;
      SqlUtilities instance = null;
      if (implementation != null) {
        try {
          instance = (SqlUtilities) implementation.newInstance();
          try {
            instance.autocommit = ConnectionManager.getInstance().getConnection().getAutoCommit();
          } catch (SQLException ex) {
            //ignore it
            instance.autocommit = true;
          }
        } catch (InstantiationException ex) {
          Logger.getLogger(SqlUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
          Logger.getLogger(SqlUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        instances.put(clazz, instance);
      }
    }
    return instances.get(clazz);
  }

  public int executeUpdate(java.sql.PreparedStatement statement,
          java.util.Map<Field, Object> columnValues,
          Field... fields) throws SQLException {
    return executeUpdate(statement, null, columnValues == null ? new java.util.HashMap<Field, Object>() : columnValues, fields);
  }

  public int executeUpdate(java.sql.PreparedStatement statement,
          com.openitech.db.model.DbDataSource source,
          String... fieldNames) throws SQLException {
    Field[] fields = new Field[fieldNames.length];
    for (int pos = 0; pos < fields.length; pos++) {
      fields[pos] = new Field(fieldNames[pos], source.getType(fieldNames[pos]));
    }
    return executeUpdate(statement, source, null, fields);
  }

  private int executeUpdate(java.sql.PreparedStatement statement,
          com.openitech.db.model.DbDataSource source,
          java.util.Map<Field, Object> columnValues,
          Field... fields) throws SQLException {
    FieldValue[] fieldValues = new FieldValue[fields.length];

    for (int pos = 1; pos <= fields.length; pos++) {
      Field field = fields[pos - 1];
      Object value;
      boolean wasNull = true;

      if (source != null) {
        value = source.getObject(field.getName());
        wasNull = source.wasNull();
      } else {
        value = columnValues.get(field);
        wasNull = value == null;
      }

      fieldValues[pos - 1] = new FieldValue(field);
      fieldValues[pos - 1].setValue(wasNull ? null : value);
    }

    return executeUpdate(statement, fieldValues);
  }

  public int executeUpdate(java.sql.PreparedStatement statement,
          FieldValue... fieldValues) throws SQLException {
    statement.clearParameters();
    System.out.println("Setting parameters");
    for (int pos = 1; pos <= fieldValues.length; pos++) {
      FieldValue fieldValue = fieldValues[pos - 1];
      final String fieldName = fieldValue.getName();
      final int type = fieldValue.getType();
      final Object value = fieldValue.getValue();
      final boolean wasNull = fieldValue.isNull();


      System.out.println(pos + ":" + fieldName + ":" + type + ":" + (wasNull ? "null" : value.toString()));
      if (wasNull) {
        statement.setNull(pos, type);
      } else {
        switch (type) {
          case Types.DECIMAL:
          case Types.DOUBLE:
          case Types.FLOAT:
            if (value instanceof Number) {
              statement.setDouble(pos, ((Number) value).doubleValue());
            } else {
              statement.setDouble(pos, Double.parseDouble(value.toString()));
            }
            break;
          case Types.BIT:
          case Types.INTEGER:
            if (value instanceof Number) {
              statement.setInt(pos, ((Number) value).intValue());
              break;
            } else {
              statement.setInt(pos, Integer.parseInt(value.toString()));
            }
            break;
          case Types.BOOLEAN:
            if (value instanceof Number) {
              statement.setBoolean(pos, Equals.equals(value, 1));
            } else if (value instanceof String) {
              String svalue = value.toString().trim().toUpperCase();
              statement.setBoolean(pos, svalue.length() > 0 && !(svalue.equals("0") || svalue.startsWith("N") || svalue.startsWith("F")));
            }
            break;
          case Types.CHAR:
          case Types.VARCHAR:
            statement.setString(pos, value.toString());
            break;
          default:
            statement.setObject(pos, value, type);
        }
      }
    }

    return statement.executeUpdate();
  }

  public java.sql.ResultSet executeQuery(java.sql.PreparedStatement statement,
          FieldValue... fieldValues) throws SQLException {
    statement.clearParameters();
    System.out.println("Setting parameters");
    for (int pos = 1; pos <= fieldValues.length; pos++) {
      FieldValue fieldValue = fieldValues[pos - 1];
      final String fieldName = fieldValue.getName();
      final int type = fieldValue.getType();
      final Object value = fieldValue.getValue();
      final boolean wasNull = fieldValue.isNull();


      System.out.println(pos + ":" + fieldName + ":" + type + ":" + (wasNull ? "null" : value.toString()));
      if (wasNull) {
        statement.setNull(pos, type);
      } else {
        switch (type) {
          case Types.FLOAT:
          case Types.REAL:
          case Types.DOUBLE:
          case Types.DECIMAL:
          case Types.NUMERIC:
            if (value instanceof Number) {
              statement.setDouble(pos, ((Number) value).doubleValue());
            } else {
              statement.setDouble(pos, Double.parseDouble(value.toString()));
            }
            break;
          case Types.BIT:
          case Types.TINYINT:
          case Types.SMALLINT:
          case Types.BIGINT:
          case Types.INTEGER:
            if (value instanceof Number) {
              statement.setInt(pos, ((Number) value).intValue());
              break;
            } else {
              statement.setInt(pos, Integer.parseInt(value.toString()));
            }
            break;
          case Types.BOOLEAN:
            if (value instanceof Number) {
              statement.setBoolean(pos, Equals.equals(value, 1));
            } else if (value instanceof String) {
              String svalue = value.toString().trim().toUpperCase();
              statement.setBoolean(pos, svalue.length() > 0 && !(svalue.equals("0") || svalue.startsWith("N") || svalue.startsWith("F")));
            }
            break;
          case Types.CHAR:
          case Types.VARCHAR:
          case Types.LONGVARCHAR:
            statement.setString(pos, value.toString());
            break;
          default:
            statement.setObject(pos, value, type);
        }
      }
    }

    return statement.executeQuery();
  }

  private java.util.List<String> getPrimaryKeys(java.sql.Connection connection, String tableName) throws SQLException {

    java.sql.DatabaseMetaData metaData = connection.getMetaData();

    java.sql.ResultSet rs_keys = metaData.getPrimaryKeys(null, null, tableName);
    ArrayList<String> keys = new ArrayList<String>();
    while (rs_keys.next()) {
      keys.ensureCapacity(rs_keys.getInt("KEY_SEQ"));
      for (int pos = keys.size(); pos < rs_keys.getInt("KEY_SEQ"); pos++) {
        keys.add(null);
      }
      keys.set(rs_keys.getInt("KEY_SEQ") - 1, rs_keys.getString("COLUMN_NAME"));
    }
    rs_keys.close();

    return keys;
  }

  public void logChanges(String application, String database, String tableName, Operation operation, com.openitech.db.model.DbDataSource source, java.util.Map<Field, Object> columnValues,
          String... fieldNames) throws SQLException {

    FieldValue[] fieldValues = new FieldValue[fieldNames.length];

    for (int pos = 1; pos <= fieldNames.length; pos++) {
      Field field = new Field(fieldNames[pos - 1], source.getType(fieldNames[pos - 1]));

      fieldValues[pos - 1] = new FieldValue(field);
      fieldValues[pos - 1].setValue(columnValues.get(field));

    }

    logChanges(application, database, tableName, operation, source, fieldValues);
  }

  public void logChanges(
          String application,
          String database,
          String tableName,
          Operation operation,
          FieldValue... fieldValues) throws SQLException {
    logChanges(application, database, tableName, operation, (com.openitech.db.model.DbDataSource) null, fieldValues);
  }

  public void logChanges(String application, String database, String tableName, Operation operation, com.openitech.db.model.DbDataSource source,
          FieldValue... fieldValues) throws SQLException {
    List<FieldValue> newValues = new ArrayList<FieldValue>(fieldValues.length);
    List<FieldValue> oldValues = new ArrayList<FieldValue>(fieldValues.length);

    java.util.List<String> keys = new java.util.ArrayList<String>();

    if (source != null) {
      keys = getPrimaryKeys(source.getConnection(), tableName);
    }

    for (FieldValue value : fieldValues) {
      boolean isPrimaryKey = false;
      final String name = value.getName();

      for (int i = 0; i < keys.size(); i++) {
        isPrimaryKey = keys.get(i).equalsIgnoreCase(name);
        if (isPrimaryKey) {
          break;
        }
      }
      value.setLogAlways(isPrimaryKey || value.isLogAlways());

      if ((operation != Operation.UPDATE) || (source != null && source.hasChanged(name)) || value.isLogAlways()) {
        newValues.add(value);
        oldValues.add(new FieldValue(name, value.getType(), ((source == null) || (operation == Operation.INSERT)) ? null : source.getOldValue(name)));
      }
    }

    logChanges(application, database, tableName, operation, newValues, oldValues);
  }

  protected abstract void logChanges(String application, String database, String tableName, Operation operation, List<FieldValue> newValues, List<FieldValue> oldValues) throws SQLException;

  public Map<Field, Object> getColumnValues(DbDataSource source) throws SQLException {
    ResultSetMetaData metaData = source.getMetaData();

    Map<Field, Object> columnValues = new HashMap<Field, Object>();
    for (int field = 1; field <= metaData.getColumnCount(); field++) {
      columnValues.put(new Field(metaData.getColumnName(field), metaData.getColumnType(field)), source.getObject(field));
    }

    return columnValues;
  }

  public Map<Field, Object> getColumnValues(StoreUpdatesEvent event) throws SQLException {
    Map<Field, Object> columnValues = getColumnValues(event.getSource());
    if (event.getColumnValues() != null) {
      for (Map.Entry<String, Object> entry : event.getColumnValues().entrySet()) {
        columnValues.put(new Field(entry.getKey(), event.getSource().getType(entry.getKey())), entry.getValue());
      }
    }

    return columnValues;
  }

  public abstract long getLastIdentity() throws SQLException;

  public abstract long getScopeIdentity() throws SQLException;

  public abstract long getCurrentIdentity(String tableName) throws SQLException;

  public Event findEvent(int sifrant, String sifra, FieldValue... fieldValues) throws SQLException {
    Event search = new Event(sifrant, sifra);
    if (fieldValues != null) {
      for (FieldValue fieldValue : fieldValues) {
        search.addValue(fieldValue);
      }
    }
    return findEvent(search);
  }

  public abstract Event findEvent(Event event) throws SQLException;

  public Long updateEvent(Event event) throws SQLException {
    return updateEvent(event, event); //event vsebuje eventId oz. se dodaja
  }

  @Override
  public Long updateEvent(Event newValues, Event oldValues) throws SQLException {
    boolean isTransaction = isTransaction();
    boolean commit = false;
    try {
      if (!isTransaction) {
        beginTransaction();
      }

      List<Long> eventIds = new ArrayList<Long>();
      Long eventId = updateEvent(newValues, oldValues, eventIds);

      if (newValues.isVersioned()) {
        assignEventVersion(eventIds);
      }

      commit = eventId != null;
      return eventId;
    } finally {
      if (!isTransaction) {
        endTransaction(commit);
      }
    }
  }

  private Long updateEvent(Event newValues, Event oldValues, List<Long> eventIds) throws SQLException {
    Event find = findEvent(oldValues);
    if (find != null) {
      newValues.setId(find.getId());
      newValues.setEventSource(find.getEventSource());
    }
    if (eventIds == null) {
      eventIds = new ArrayList<Long>();
    }

    for (Event childEvent : newValues.getChildren()) {
      if (childEvent.getOperation() != Event.EventOperation.IGNORE) {
        updateEvent(childEvent, childEvent, eventIds);
      } else {
        addIngoredEventIds(childEvent, eventIds);
      }
    }
    Long eventId = storeEvent(newValues, find);

    eventIds.add(eventId);

    return eventId;
  }

  private void addIngoredEventIds(Event newValues, List<Long> eventIds) {
    if ((newValues.getId() != null) && (newValues.getId() > 0)) {
      eventIds.add(newValues.getId());
      for (Event childEvent : newValues.getChildren()) {
        addIngoredEventIds(childEvent, eventIds);
      }
    }
  }

  protected abstract Long assignEventVersion(List<Long> eventIds) throws SQLException;

  public abstract Map<CaseInsensitiveString, Field> getPreparedFields() throws SQLException;

  public abstract FieldValue getNextIdentity(Field field) throws SQLException;

  public abstract FieldValue getParentIdentity(Field field) throws SQLException;

  public Long storeEvent(Event event) throws SQLException {
    return storeEvent(event, null);
  }

  public abstract Long storeEvent(Event event, Event oldEvent) throws SQLException;

  public CachedRowSet getGeneratedFields(int idSifranta, String idSifre) throws SQLException {
    return getGeneratedFields(idSifranta, idSifre, false);
  }

  public CachedRowSet getGeneratedFields(int idSifranta, String idSifre, boolean visibleOnly) throws SQLException {
    return getGeneratedFields(idSifranta, idSifre, visibleOnly, null);
  }

  public abstract CachedRowSet getGeneratedFields(int idSifranta, String idSifre, boolean visibleOnly, ActivityEvent activityEvent) throws SQLException;

  public Long storeValue(int fieldType, final Object value) throws SQLException {
    return storeValue(ValueType.valueOf(fieldType), value);
  }

  public abstract Long storeValue(ValueType valueType, final Object value) throws SQLException;

  public abstract DbNaslovDataModel.Naslov storeAddress(DbNaslovDataModel.Naslov address) throws SQLException;

  public DbDataSource getDsSifrantModel(java.util.List<Object> parameters) throws SQLException {
    return getDsSifrantModel("", parameters);
  }

  public abstract DbDataSource getDsSifrantModel(String dataBase, java.util.List<Object> parameters) throws SQLException;

  public EventQuery prepareEventQuery(Event event, Set<Field> searchFields, Set<Field> resultFields) {
    return prepareEventQuery(event, searchFields, resultFields, event.getSifrant(), new String[]{event.getSifra()}, true, false);
  }

  public abstract EventQueryParameter getEventQueryParameter(Map<Field, DbDataSource.SqlParameter<Object>> namedParameters, Integer eventSource, java.util.Date eventDatum, int sifrant, String[] sifra, boolean validOnly);

  public abstract EventQuery prepareEventQuery(Event parent, Set<Field> searchFields, Set<Field> resultFields, int sifrant, String[] sifra, boolean validOnly, boolean lastEntryOnly);

  public abstract Map<String, com.openitech.db.model.xml.config.TemporaryTable> getCachedTemporaryTables();

  public void storeCachedTemporaryTable(TemporarySubselectSqlParameter ttsql) {
    if (ttsql.getSqlMaterializedView() != null) {
      storeCachedTemporaryTable(ttsql.getTemporaryTable());
    }
  }

  public abstract void storeCachedTemporaryTable(com.openitech.db.model.xml.config.TemporaryTable tt);

  public abstract com.openitech.db.model.xml.config.MaterializedView getCacheDefinition(String table);
  /*
   * Sestavi SQL s katerim lahko pripravimo tabelo za shranjevanje rezultatov v podanem result set.
   */

  public abstract String getCreateTableSQL(String tableName, java.sql.ResultSet rs) throws SQLException;

  public abstract boolean storePrimaryKey(EventPK eventPK) throws SQLException;

  public abstract EventPK findEventPK(long eventId) throws SQLException;

  public abstract boolean deleteEvent(long eventId) throws SQLException;

  public static enum Operation {

    INSERT,
    UPDATE,
    DELETE
  }
}
