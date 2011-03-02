/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.jdbc.proxy;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.connection.DbConnection;
import com.openitech.db.model.DbDataSource;
import com.openitech.events.concurrent.Interruptable;
import com.openitech.events.concurrent.Locking;
import com.openitech.ref.WeakList;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;

/**
 *
 * @author uros
 */
public abstract class AbstractConnection implements java.sql.Connection, Locking, Interruptable {

  protected javax.sql.PooledConnection pooledConnection;
  protected java.sql.Connection connection;
  protected final javax.sql.DataSource dataSource;
  private final java.util.List<String> executeOnCreate = new ArrayList<String>();
  protected final java.util.List<Statement> activeStatemens = new WeakList<Statement>();
  protected final java.util.List<Savepoint> activeSavepoints = new WeakList<Savepoint>();
  protected boolean initAutoCommit = true;
  protected boolean shadowLoading = Boolean.valueOf(ConnectionManager.getInstance().getProperty(DbConnection.DB_SHADOW_LOADING, "false"));

  public AbstractConnection(DataSource dataSource) throws SQLException {
    this(dataSource, true, null);
  }

  public AbstractConnection(DataSource dataSource, boolean autoCommit, java.util.List<String> executeOnCreate) throws SQLException {
    this.dataSource = dataSource;
    this.initAutoCommit = autoCommit;
    if (executeOnCreate != null) {
      this.executeOnCreate.addAll(executeOnCreate);
    }
    this.connection = openConnection();
    this.connection.setAutoCommit(initAutoCommit);
  }

  private Connection openConnection() throws SQLException {
    Logger.getLogger(AbstractConnection.class.getName()).info("Create connection.");
    lock();
    Connection result = null;
    try {
      if (pooledConnection == null) {
        result = (dataSource instanceof ConnectionPoolDataSource) ? (pooledConnection = ((ConnectionPoolDataSource) dataSource).getPooledConnection()).getConnection() : dataSource.getConnection();
      } else {
        result = pooledConnection.getConnection();
      }
      Statement initStatement;
      
      try {
        initStatement = result.createStatement();
      } catch (SQLException err) {
        if (pooledConnection!=null) {
          try {
            pooledConnection.close();
          } catch (SQLException ex2) {
            //ignore it
          }
          pooledConnection = null;
        }
        result = (dataSource instanceof ConnectionPoolDataSource) ? (pooledConnection = ((ConnectionPoolDataSource) dataSource).getPooledConnection()).getConnection() : dataSource.getConnection();
        initStatement = result.createStatement();
      }

      try {
        for (String sql : executeOnCreate) {
          initStatement.execute(sql);
        }
      } finally {
        initStatement.close();
      }
      result.setAutoCommit(initAutoCommit);
    } finally {
      unlock();
    }
    return result;
  }

  protected Statement addStatement(Statement statement) {
    if (activeStatemens.add(statement)) {
      return statement;
    } else {
      return null;
    }
  }

  protected boolean removeStatement(Statement statement) {
    return activeStatemens.remove(statement);
  }

  /**
   * Get the value of initAutoCommit
   *
   * @return the value of initAutoCommit
   */
  public boolean isInitAutoCommit() {
    return initAutoCommit;
  }

  /**
   * Set the value of initAutoCommit
   *
   * @param initAutoCommit new value of initAutoCommit
   */
  public void setInitAutoCommit(boolean initAutoCommit) {
    this.initAutoCommit = initAutoCommit;
  }

  protected boolean isValid() {
    lock();
    try {
      if (this.connection == null || this.connection.isClosed()) {
        return false;
      } else {
        this.connection.getWarnings();
        return true;
      }
    } catch (SQLException ex) {
      Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING, ex.getMessage());
      return false;
    } finally {
      unlock();
    }
  }

  protected java.sql.Connection getActiveConnection() throws SQLException {
    if (!isValid()) {
      lock();
      try {
        java.sql.Connection activeConnection = openConnection();
        if (autoCommit != null) {
          activeConnection.setAutoCommit(autoCommit);
        }
        if (readOnly != null) {
          activeConnection.setReadOnly(readOnly);
        }
        if (catalog != null) {
          activeConnection.setCatalog(catalog);
        }
        if (transactionIsolation != null) {
          activeConnection.setTransactionIsolation(transactionIsolation);
        }
        if (typeMap != null) {
          activeConnection.setTypeMap(typeMap);
        }
        if (clientInfo != null) {
          activeConnection.setClientInfo(clientInfo);
        }
        this.connection = activeConnection;
        Logger.getLogger(AbstractConnection.class.getName()).info("Connection reopened.");
      } finally {
        unlock();
      }
    }
    timestamp = System.currentTimeMillis();
    return this.connection;
  }
  private volatile long timestamp;

  /**
   * Get the value of timestamp
   *
   * @return the value of timestamp
   */
  public long getTimestamp() {
    return timestamp;
  }

  protected boolean isConnectionActive() {
    return !(activeSavepoints.isEmpty() && activeStatemens.isEmpty());
  }

  @Override
  public String nativeSQL(String sql) throws SQLException {
    return getActiveConnection().nativeSQL(sql);
  }
  protected Boolean autoCommit;

  @Override
  public void setAutoCommit(boolean autoCommit) throws SQLException {
    getActiveConnection().setAutoCommit(autoCommit);
    if (autoCommit) {
      activeSavepoints.clear();
    }
    this.autoCommit = autoCommit;
  }

  @Override
  public boolean getAutoCommit() throws SQLException {
    return getActiveConnection().getAutoCommit();
  }

  @Override
  public void commit() throws SQLException {
    activeSavepoints.clear();
    getActiveConnection().commit();
  }

  @Override
  public void rollback() throws SQLException {
    activeSavepoints.clear();
    getActiveConnection().rollback();
  }
  protected Boolean closed = Boolean.FALSE;

  @Override
  public void close() throws SQLException {
    lock();
    try {
      if (!closed) {
        if (pooledConnection != null) {
          pooledConnection.close();
          pooledConnection = null;
        } else if (connection != null) {
          connection.close();
        }
        activeStatemens.clear();
        activeSavepoints.clear();
        connection = null;
        closed = Boolean.TRUE;
        Logger.getLogger(AbstractConnection.class.getName()).info("Connection closed.");
//        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
//
//        System.out.println("StackTrace:::");
//        for (StackTraceElement element : stackTrace) {
//          System.out.println(element.toString());
//        }
      }
    } finally {
      unlock();
    }
  }

  @Override
  public boolean isClosed() throws SQLException {
    return closed;
  }

  @Override
  public DatabaseMetaData getMetaData() throws SQLException {
    return getActiveConnection().getMetaData();
  }
  protected Boolean readOnly;

  @Override
  public void setReadOnly(boolean readOnly) throws SQLException {
    getActiveConnection().setReadOnly(readOnly);
    this.readOnly = readOnly;
  }

  @Override
  public boolean isReadOnly() throws SQLException {
    return getActiveConnection().isReadOnly();
  }
  protected String catalog;

  @Override
  public void setCatalog(String catalog) throws SQLException {
    getActiveConnection().setCatalog(catalog);
    this.catalog = catalog;
  }

  @Override
  public String getCatalog() throws SQLException {
    return getActiveConnection().getCatalog();
  }
  protected Integer transactionIsolation;

  @Override
  public void setTransactionIsolation(int transactionIsolation) throws SQLException {
    getActiveConnection().setTransactionIsolation(transactionIsolation);
    this.transactionIsolation = transactionIsolation;
  }

  @Override
  public int getTransactionIsolation() throws SQLException {
    return getActiveConnection().getTransactionIsolation();
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    return getActiveConnection().getWarnings();
  }

  @Override
  public void clearWarnings() throws SQLException {
    getActiveConnection().clearWarnings();
  }
  protected Map<String, Class<?>> typeMap;

  @Override
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    return getActiveConnection().getTypeMap();
  }

  @Override
  public void setTypeMap(Map<String, Class<?>> typeMap) throws SQLException {
    getActiveConnection().setTypeMap(typeMap);
    this.typeMap = typeMap;
  }
  protected Integer holdability;

  @Override
  public void setHoldability(int holdability) throws SQLException {
    getActiveConnection().setHoldability(holdability);
    this.holdability = holdability;
  }

  @Override
  public int getHoldability() throws SQLException {
    return getActiveConnection().getHoldability();
  }

  @Override
  public boolean isValid(int timeout) throws SQLException {
    return connection.isValid(timeout);
  }
  protected Properties clientInfo;

  @Override
  public void setClientInfo(String name, String value) throws SQLClientInfoException {
    try {
      getActiveConnection().setClientInfo(name, value);
      clientInfo = getActiveConnection().getClientInfo();
    } catch (SQLException ex) {
      Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING, null, ex);
      connection.setClientInfo(name, value);
    }
  }

  @Override
  public void setClientInfo(Properties properties) throws SQLClientInfoException {
    try {
      getActiveConnection().setClientInfo(properties);
      clientInfo = connection.getClientInfo();
    } catch (SQLException ex) {
      Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING, null, ex);
      connection.setClientInfo(properties);
    }
  }

  @Override
  public String getClientInfo(String name) throws SQLException {
    return getActiveConnection().getClientInfo(name);
  }

  @Override
  public Properties getClientInfo() throws SQLException {
    return getActiveConnection().getClientInfo();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return getActiveConnection().unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return getActiveConnection().isWrapperFor(iface);
  }

  @Override
  public Statement createStatement() throws SQLException {
    return createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
            java.sql.ResultSet.CONCUR_READ_ONLY);
  }

  @Override
  public Statement createStatement(int type, int concurrency, int holdability)
          throws SQLException {
    setHoldability(holdability);

    return createStatement(type, concurrency);
  }
  ReentrantLock available = new ReentrantLock(true);

  @Override
  public boolean lock() {
    return lock(true, true);
  }

  @Override
  public boolean canLock() {
    boolean result = false;
    try {
      result = available.tryLock() || available.tryLock(10L, TimeUnit.MILLISECONDS);
      if (result) {
        available.unlock();
      }
    } catch (InterruptedException ex) {
      //ignore it;
    }
    return result;
  }

  @Override
  public boolean lock(boolean fatal) {
    return lock(fatal, false);
  }

  @Override
  public boolean lock(boolean fatal, boolean force) {
    boolean result = false;
    try {
      if (force) {
        available.lock();
        result = true;
      } else {
        if (!(result = (available.tryLock() || available.tryLock(2L, TimeUnit.SECONDS)))) {
          if (fatal) {
            throw new IllegalStateException("Can't obtain lock on: " + toString());
          } else {
            Logger.getLogger(DbDataSource.class.getName()).log(Level.WARNING, null, new IllegalStateException("Can't obtain lock on: " + toString()));
          }
        }
      }
    } catch (InterruptedException ex) {
      throw (IllegalStateException) (new IllegalStateException("Can't obtain lock")).initCause(ex);
    }
    return result;
  }

  @Override
  public void unlock() {
    available.unlock();
  }

  @Override
  public void interrupt() {
    if (this.connection != null) {
//      try {
//        this.connection.close();
      for (Statement statement : activeStatemens) {
        if (statement instanceof Interruptable) {
          ((Interruptable) statement).interrupt();
        }
      }
//        activeSavepoints.clear();
//        getActiveConnection();
//      } catch (SQLException ex) {
//        Logger.getLogger(AbstractConnection.class.getName()).log(Level.SEVERE, null, ex);
//      }
    }
  }

  public void reconnect() {
    try {
      if (this.connection != null) {
        this.connection.close();
        this.connection = null;
        activeSavepoints.clear();
        getActiveConnection();
      }
    } catch (SQLException ex) {
    }
  }

  /**
   * Get the value of shadowLoading
   *
   * @return the value of shadowLoading
   */
  public boolean isShadowLoading() {
    return shadowLoading;
  }
}
