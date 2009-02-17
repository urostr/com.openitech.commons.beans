/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.util.mssql;

import com.openitech.db.ConnectionManager;
import com.openitech.sql.util.SqlUtilities;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

/**
 *
 * @author uros
 */
public class SqlUtilitesImpl extends SqlUtilities {

  PreparedStatement logChanges;
  PreparedStatement logValues;
  PreparedStatement logChangedValues;
  PreparedStatement find_datevalue;
  PreparedStatement find_intvalue;
  PreparedStatement find_realvalue;
  PreparedStatement find_stringvalue;

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

      int fieldType;
      final Object value = newValue.getValue();

      switch (newValue.getType()) {
        case Types.BIT:
        case Types.BOOLEAN:
        case Types.TINYINT:
        case Types.SMALLINT:
        case Types.BIGINT:
        case Types.INTEGER:
          fieldType = 1;
          break;
        case Types.FLOAT:
        case Types.REAL:
        case Types.DOUBLE:
        case Types.DECIMAL:
        case Types.NUMERIC:
          fieldType = 2;
          break;
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
          if (value != null && value.toString().length() > 108) {
            fieldType = 6;
          } else {
            fieldType = 3;
          }
          break;
        case Types.DATE:
        case Types.TIME:
        case Types.TIMESTAMP:
          fieldType = 4;
          break;
        default:
          fieldType = 5;
      }
      Long newValueId = storeValue(fieldType, value);
      Long oldValueId =  storeValue(fieldType, oldValue.getValue());

      fieldValues = new FieldValue[] {
        new FieldValue("ChangeId", Types.BIGINT, changeId),
        new FieldValue("FieldName", Types.VARCHAR, newValue.getName()),
        new FieldValue("NewValueId", Types.BIGINT, newValueId),
        new FieldValue("OldValueId", Types.BIGINT, oldValueId)
      };

      executeUpdate(logChangedValues, fieldValues);
    }
  }

  @Override
  public Long storeValue(int fieldType, final Object value) throws SQLException {
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
      if (newValueId==null) {
        executeUpdate(logValues, fieldValues);
        newValueId = getLastIdentity();
      }
    }
    return newValueId;
  }
}
