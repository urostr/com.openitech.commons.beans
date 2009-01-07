/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.util;

import com.openitech.db.ConnectionManager;
import com.openitech.db.events.StoreUpdatesEvent;
import com.openitech.db.model.DbDataSource;
import com.openitech.util.Equals;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public abstract class SqlUtilities {

  private static final Map<String, Class<? extends SqlUtilities>> implementations = new HashMap<String, Class<? extends SqlUtilities>>();
  private static SqlUtilities instance;
  private boolean autocommit;

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
    return instance;
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
        value = source.getObject(field.name);
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
      String fieldName = fieldValue.name;
      int type = fieldValue.type;
      Object value = fieldValue.value;
      boolean wasNull = fieldValue.isNull();


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
      String fieldName = fieldValue.name;
      int type = fieldValue.type;
      Object value = fieldValue.value;
      boolean wasNull = fieldValue.isNull();


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

    for (FieldValue value : fieldValues) {
      if ((operation==Operation.INSERT)||source.hasChanged(value.name)) {
        newValues.add(value);
        oldValues.add(new FieldValue(value.name, value.type, ((source == null) || (operation==Operation.INSERT)) ? null : source.getOldValue(value.name)));
      }
    }

    logChanges(application, database, tableName, operation, newValues, oldValues);
  }

  protected abstract void logChanges(String application, String database, String tableName, Operation operation, List<FieldValue> newValues, List<FieldValue> oldValues) throws SQLException;

  public boolean beginTransaction() throws SQLException {
    Connection connection = ConnectionManager.getInstance().getConnection();

    if (connection.getAutoCommit()) {
      autocommit = connection.getAutoCommit();

      connection.setAutoCommit(false);
    }
    return !connection.getAutoCommit();
  }

  public boolean endTransaction(boolean commit) throws SQLException {
    Connection connection = ConnectionManager.getInstance().getConnection();

    if (!connection.getAutoCommit()) {
      if (commit) {
        connection.commit();
      } else {
        connection.rollback();
      }
      connection.setAutoCommit(autocommit);
      return true;
    } else {
      return false;
    }
  }

  public Map<Field, Object> getColumnValues(StoreUpdatesEvent event) throws SQLException {
    Map<SqlUtilities.Field, Object> columnValues = new HashMap<SqlUtilities.Field, Object>();
    for (Map.Entry<String, Object> entry : event.getColumnValues().entrySet()) {
      columnValues.put(new SqlUtilities.Field(entry.getKey(), event.getSource().getType(entry.getKey())), entry.getValue());
    }

    return columnValues;
  }

  public abstract long getLastIdentity() throws SQLException;

  public abstract long getScopeIdentity() throws SQLException;

  public abstract long getCurrentIdentity(String tableName) throws SQLException;

  public static class Field {

    String name;
    int type;

    public Field(String name, int type) {
      this.name = name;
      this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (!obj.getClass().isInstance(this)) {
        return false;
      }
      final Field other = (Field) obj;
      if ((this.name == null) ? (other.name != null) : !this.name.equalsIgnoreCase(other.name)) {
        return false;
      }
      return true;
    }

    public String getName() {
      return name;
    }

    public int getType() {
      return type;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 47 * hash + (this.name != null ? this.name.toUpperCase().hashCode() : 0);
      return hash;
    }
  }

  public static class FieldValue extends Field {

    Object value;

    public FieldValue(Field field) {
      super(field.name, field.type);
    }

    public FieldValue(String name, int type) {
      super(name, type);
    }

    public FieldValue(String name, int type, Object value) {
      super(name, type);
      this.value = value;
    }

    public Object getValue() {
      return value;
    }

    public void setValue(Object value) {
      this.value = value;
    }

    public boolean isNull() {
      return value == null;
    }
  }

  public static enum Operation {
    INSERT,
    UPDATE
  }
}
