/*
 * DbDataSource.java
 *
 * Created on April 2, 2006, 11:59 AM
 *
 * $Revision: 1.8 $
 */
package com.openitech.sql;

import com.openitech.db.model.*;
import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.DbDataSource.SqlParameter;
import com.openitech.io.LogWriter;
import com.openitech.io.ReadInputStream;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class SQLWorker {

  private final LogWriter logWriter;

  /** Creates a new instance of DbDataSource */
  public SQLWorker() {
    this(new LogWriter(Logger.getLogger(SQLWorker.class.getName()), Level.INFO));
  }

  public SQLWorker(LogWriter logWriter) {
    this.logWriter = logWriter;
  }

  @Override
  protected void finalize() throws Throwable {
    logWriter.flush();
    super.finalize();
  }

  public String substParameters(String sql, List<?> parameters) {
    if (sql != null && sql.length() > 0) {
      Object value;
      Integer type;

      sql = ReadInputStream.getReplacedSql(sql);

      for (Iterator values = parameters.iterator(); values.hasNext();) {
        value = values.next();
        if (value instanceof DbDataSource.SubstSqlParameter) {
          type = ((DbDataSource.SubstSqlParameter) value).getType();
          if (type.equals(Types.SUBST_ALL)) {
            sql = sql.replaceAll(((DbDataSource.SubstSqlParameter) value).getReplace(), ((DbDataSource.SubstSqlParameter) value).getValue());
          } else if (type.equals(Types.SUBST) || type.equals(Types.SUBST_FIRST)) {
            sql = sql.replaceFirst(((DbDataSource.SubstSqlParameter) value).getReplace(), ((DbDataSource.SubstSqlParameter) value).getValue());
          }
        }
      }
    }
    return sql;
  }

  public List<Object> getParameters(List<?> source) {
    List<Object> target = new ArrayList<Object>();

    getParameters(source, target, 0, false);

    return target;
  }

  private int getParameters(List<?> source, List<Object> target, int pos, boolean subset) {
    if (!subset) {
      target.clear();
    }

    Integer type;

    for (Object value : source) {
      if (value instanceof DbDataSource.SqlParameter) {
        type = ((DbDataSource.SqlParameter) value).getType();
        if (!(type.equals(Types.SUBST_ALL) || type.equals(Types.SUBST) || type.equals(Types.SUBST_FIRST))) {
          if (((DbDataSource.SqlParameter) value).getValue() != null) {
            target.add(((DbDataSource.SqlParameter) value).getValue());
          } else {
            target.add(null);
          }
        } else if ((value instanceof DbDataSource.SubstSqlParameter) && (((DbDataSource.SubstSqlParameter) value).getParameters().size() > 0)) {
          pos = getParameters(((DbDataSource.SubstSqlParameter) value).getParameters(), target, pos, true);
        }
      } else {
        if (value == null) {
          target.add(null);
        } else {
          target.add(value);
        }
      }
    }
    return pos;
  }

  public int setParameters(PreparedStatement statement, List<?> parameters, int pos, boolean subset) throws SQLException {
    if (!subset) {
      statement.clearParameters();
    }

    ParameterMetaData metaData = null;
    int parameterCount = Integer.MAX_VALUE;
    try {
      metaData = statement.getParameterMetaData();
      parameterCount = metaData.getParameterCount();
    } catch (SQLException err) {
      logWriter.println(err.getMessage());
      logWriter.flush(logWriter.getLogger(), Level.WARNING, err);
    }
    Object value;
    Integer type;

    for (Iterator values = parameters.iterator(); (pos <= parameterCount) && values.hasNext();) {
      value = values.next();
      if (value instanceof DbDataSource.SqlParameter) {
        type = ((DbDataSource.SqlParameter) value).getType();
        if (!(type.equals(Types.SUBST_ALL) || type.equals(Types.SUBST) || type.equals(Types.SUBST_FIRST))) {
          if (((DbDataSource.SqlParameter) value).getValue() != null) {
            statement.setObject(pos++, ((DbDataSource.SqlParameter) value).getValue(),
                    ((DbDataSource.SqlParameter) value).getType());
            if (DbDataSource.DUMP_SQL) {
              logWriter.println("--[" + (pos - 1) + "]=" + ((DbDataSource.SqlParameter) value).getValue().toString());
            }
          } else {
            statement.setNull(pos++, ((DbDataSource.SqlParameter) value).getType());
            if (DbDataSource.DUMP_SQL) {
              logWriter.println("--[" + (pos - 1) + "]=null");
            }
          }
        } else if ((value instanceof DbDataSource.SubstSqlParameter) && (((DbDataSource.SubstSqlParameter) value).getParameters().size() > 0)) {
          pos = setParameters(statement, ((DbDataSource.SubstSqlParameter) value).getParameters(), pos, true);
        }
      } else {
        if (value == null) {
          statement.setNull(pos, metaData == null ? java.sql.Types.VARCHAR : metaData.getParameterType(pos++));
          if (DbDataSource.DUMP_SQL) {
            logWriter.println("--[" + (pos - 1) + "]=null");
          }
        } else {
          statement.setObject(pos++, value);
          if (DbDataSource.DUMP_SQL) {
            logWriter.println("--[" + (pos - 1) + "]=" + value.toString());
          }
        }
      }
    }
    if (parameterCount < Integer.MAX_VALUE) {
      while ((pos <= parameterCount) && !subset) {
        statement.setNull(pos, metaData == null ? java.sql.Types.VARCHAR : metaData.getParameterType(pos++));
      }
    }
    return pos;
  }

  public String setParameters(String sql, List<?> parameters) throws SQLException {
    return setParameters(sql, parameters, 1, false);
  }

  public String setParameters(String sql, List<?> parameters, int pos, boolean subset) throws SQLException {

    int parameterCount = Integer.MAX_VALUE;
    int count = 0;
    for (int i = 0; i < sql.length(); i++) {
      char charAt = sql.charAt(i);
      if (charAt == '?') {
        count++;
      }
    }

    parameterCount = count;

    Object value;
    Integer type;

    for (Iterator values = parameters.iterator(); (pos <= parameterCount) && values.hasNext();) {
      value = values.next();
      if (value instanceof DbDataSource.SqlParameter) {
        final SqlParameter sqlParameter = (DbDataSource.SqlParameter) value;
        type = sqlParameter.getType();
        if (!(type.equals(Types.SUBST_ALL) || type.equals(Types.SUBST) || type.equals(Types.SUBST_FIRST))) {
          if (sqlParameter.getValue() != null) {
            if (type == java.sql.Types.VARCHAR) {
              sql = sql.replaceFirst("\\?", "'" + sqlParameter.getValue().toString() + "'");
            } else {
              sql = sql.replaceFirst("\\?", sqlParameter.getValue().toString());
            }
//            statement.setObject(pos++, ((DbDataSource.SqlParameter) value).getValue(),
//                    ((DbDataSource.SqlParameter) value).getType());
            if (DbDataSource.DUMP_SQL) {
              logWriter.println("--[" + (pos - 1) + "]=" + sqlParameter.getValue().toString());
            }
          } else {
            sql = sql.replaceFirst("\\?", "''");
//            statement.setNull(pos++, ((DbDataSource.SqlParameter) value).getType());
            if (DbDataSource.DUMP_SQL) {
              logWriter.println("--[" + (pos - 1) + "]=''");
            }
          }
        } else if ((value instanceof DbDataSource.SubstSqlParameter) && (((DbDataSource.SubstSqlParameter) value).getParameters().size() > 0)) {
          sql = setParameters(sql, ((DbDataSource.SubstSqlParameter) value).getParameters(), pos, true);
        }
      } else {
        if (value == null) {
          sql = sql.replaceFirst("\\?", "''");
//          statement.setNull(pos, metaData == null ? java.sql.Types.VARCHAR : metaData.getParameterType(pos++));
          if (DbDataSource.DUMP_SQL) {
            logWriter.println("--[" + (pos - 1) + "]=''");
          }
        } else {
          if (value instanceof String) {
            sql = sql.replaceFirst("\\?", "'" + value.toString() + "'");
          } else {
            sql = sql.replaceFirst("\\?", value.toString());
          }
//          statement.setObject(pos++, value);
          if (DbDataSource.DUMP_SQL) {
            logWriter.println("--[" + (pos - 1) + "]=" + value.toString());
          }
        }
      }
    }
    if (parameterCount < Integer.MAX_VALUE) {
      while ((pos <= parameterCount) && !subset) {
        sql = sql.replaceFirst("\\?", "''");
        pos++;
//        statement.setNull(pos, metaData == null ? java.sql.Types.VARCHAR : metaData.getParameterType(pos++));
      }
    }
    return sql;
  }

  public ResultSet executeQuery(String selectSQL) throws SQLException {
    return executeQuery(selectSQL, null, ConnectionManager.getInstance().getConnection());
  }

  public ResultSet executeQuery(String selectSQL, List<?> parameters) throws SQLException {
    return executeQuery(selectSQL, parameters, ConnectionManager.getInstance().getConnection());
  }

  public ResultSet executeQuery(String selectSQL, List<?> parameters, Connection connection) throws SQLException {
    return executeQuery(selectSQL, parameters, connection, 1800);
  }

  public ResultSet executeQuery(String selectSQL, List<?> parameters, Connection connection, int timeout) throws SQLException {
    if (parameters == null) {
      parameters = new ArrayList();
    }
    String sql = substParameters(selectSQL, parameters);
    PreparedStatement statement = connection.prepareStatement(sql,
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY,
            ResultSet.HOLD_CURSORS_OVER_COMMIT);

    statement.setQueryTimeout(timeout);

    try {
      if (DbDataSource.DUMP_SQL) {
        logWriter.println("##############");
        logWriter.println(sql);
      }
      return executeQuery(statement, parameters);
    } finally {
      if (DbDataSource.DUMP_SQL) {
        logWriter.println("##############");
        logWriter.flush();
      }
    }
  }

  public ResultSet executeQuery(PreparedStatement statement, List<?> parameters) throws SQLException {
    ResultSet resultSet = null;
//    synchronized (statement.getConnection()) {
    try {
      List<Object> queryParameters = preprocessParameters(parameters, statement);
      setParameters(statement, queryParameters, 1, false);
    } finally {
      if (DbDataSource.DUMP_SQL) {
        logWriter.flush();
      }
    }
    resultSet = statement.executeQuery();
//    }
    return resultSet;
  }

  public boolean execute(String selectSQL, Object... parameters) throws SQLException {
    List<Object> l = new ArrayList<Object>();
    for (Object p : parameters) {
      l.add(p);
    }
    return execute(selectSQL, l);
  }

  public boolean execute(String selectSQL, List<?> parameters) throws SQLException {
    return execute(selectSQL, parameters, ConnectionManager.getInstance().getConnection());
  }

  public boolean execute(String selectSQL, final ConnectionType connectionType) throws SQLException {

    PreparedStatement statement = connectionType.getConnection().prepareStatement(selectSQL);

    return execute(statement, new ArrayList<Object>());
  }

  public boolean execute(String selectSQL, List<?> parameters, final ConnectionType connectionType) throws SQLException {
    String sql = substParameters(selectSQL, parameters);
    PreparedStatement statement = connectionType.getConnection().prepareStatement(sql);

    return execute(statement, parameters);
  }

  public boolean execute(String selectSQL, List<?> parameters, final java.sql.Connection connection) throws SQLException {
    String sql = substParameters(selectSQL, parameters);
    PreparedStatement statement = connection.prepareStatement(sql);

    return execute(statement, parameters);
  }

  public boolean execute(PreparedStatement statement, List<?> parameters) throws SQLException {
    setParameters(statement, parameters, 1, false);

    return statement.execute();
  }

  public int executeUpdate(String selectSQL, List<?> parameters) throws SQLException {
    return executeUpdate(selectSQL, parameters, ConnectionManager.getInstance().getConnection());
  }

  public int executeUpdate(String selectSQL, List<?> parameters, final java.sql.Connection connection) throws SQLException {
    String sql = substParameters(selectSQL, parameters);
    PreparedStatement statement = connection.prepareStatement(sql);

    try {
      if (DbDataSource.DUMP_SQL) {
        logWriter.println("##############");
        logWriter.println(sql);
      }
      return executeUpdate(statement, parameters);
    } finally {
      if (DbDataSource.DUMP_SQL) {
        logWriter.println("##############\n");
        logWriter.flush();
      }
    }
  }

  public int executeUpdate(PreparedStatement statement, List<?> parameters) throws SQLException {
    int result = 0;
//    synchronized (statement.getConnection()) {
    List<Object> queryParameters = preprocessParameters(parameters, statement);

    setParameters(statement, queryParameters, 1, false);

    result = statement.executeUpdate();
//    }
    return result;
  }

  public List<Object> preprocessParameters(List<?> parameters, Connection connection) throws SQLException {
    return (List<Object>) DbDataSourceParametersPreprocessor.getInstance().preprocess(parameters, connection);
  }

  private List<Object> preprocessParameters(List<?> parameters, PreparedStatement statement) throws SQLException {
    return preprocessParameters(parameters, statement.getConnection());
  }

  public static enum ConnectionType {

    Connection {

      @Override
      public Connection getConnection() {
        return ConnectionManager.getInstance().getConnection();
      }
    },
    TxConnection {

      @Override
      public Connection getConnection() {
        return ConnectionManager.getInstance().getTxConnection();
      }
    },
    TemporaryConection {

      @Override
      public Connection getConnection() throws SQLException {
        return ConnectionManager.getInstance().getTemporaryConnection();
      }
    };

    public abstract Connection getConnection() throws SQLException;
  }
}
