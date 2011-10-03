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
import com.openitech.db.connection.DbConnection;
import com.openitech.db.events.StoreUpdatesEvent;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.text.CaseInsensitiveString;
import com.openitech.value.fields.ValueType;
import com.openitech.value.events.ActivityEvent;
import com.openitech.value.events.Event;
import com.openitech.value.events.EventQuery;
import com.openitech.util.Equals;
import com.openitech.value.VariousValue;
import com.openitech.value.events.AfterUpdateEvent;
import com.openitech.value.events.EventType;
import com.openitech.value.events.UpdateEventFields;
import java.sql.Clob;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.serial.SerialClob;

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
            instance.autocommit = instance.getConnection().getAutoCommit();
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

  public static String getEventsDB() {
    return DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB);
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

  public boolean execute(final java.sql.CallableStatement statement,
          FieldValue... fieldValues) throws SQLException {
    synchronized (statement) {
      statement.clearParameters();
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Setting parameters");
      for (int pos = 1; pos <= fieldValues.length; pos++) {
        FieldValue fieldValue = fieldValues[pos - 1];
        final String fieldName = fieldValue.getName();
        final int type = fieldValue.getType();
        final Object value = fieldValue.getValue();
        final boolean wasNull = fieldValue.isNull();


        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(pos + ":" + fieldName + ":" + type + ":" + (wasNull ? "null" : value.toString()));
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
            case Types.CLOB:
            case Types.VARCHAR:
              if (value instanceof Clob || value instanceof SerialClob) {
                statement.setString(pos, ((Clob) value).getSubString(1L, (int) ((Clob) value).length()));
              } else {
                statement.setString(pos, value.toString());
              }
              break;
            case Types.BLOB:
            case Types.LONGVARBINARY:
              if (value instanceof byte[]) {
                statement.setBytes(pos, (byte[]) value);
              } else {
                statement.setObject(pos, value, type);
              }
              break;
            case Types.BIGINT:
              if (value instanceof Number) {
                statement.setLong(pos, ((Number) value).longValue());
              } else if (value instanceof Boolean) {
                boolean temp = ((Boolean) value).booleanValue();
                statement.setLong(pos, temp ? 1 : 0);
              } else {
                statement.setLong(pos, Long.parseLong(value.toString()));
              }
              break;
            default:
              statement.setObject(pos, value, type);
          }
        }
      }

      return statement.execute();
    }
  }

  public int executeUpdate(final java.sql.PreparedStatement statement,
          FieldValue... fieldValues) throws SQLException {
    synchronized (statement) {
      statement.clearParameters();
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("-- Setting parameters");
      for (int pos = 1; pos <= fieldValues.length; pos++) {
        FieldValue fieldValue = fieldValues[pos - 1];
        final String fieldName = fieldValue.getName();
        final int type = fieldValue.getType();
        final Object value = fieldValue.getValue();
        final boolean wasNull = fieldValue.isNull();


        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("--" + pos + ":" + fieldName + ":" + type + ":" + (wasNull ? "null" : value.toString()));
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
            case Types.CLOB:
            case Types.VARCHAR:
              if (value instanceof Clob || value instanceof SerialClob) {
                statement.setString(pos, ((Clob) value).getSubString(1L, (int) ((Clob) value).length()));
              } else {
                statement.setString(pos, value.toString());
              }
              break;
            default:
              statement.setObject(pos, value, type);
          }
        }
      }

      return statement.executeUpdate();
    }
  }

  public java.sql.ResultSet executeQuery(final java.sql.PreparedStatement statement,
          FieldValue... fieldValues) throws SQLException {
    synchronized (statement) {
      statement.clearParameters();
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Setting parameters");
      for (int pos = 1; pos <= fieldValues.length; pos++) {
        FieldValue fieldValue = fieldValues[pos - 1];
        final String fieldName = fieldValue.getName();
        final int type = fieldValue.getType();
        final Object value = fieldValue.getValue();
        final boolean wasNull = fieldValue.isNull();


        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(pos + ":" + fieldName + ":" + type + ":" + (wasNull ? "null" : value.toString()));
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
            case Types.CLOB:
            case Types.VARCHAR:
              if (value instanceof Clob || value instanceof SerialClob) {
                statement.setString(pos, ((Clob) value).getSubString(1L, (int) ((Clob) value).length()));
              } else {
                statement.setString(pos, value.toString());
              }
              break;
            default:
              statement.setObject(pos, value, type);
          }
        }
      }

      return statement.executeQuery();
    }
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

  public abstract void logActions(List<LogRecord> logRecords);

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

  public abstract Event findEvent(Long eventId) throws SQLException;

  public abstract Event findEvent(Event event) throws SQLException;

  public Long updateEvent(Event event) throws SQLException {
    return updateEvent(event, event); //event vsebuje eventId oz. se dodaja
  }

  @Override
  public Long updateEvent(Event newValues, Event oldValues) throws SQLException {
    boolean isTransaction = isTransaction();
    boolean commit = false;
    boolean success = true;

    if (!isTransaction) {
      beginTransaction();
    }

    try {
      Map<CaseInsensitiveString, Field> preparedFields;

      if ((newValues.getPreparedFields() == null) && (oldValues.getPreparedFields() == null)) {
        preparedFields = getPreparedFields();
      } else if (newValues.getPreparedFields() == null) {
        preparedFields = oldValues.getPreparedFields();
      } else {
        preparedFields = newValues.getPreparedFields();
      }

      newValues.setPreparedFields(preparedFields);
      oldValues.setPreparedFields(preparedFields);

      List<EventPK> eventPKs = new ArrayList<EventPK>();
      Long eventId = updateEvent(newValues, oldValues, eventPKs);

      success = success && eventId != null;


      Integer versionId = null;
      //za razumevanje in konsistentnost raje vzamem versionedparent in ne samo newvalues, ceprav sta ista
      versionId = assignEventVersion(newValues.getVersionParent(), eventPKs);

      if (Boolean.valueOf(ConnectionManager.getInstance().getProperty(DbConnection.DB_SAVE_PK, Boolean.toString(true)))) {
        for (EventPK eventPK : eventPKs) {
          eventPK.setVersionID(versionId);
          success = success && storePrimaryKeyVersions(eventPK);
        }
      }


      commit = success;
      return eventId;
    } finally {
      if (!isTransaction) {
        endTransaction(commit);
      }
    }
  }

  private Long updateEvent(Event newValues, Event oldValues, List<EventPK> eventIds/*, List<Event> oldEvents//*/) throws SQLException {
    Event find = findEvent(oldValues);
    if (find != null) {
      newValues.setId(find.getId());
      newValues.setEventSource(find.getEventSource());
      find.setVersioned(newValues.isVersioned());
    }
    if (eventIds == null) {
      eventIds = new ArrayList<EventPK>();
    }

    for (UpdateEventFields updateEventFields : newValues.getUpdateEventFields()) {
      updateEventFields.updateEventFields(newValues, find);
    }

    for (Event childEvent : newValues.getChildren()) {
      if (childEvent.getOperation() != Event.EventOperation.IGNORE) {
        updateEvent(childEvent, childEvent, eventIds);
      } else {
        addIngoredEventIds(childEvent, eventIds);
      }
    }
    EventPK eventPK = storeEvent(newValues, find);

    newValues.setId(eventPK.getEventId());
    for (AfterUpdateEvent afterUpdateEvent : newValues.getAfterUpdateEvent()) {
      afterUpdateEvent.afterUpdateEvent(newValues, find);
    }

    updateEventsCache(newValues, false);

    if (eventPK.getEventOperation() == Event.EventOperation.UPDATE) {
      eventIds.add(eventPK);
    }
    return eventPK.getEventId();
  }

  private void addIngoredEventIds(Event newValues, List<EventPK> eventIds) {
    if ((newValues.getId() != null) && (newValues.getId() > 0)) {
      eventIds.add(newValues.getEventPK());
      for (Event childEvent : newValues.getChildren()) {
        addIngoredEventIds(childEvent, eventIds);
      }
    }
  }

  public void updateEventsCache(Event event) throws SQLException {
    if (event.getOperation() != Event.EventOperation.IGNORE) {
      for (Event childEvent : event.getChildren()) {
        updateEventsCache(childEvent);
      }
      cacheEvent(event);
    }
  }

  public void updateEventsCache(Event event, boolean isValid) throws SQLException {
    if (event.getOperation() != Event.EventOperation.IGNORE) {
      for (Event childEvent : event.getChildren()) {
        updateEventsCache(childEvent, isValid);
      }
    }
  }

  @Override
  protected abstract boolean isTransactionValid() throws SQLException;

  protected abstract void updateVersion(int oldVersion, List<Long> parentEventIds, List<Long> oldParentEventIds) throws SQLException;

  protected abstract long storeVersion(EventType eventType) throws SQLException;

  protected abstract void cacheEvent(Event event) throws SQLException;

  protected abstract void invalidateCacheEvent(Event event) throws SQLException;

  protected abstract Integer assignEventVersion(EventType parent, List<EventPK> eventIds) throws SQLException;

  public abstract Map<CaseInsensitiveString, Field> getPreparedFields() throws SQLException;

  public FieldValue getNextIdentity(Field field) throws SQLException {
    return getNextIdentity(field, null);
  }

  public abstract FieldValue getNextIdentity(Field field, Object initValue) throws SQLException;

  protected EventPK storeEvent(Event event) throws SQLException {
    return storeEvent(event, null);
  }

  protected abstract EventPK storeEvent(Event event, Event oldEvent) throws SQLException;

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

  public abstract VariousValue findValue(long valueId) throws SQLException;

  public abstract DbNaslovDataModel.Naslov storeAddress(DbNaslovDataModel.Naslov address) throws SQLException;

  public DbDataSource getDsSifrantModel(java.util.List<Object> parameters) throws SQLException {
    return getDsSifrantModel("", parameters);
  }

  public abstract DbDataSource getDsSifrantModel(String dataBase, java.util.List<Object> parameters) throws SQLException;

  public EventQuery prepareEventQuery(Event event, Set<Field> searchFields, Set<Field> resultFields) {
    return prepareEventQuery(event, searchFields, resultFields, event.getSifrant(), new String[]{event.getSifra()}, true, false);
  }

  public abstract EventQuery prepareEventQuery(Event parent, Set<Field> searchFields, Set<Field> resultFields, int sifrant, String[] sifra, boolean validOnly, boolean lastEntryOnly);

  public abstract DbDataSource joinSecondaryDataSources(List<DbDataSource> dataSources) throws SQLException;

  public abstract Map<String, com.openitech.db.model.xml.config.TemporaryTable> getCachedTemporaryTables();

  public abstract boolean getSearchByEventPK(int idSifranta, String... idSifre);

  public void storeCachedTemporaryTable(TemporarySubselectSqlParameter ttsql) {
    if (ttsql.getSqlMaterializedView() != null) {
      storeCachedTemporaryTable(ttsql.getTemporaryTable());
    }
  }

  public abstract void storeCachedTemporaryTable(com.openitech.db.model.xml.config.TemporaryTable tt);

  public abstract com.openitech.db.model.xml.config.MaterializedView getCacheDefinition(String table);

  public abstract com.openitech.db.model.xml.config.MaterializedView getCacheDefinition(String table, int idSifranta, String idSifre);
  /*
   * Sestavi SQL s katerim lahko pripravimo tabelo za shranjevanje rezultatov v podanem result set.
   */

  public abstract String getCreateTableSQL(String tableName, java.sql.ResultSet rs) throws SQLException;

  public abstract boolean storePrimaryKey(EventPK eventPK) throws SQLException;

  public abstract boolean storePrimaryKeyVersions(EventPK eventPK) throws SQLException;

  public abstract boolean storeEventLookUpKeys(Long eventId, List<FieldValue> fieldValues) throws SQLException;

  public abstract EventPK findEventPK(long eventId) throws SQLException;

  public abstract EventPK findEventPKVersions(long eventId, Integer versionId) throws SQLException;

  public abstract boolean deleteEvent(long eventId) throws SQLException;

  public abstract Integer findVersion(Long eventId) throws SQLException;

  public abstract EventPK findEventPKVersions(Integer idSifranta, String idSifre, Integer versionId, String primaryKey) throws SQLException;

  public abstract String getPPSelectFields();

  public abstract String getPPJoinFields();

  public abstract Clob getWorkArea(int workAreaId) throws SQLException;

  public abstract Set<String> getEventViewColumns(String viewName);

  public void createEventViews(int idSifranta, String idSifre) {
    createEventViews(idSifranta, idSifre, getRunParameterBoolean(ConnectionManager.DB_OVERRIDE_VIEWS), true);
  }
  public void createEventViews(int idSifranta, String idSifre, boolean createIndexPK) {
    createEventViews(idSifranta, idSifre, getRunParameterBoolean(ConnectionManager.DB_OVERRIDE_VIEWS), createIndexPK);
  }

  public abstract void createEventViews(int idSifranta, String idSifre, boolean overrideIfExists, boolean createIndexPK);

  public abstract String getViewName(int idSifranta, String idSifre, boolean valid);

  public boolean getRunParameterBoolean(String parameter) {
    return getRunParameterBoolean(parameter, false);
  }

  public abstract boolean getRunParameterBoolean(String parameter, boolean defaultValue);

  public abstract void loadCaches() throws SQLException;
  
  public abstract String getDataSourceSQL(int idSifranta, String idSifre) throws SQLException;

  public static enum Operation {

    INSERT,
    UPDATE,
    DELETE
  }
}
