/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.sql.util;

import com.openitech.db.ConnectionManager;
import com.openitech.db.model.DbDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public abstract class SqlUtilities {
  private static final Map<String,Class<? extends SqlUtilities>> implementations = new HashMap<String,Class<? extends SqlUtilities>>();

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
    if (instance==null) {
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
                                  com.openitech.db.model.DbDataSource source,
                                  String... fields) throws SQLException {
    int pos = 1;

    System.out.println("Setting parameters");
    for (String field:fields) {
      int type = source.getType(field);
      Object value = source.getObject(field);
      System.out.println(pos+":"+field+":"+type+":"+(source.wasNull()?"null":value.toString()));
      if (source.wasNull()) {
        statement.setNull(pos++, type);
      } else {
        statement.setObject(pos++, value, type);
      }
    }

    return statement.executeUpdate();
  }
  
  public boolean beginTransaction() throws SQLException {
    Connection connection = ConnectionManager.getInstance().getConnection();

    if (!connection.getAutoCommit()) {
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

  public abstract long getLastSessionIdentity() throws SQLException;
}
