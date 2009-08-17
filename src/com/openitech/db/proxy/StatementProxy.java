/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.proxy;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class StatementProxy implements java.sql.Statement {

  final ConnectionProxy proxy;
  final int resultSetType;
  final int resultSetConcurrency;
  final int resultSetHoldability;
  protected Set<Invocation<Statement>> invocations = new HashSet<Invocation<Statement>>();
  protected List<String> batch = new ArrayList<String>();

  protected StatementProxy(ConnectionProxy proxy, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
    this.proxy = proxy;
    this.resultSetType = resultSetType;
    this.resultSetConcurrency = resultSetConcurrency;
    this.resultSetHoldability = resultSetHoldability;
  }
  protected Statement statement = null;

  protected StatementProxy(ConnectionProxy proxy, Statement createStatement) throws SQLException {
    this(proxy, createStatement.getResultSetType(), createStatement.getResultSetConcurrency(), createStatement.getResultSetHoldability());
    statement = createStatement;
  }

  public void testStatement() throws java.sql.SQLException {
    boolean passed = false;
    try {
      if (statement == null) {
        throw new java.sql.SQLException("Connection not ready");
      } else {
        proxy.testConnection();
        statement.getResultSetType();
      }
      passed = true;
    } finally {
      if (!passed) {
        System.err.println(getClass().getName() + ":TEST FAILED:invalidating statement");
        statement = null;
      }
    }
  }

  protected Statement getStatement() throws SQLException {
    return getStatement(null);
  }

  protected Statement getStatement(Invocation<Statement> ignore) throws SQLException {
    boolean create = (statement == null);

    if (!create) {
      try {
        proxy.connection.getWarnings();
        statement.getWarnings();
      } catch (Throwable ex) {
        System.out.println(getClass() + ":recreating statement:cause [" + ex.getMessage() + "]");
        statement = null;
        create = true;
      }
    }

    if (create) {
      synchronized (this) {
        if (statement == null) {
          statement = ((StatementProxy) proxy.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability)).statement;
          try {
            List<Invocation<Statement>> invoke = new ArrayList(invocations.size());
            invoke.addAll(invocations);
            Collections.sort(invoke);
            
            for (Invocation invocation : invoke) {
              if (!invocation.equals(ignore)) {
                invocation.invoke(statement);
              }
            }
          } catch (InvocationTargetException err) {
            throw (err.getCause() instanceof SQLException) ? (SQLException) err.getCause() : new SQLException(err);
          }
          for (String sql : batch) {
            statement.addBatch(sql);
          }
        }
      }
    }
    return statement;
  }
  private static final StatementProxyBeanInfo beanInfo = new StatementProxyBeanInfo();
  private static final Map<Integer, Method> methodCache = new ConcurrentHashMap<Integer, Method>();
  private static final Map<Integer, Method> writeMethodCache = new ConcurrentHashMap<Integer, Method>();
  private static final Map<Integer, Method> readMethodCache = new ConcurrentHashMap<Integer, Method>();

//  protected Class<? extends Statement> getStatementClass() {
//    return Statement.class;
//    //return statement.getClass();
//  }
  private Method getMethod(int methodIndex) {
    if (methodCache.containsKey(methodIndex)) {
      return methodCache.get(methodIndex);
    } else {
      Method method = null;
      try {
        Method spm = beanInfo.getMethodDescriptors()[methodIndex].getMethod();
        method = Statement.class.getMethod(spm.getName(), spm.getParameterTypes());
        methodCache.put(methodIndex, method);
      } catch (Exception ex) {
        Logger.getLogger(StatementProxy.class.getName()).log(Level.SEVERE, null, ex);
      }
      return method;
    }
  }

  private Method getWriteMethod(int propertyIndex) {
    if (writeMethodCache.containsKey(propertyIndex)) {
      return writeMethodCache.get(propertyIndex);
    } else {
      Method method = null;
      try {
        PropertyDescriptor pd = (PropertyDescriptor) beanInfo.getPropertyDescriptors()[propertyIndex];
        Method spm = pd instanceof IndexedPropertyDescriptor ? ((IndexedPropertyDescriptor) pd).getIndexedWriteMethod() : pd.getWriteMethod();
        method = Statement.class.getMethod(spm.getName(), spm.getParameterTypes());
        writeMethodCache.put(propertyIndex, method);
      } catch (Exception ex) {
        Logger.getLogger(StatementProxy.class.getName()).log(Level.SEVERE, null, ex);
      }
      return method;
    }
  }

  private Method getReadMethod(int propertyIndex) {
    if (readMethodCache.containsKey(propertyIndex)) {
      return readMethodCache.get(propertyIndex);
    } else {
      Method method = null;
      try {
        PropertyDescriptor pd = (PropertyDescriptor) beanInfo.getPropertyDescriptors()[propertyIndex];
        Method spm = pd instanceof IndexedPropertyDescriptor ? ((IndexedPropertyDescriptor) pd).getIndexedReadMethod() : pd.getReadMethod();
        method = Statement.class.getMethod(spm.getName(), spm.getParameterTypes());
        readMethodCache.put(propertyIndex, method);
      } catch (Exception ex) {
        Logger.getLogger(StatementProxy.class.getName()).log(Level.SEVERE, null, ex);
      }
      return method;
    }
  }

  protected Object tryToInvoke(Invocation invocation) throws SQLException {
    int trys = 1;
    while (true) {
      try {
        return invocation.invoke(getStatement(invocation));
      } catch (Throwable err) {
        System.out.println(getClass().getName()+":failed to invoke ["+invocation.method.getName()+"]:reason:"+err.getMessage());
        boolean problematic = false;
        try {
          testStatement();
        } catch (Throwable cex) {
          problematic = true;
        }
//        if ((problematic) || (--trys > 0)) {
//          if (trys < 2) {
//            statement = null;
//          }
//          System.out.println(getClass() + ":retrying:" + (3 - trys) + ":" + invocation.getMethod().getName() + ":cause [" + err.getMessage() + "]");
//        } else {
        if (problematic) {
          System.out.println(getClass() + ":retrying:" + (trys++) + ":" + invocation.getMethod().getName() + ":cause [" + err.getMessage() + "]");
        } else {
          if (!(err instanceof SQLException) &&
                  (err.getCause() instanceof SQLException)) {
            err = (SQLException) err.getCause();
          }
          throw err instanceof SQLException ? (SQLException) err : new SQLException(err);
        }
      }
    }
  }

  @Override
  public ResultSet executeQuery(String sql) throws SQLException {
    final Invocation invocation = new Invocation<Statement>(getMethod(StatementProxyBeanInfo.METHOD_executeQuery10), new Object[]{sql});
    return new ResultSetProxy((ResultSet) tryToInvoke(invocation), invocation, this);
  }

  @Override
  public int executeUpdate(String sql) throws SQLException {
    return (Integer) tryToInvoke(new Invocation<Statement>(getMethod(StatementProxyBeanInfo.METHOD_executeUpdate11), new Object[]{sql}));
  }

  @Override
  public void close() throws SQLException {
    getStatement().close();
  }

  @Override
  public int getMaxFieldSize() throws SQLException {
    return getStatement().getMaxFieldSize();
  }

  @Override
  public void setMaxFieldSize(int max) throws SQLException {
    invocations.add(new Invocation<Statement>(getWriteMethod(StatementProxyBeanInfo.PROPERTY_maxFieldSize), new Object[]{max}));
    getStatement().setMaxFieldSize(max);
  }

  @Override
  public int getMaxRows() throws SQLException {
    return getStatement().getMaxRows();
  }

  @Override
  public void setMaxRows(int max) throws SQLException {
    invocations.add(new Invocation<Statement>(getWriteMethod(StatementProxyBeanInfo.PROPERTY_maxRows), new Object[]{max}));
    getStatement().setMaxRows(max);
  }

  @Override
  public void setEscapeProcessing(boolean enable) throws SQLException {
    invocations.add(new Invocation<Statement>(getWriteMethod(StatementProxyBeanInfo.PROPERTY_escapeProcessing), new Object[]{enable}));
    getStatement().setEscapeProcessing(enable);
  }

  @Override
  public int getQueryTimeout() throws SQLException {
    return getStatement().getQueryTimeout();
  }

  @Override
  public void setQueryTimeout(int seconds) throws SQLException {
    invocations.add(new Invocation<Statement>(getWriteMethod(StatementProxyBeanInfo.PROPERTY_queryTimeout), new Object[]{seconds}));
    getStatement().setQueryTimeout(seconds);
  }

  @Override
  public void cancel() throws SQLException {
    getStatement().cancel();
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    return getStatement().getWarnings();
  }

  @Override
  public void clearWarnings() throws SQLException {
    getStatement().clearWarnings();
  }

  @Override
  public void setCursorName(String name) throws SQLException {
    invocations.add(new Invocation<Statement>(getWriteMethod(StatementProxyBeanInfo.PROPERTY_cursorName), new Object[]{name}));
    getStatement().setCursorName(name);
  }

  @Override
  public boolean execute(String sql) throws SQLException {
    return getStatement().execute(sql);
  }

  //TODO: Kako s cachiranjem tega?
  @Override
  public ResultSet getResultSet() throws SQLException {
    return getStatement().getResultSet();
  }

  @Override
  public int getUpdateCount() throws SQLException {
    return getStatement().getUpdateCount();
  }

  @Override
  public boolean getMoreResults() throws SQLException {
    return getStatement().getMoreResults();
  }

  @Override
  public void setFetchDirection(int direction) throws SQLException {
    invocations.add(new Invocation<Statement>(getWriteMethod(StatementProxyBeanInfo.PROPERTY_fetchDirection), new Object[]{direction}));
    getStatement().setFetchDirection(direction);
  }

  @Override
  public int getFetchDirection() throws SQLException {
    return getStatement().getFetchDirection();
  }

  @Override
  public void setFetchSize(int rows) throws SQLException {
    invocations.add(new Invocation<Statement>(getWriteMethod(StatementProxyBeanInfo.PROPERTY_fetchSize), new Object[]{rows}));
    getStatement().setFetchSize(rows);
  }

  @Override
  public int getFetchSize() throws SQLException {
    return getStatement().getFetchSize();
  }

  @Override
  public int getResultSetConcurrency() throws SQLException {
    return getStatement().getResultSetConcurrency();
  }

  @Override
  public int getResultSetType() throws SQLException {
    return getStatement().getResultSetType();
  }

  @Override
  public void addBatch(String sql) throws SQLException {
    batch.add(sql);
    getStatement().addBatch(sql);
  }

  @Override
  public void clearBatch() throws SQLException {
    batch.clear();
    getStatement().clearBatch();
  }

  @Override
  public int[] executeBatch() throws SQLException {
    return (int[]) tryToInvoke(new Invocation<Statement>(getMethod(StatementProxyBeanInfo.METHOD_executeBatch9), new Object[]{}));
  }

  @Override
  public Connection getConnection() throws SQLException {
    return proxy;
  }

  @Override
  public boolean getMoreResults(int current) throws SQLException {
    return getStatement().getMoreResults(current);
  }

  @Override
  public ResultSet getGeneratedKeys() throws SQLException {
    final Invocation invocation = new Invocation<Statement>(getReadMethod(StatementProxyBeanInfo.PROPERTY_generatedKeys), new Object[]{});
    return new ResultSetProxy((ResultSet) tryToInvoke(invocation), invocation, this);
  }

  @Override
  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    return (Integer) tryToInvoke(new Invocation<Statement>(getMethod(StatementProxyBeanInfo.METHOD_executeUpdate12), new Object[]{sql, autoGeneratedKeys}));
  }

  @Override
  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
    return (Integer) tryToInvoke(new Invocation<Statement>(getMethod(StatementProxyBeanInfo.METHOD_executeUpdate13), new Object[]{sql, columnIndexes}));
  }

  @Override
  public int executeUpdate(String sql, String[] columnNames) throws SQLException {
    return (Integer) tryToInvoke(new Invocation<Statement>(getMethod(StatementProxyBeanInfo.METHOD_executeUpdate14), new Object[]{sql, columnNames}));
  }

  @Override
  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
    return (Boolean) tryToInvoke(new Invocation<Statement>(getMethod(StatementProxyBeanInfo.METHOD_execute6), new Object[]{sql, autoGeneratedKeys}));
  }

  @Override
  public boolean execute(String sql, int[] columnIndexes) throws SQLException {
    return (Boolean) tryToInvoke(new Invocation<Statement>(getMethod(StatementProxyBeanInfo.METHOD_execute7), new Object[]{sql, columnIndexes}));
  }

  @Override
  public boolean execute(String sql, String[] columnNames) throws SQLException {
    return (Boolean) tryToInvoke(new Invocation<Statement>(getMethod(StatementProxyBeanInfo.METHOD_execute8), new Object[]{sql, columnNames}));
  }

  @Override
  public int getResultSetHoldability() throws SQLException {
    return getStatement().getResultSetHoldability();
  }

  @Override
  public boolean isClosed() throws SQLException {
    return getStatement().isClosed();
  }

  @Override
  public void setPoolable(boolean poolable) throws SQLException {
    invocations.add(new Invocation<Statement>(getWriteMethod(StatementProxyBeanInfo.PROPERTY_poolable), new Object[]{poolable}));
    getStatement().setPoolable(poolable);
  }

  @Override
  public boolean isPoolable() throws SQLException {
    return getStatement().isPoolable();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return getStatement().unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return getStatement().isWrapperFor(iface);
  }
}
