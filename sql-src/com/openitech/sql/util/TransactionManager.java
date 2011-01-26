/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.sql.util;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.DbDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class TransactionManager {
  private java.util.Stack<Savepoint> activeSavepoints = new java.util.Stack<Savepoint>();
  protected boolean autocommit;
  private final Connection connection;
  private final ReentrantLock lock = new ReentrantLock();

  private static Map<Connection, TransactionManager> managers = Collections.synchronizedMap(new WeakHashMap<Connection, TransactionManager>());

  protected TransactionManager() {
    this(ConnectionManager.getInstance().getTxConnection());
  }

  protected TransactionManager(Connection connection) {
    this.connection = connection;
    managers.put(connection, this);
  }

  public ReentrantLock getLock() {
    return lock;
  }

  public static TransactionManager getInstance(Connection connection) {
    TransactionManager result;
    if (managers.containsKey(connection)) {
      result = managers.get(connection);
    } else {
      result = new TransactionManager(connection);
      managers.put(connection, result);
    }
    return result;
  }

  public Savepoint beginTransaction() throws SQLException {
    if (connection.getAutoCommit()) {
      autocommit = connection.getAutoCommit();
      connection.setAutoCommit(false);
    }
    activeSavepoints.push(connection.setSavepoint());
    if (DbDataSource.DUMP_SQL) {
      if (activeSavepoints.size() > 1) {
        System.err.println("-- SET SAVEPOINT (" + activeSavepoints.peek().toString() + ") -- ");
      } else {
        System.err.println("-- BEGIN TRANSACTION (" + activeSavepoints.peek().toString() + ") -- ");
      }
    }
    return activeSavepoints.peek();
  }

  public boolean endTransaction(boolean commit, boolean force) throws SQLException {
    if (force) {
      try {
        while (!activeSavepoints.empty()) {
          connection.releaseSavepoint(activeSavepoints.pop());
        }
      } catch (SQLException err) {
        Logger.getLogger(SqlUtilities.class.getName()).log(Level.WARNING, err.getMessage(), err);
      } finally {
        activeSavepoints.clear();
      }
    }
    return endTransaction(commit);
  }

  public boolean endTransaction(boolean commit) throws SQLException {
    return endTransaction(commit, activeSavepoints.empty() ? null : activeSavepoints.pop());
  }

  public boolean endTransaction(boolean commit, Savepoint savepoint) throws SQLException {
    if (!connection.getAutoCommit()) {
      if (commit) {
        if (savepoint != null) {
          try {
            connection.releaseSavepoint(savepoint);
          } catch (SQLException err) {
            Logger.getLogger(SqlUtilities.class.getName()).log(Level.WARNING, err.getMessage(), err);
          }
        }
        if (activeSavepoints.empty()) {
          connection.commit();
          if (DbDataSource.DUMP_SQL) {
            System.err.println("-- COMMIT TRANSACTION -- ");
          }
        } else if (savepoint != null) {
          System.err.println("-- RELEASE SAVEPOINT (" + savepoint.toString() + ") -- ");
        }
      } else if (savepoint != null) {
        connection.rollback(savepoint);
      } else {
        activeSavepoints.clear();
        connection.rollback();
      }
      if (savepoint != null) {
        activeSavepoints.remove(savepoint);
      }
      if (!commit) {
        if (activeSavepoints.empty()) {
          System.err.println("-- ROLLBACK TRANSACTION -- ");
        } else {
          System.err.println("-- ROLLBACK TO SAVEPOINT (" + savepoint.toString() + ") -- ");
        }
      }

      if (activeSavepoints.empty()) {
        connection.setAutoCommit(autocommit);
      }
      return true;
    } else {
      return false;
    }
  }

  public void clearTransactions() throws SQLException {
    try {
      endTransaction(false, true);
    } catch (SQLException ex) {
      //ignore it
       Logger.getLogger(SqlUtilities.class.getName()).info(ex.getSQLState()+":"+ex.getMessage());
      activeSavepoints.clear();
      connection.setAutoCommit(autocommit);
      System.err.println("-- TRANSACTION CLEARED -- ");
    }
  }

  public boolean isTransaction() {
    return !activeSavepoints.empty();
  }

}
