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

      fieldValues = new FieldValue[9];

      int pos = 0;
      fieldValues[pos++] = new FieldValue("Source", Types.VARCHAR, "LOG");
      fieldValues[pos++] = new FieldValue("FieldName", Types.VARCHAR, newValue.getName());

      int fieldType;

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
          if (newValue.getValue() != null && newValue.getValue().toString().length() > 108) {
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

      fieldValues[pos++] = new FieldValue("FieldType", Types.INTEGER, fieldType);

      Long newValueId = null;

      if (!newValue.isNull()) {
        switch (fieldType) {
          case 1:
            fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, newValue.getValue());
            fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
            fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
            fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
            fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);

            break;

          case 2:
            fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
            fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, newValue.getValue());
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
            fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
            fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
            fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);

            break;

          case 3:
            fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
            fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, newValue.getValue());
            fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
            fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
            fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);

            break;

          case 4:
            fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
            fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
            fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, newValue.getValue());
            fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
            fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);

            break;

          case 5:
            fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
            fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
            fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
            fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, newValue.getValue());
            fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);

            break;

          case 6:
            fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
            fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
            fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
            fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, newValue.getValue());

            break;
        }

        executeUpdate(logValues, fieldValues);
        newValueId = getLastIdentity();
        pos = 3;
      }



      Long oldValueId = null;

      if (!oldValue.isNull()) {
        switch (fieldType) {
          case 1:
            fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, oldValue.getValue());
            fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
            fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
            fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
            fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);

            break;

          case 2:
            fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
            fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, oldValue.getValue());
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
            fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
            fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
            fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);

            break;

          case 3:
            fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
            fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, oldValue.getValue());
            fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
            fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
            fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);

            break;

          case 4:
            fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
            fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
            fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, oldValue.getValue());
            fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
            fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);

            break;

          case 5:
            fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
            fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
            fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
            fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, oldValue.getValue());
            fieldValues[pos++] = new FieldValue("ClobValue", Types.LONGVARBINARY, null);

            break;

          case 6:
            fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
            fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
            fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
            fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
            fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, oldValue.getValue());

            break;
        }
        executeUpdate(logValues, fieldValues);
        oldValueId = getLastIdentity();
      }

      pos = 0;
      fieldValues = new FieldValue[3];
      fieldValues[pos++] = new FieldValue("ChangeId", Types.BIGINT, changeId);
      fieldValues[pos++] = new FieldValue("NewValueId", Types.BIGINT, newValueId);
      fieldValues[pos++] = new FieldValue("OldValueId", Types.BIGINT, oldValueId);

      executeUpdate(logChangedValues, fieldValues);
    }
  }
}
