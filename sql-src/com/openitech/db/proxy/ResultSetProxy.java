/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.proxy;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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
public class ResultSetProxy implements ResultSet {

  Invocation<Statement> invocation;
  ResultSet resultSet;
  StatementProxy target;
  protected List<Invocation<ResultSet>> reads = new ArrayList<Invocation<ResultSet>>();
  protected List<Invocation<ResultSet>> motion = new ArrayList<Invocation<ResultSet>>();
  protected List<Invocation<ResultSet>> update = new ArrayList<Invocation<ResultSet>>();
  protected List<Invocation<ResultSet>> regular = new ArrayList<Invocation<ResultSet>>();
  protected Set<Invocation<ResultSet>> single = new HashSet<Invocation<ResultSet>>();
  final Invocation<ResultSet> clearWarnings;
  final Invocation<ResultSet> isBeforeFirst;
  final Invocation<ResultSet> isAfterLast;
  final Invocation<ResultSet> isFirst;
  final Invocation<ResultSet> isLast;
  final Invocation<ResultSet> beforeFirst;
  final Invocation<ResultSet> afterLast;
  final Invocation<ResultSet> first;
  final Invocation<ResultSet> last;
  final Invocation<ResultSet> getRow;
  final Invocation<ResultSet> previous;
  final Invocation<ResultSet> setFetchDirection;
  final Invocation<ResultSet> getFetchDirection;
  final Invocation<ResultSet> setFetchSize;
  final Invocation<ResultSet> getFetchSize;
  final Invocation<ResultSet> getType;
  final Invocation<ResultSet> getConcurrency;
  final Invocation<ResultSet> rowUpdated;
  final Invocation<ResultSet> rowInserted;
  final Invocation<ResultSet> rowDeleted;
  final Invocation<ResultSet> insertRow;
  final Invocation<ResultSet> updateRow;
  final Invocation<ResultSet> deleteRow;
  final Invocation<ResultSet> refreshRow;
  final Invocation<ResultSet> cancelRowUpdates;
  final Invocation<ResultSet> moveToInsertRow;
  final Invocation<ResultSet> moveToCurrentRow;
  final Invocation<ResultSet> getStatement;
  final Invocation<ResultSet> getHoldability;
  final Invocation<ResultSet> isClosed;
  final Invocation<ResultSet> unwrap;
  final Invocation<ResultSet> isWrapperFor;
  //speed up
  final Invocation<ResultSet> getObjectS;
  final Invocation<ResultSet> getObjectI;

  protected ResultSetProxy(ResultSet resultSet, Invocation<Statement> invocation, StatementProxy target) {
    this.invocation = invocation;
    this.resultSet = resultSet;
    this.target = target;

    clearWarnings = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_clearWarnings4), new Object[]{});
    isBeforeFirst = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_beforeFirst2), new Object[]{});
    isAfterLast = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_afterLast), new Object[]{});
    isFirst = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_first), new Object[]{});
    isLast = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_last), new Object[]{});
    beforeFirst = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_beforeFirst2), new Object[]{});
    afterLast = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_afterLast1), new Object[]{});
    first = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_first8), new Object[]{});
    last = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_last49), new Object[]{});
    getRow = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_row), new Object[]{});
    previous = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_previous53), new Object[]{});
    setFetchDirection = new Invocation<ResultSet>(getWriteMethod(ResultSetProxyBeanInfo.PROPERTY_fetchDirection), null);
    getFetchDirection = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_fetchDirection), new Object[]{});
    setFetchSize = new Invocation<ResultSet>(getWriteMethod(ResultSetProxyBeanInfo.PROPERTY_fetchSize), null);
    getFetchSize = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_fetchSize), new Object[]{});
    getType = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_type), new Object[]{});
    getConcurrency = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_concurrency), new Object[]{});
    rowUpdated = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_rowUpdated58), new Object[]{});
    rowInserted = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_rowInserted57), new Object[]{});
    rowDeleted = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_rowDeleted56), new Object[]{});
    insertRow = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_insertRow47), new Object[]{});
    updateRow = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateRow130), new Object[]{});
    deleteRow = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_deleteRow6), new Object[]{});
    refreshRow = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_refreshRow54), new Object[]{});
    cancelRowUpdates = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_cancelRowUpdates3), new Object[]{});
    moveToInsertRow = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_moveToInsertRow51), new Object[]{});
    moveToCurrentRow = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_moveToCurrentRow50), new Object[]{});
    getStatement = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_statement), new Object[]{});
    getHoldability = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_holdability), new Object[]{});
    isClosed = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_closed), new Object[]{});
    unwrap = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_unwrap59), null);
    isWrapperFor = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_isWrapperFor48), null);

    //speed up
    getObjectS = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getObject31), null);
    getObjectI = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_object), null);
  }

  protected ResultSet getResultSet() throws SQLException {
    return getResultSet(null);
  }

  protected ResultSet getResultSet(Invocation<ResultSet> ignore) throws SQLException {
    boolean create = (resultSet == null);

    if (!create) {
      try {
        target.proxy.getWarnings();
        target.getWarnings();
        resultSet.getWarnings();
      } catch (Throwable ex) {
        System.out.println(getClass() + ":recreating result set:cause [" + ex.getMessage() + "]");
        resultSet = null;
        create = true;
      }
    }
    if (create) {
      try {
        target.getStatement();
        synchronized (this) {
          if (resultSet == null) {
            resultSet = (ResultSet) invocation.invoke(target);
            if (resultSet instanceof ResultSetProxy) {
              resultSet = ((ResultSetProxy) resultSet).resultSet;
            }
            List<Invocation<ResultSet>> invoke = new ArrayList(reads.size() + motion.size() + update.size() + single.size() + regular.size());
            invoke.addAll(reads);
            invoke.addAll(motion);
            invoke.addAll(update);
            invoke.addAll(single);
            invoke.addAll(regular);
            Collections.sort(invoke);

            List<Invocation<ResultSet>> copies = new ArrayList(invoke.size());

            for (Invocation<ResultSet> element : invoke) {
              if (!element.equals(ignore)) {
                copies.add((Invocation<ResultSet>) element.clone());
              }
            }

            for (Invocation<ResultSet> element : copies) {
              element.invoke(resultSet);
            }
          }
        }
      } catch (InvocationTargetException err) {
        System.out.println(getClass() + ":failed to recreate statement:cause [" + err.getMessage() + "]");
        throw (err.getCause() instanceof SQLException) ? (SQLException) err.getCause() : new SQLException(err);
      }

    }
    return resultSet;
  }
  private static final ResultSetProxyBeanInfo beanInfo = new ResultSetProxyBeanInfo();
  private static final Map<Integer, Method> methodCache = new ConcurrentHashMap<Integer, Method>();
  private static final Map<Integer, Method> writeMethodCache = new ConcurrentHashMap<Integer, Method>();
  private static final Map<Integer, Method> readMethodCache = new ConcurrentHashMap<Integer, Method>();

  private Method getMethod(int methodIndex) {
    if (methodCache.containsKey(methodIndex)) {
      return methodCache.get(methodIndex);
    } else {
      Method method = null;
      try {
        Method spm = beanInfo.getMethodDescriptors()[methodIndex].getMethod();
        method = ResultSet.class.getMethod(spm.getName(), spm.getParameterTypes());
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
        method = ResultSet.class.getMethod(spm.getName(), spm.getParameterTypes());
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
        method = ResultSet.class.getMethod(spm.getName(), spm.getParameterTypes());
        readMethodCache.put(propertyIndex, method);
      } catch (Exception ex) {
        Logger.getLogger(StatementProxy.class.getName()).log(Level.SEVERE, null, ex);
      }
      return method;
    }
  }

  protected Object tryToInvoke(Invocation<ResultSet> invocation) throws SQLException {
    return tryToInvoke(invocation, false);
  }

  protected Object tryToInvoke(Invocation<ResultSet> invocation, boolean critical) throws SQLException {
    int trys = 1;
    while (true) {
      try {
        Object result = invocation.invoke(getResultSet(invocation));
        Logger.getLogger(StatementProxy.class.getName()).finest(invocation.getMethod().getName() + "=" + result);
        return result;
      } catch (Throwable err) {
        System.out.println(getClass().getName() + ":failed to invoke [" + invocation.method.getName() + "]:reason:" + err.getMessage());
        boolean problematic = false;
        try {
          target.testStatement();
        } catch (Throwable cex) {
          resultSet = null;
          try {
            Thread.currentThread().sleep(PooledConnection.RETRY_DELAY);
          } catch (InterruptedException ex) {
            Logger.getLogger(StatementProxy.class.getName()).log(Level.INFO, ex.getMessage());
          }
          problematic = true;
        }
        if (problematic&&(trys<PooledConnection.RETRYS_LIMIT)) {
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
  public boolean next() throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_next52), new Object[]{});
    motion.add(invoke);
    try {
      return (Boolean) tryToInvoke(invoke, true);
    } finally {
      update.clear();
    }
  }

  @Override
  public void close() throws SQLException {
    getResultSet().close();
  }

  @Override
  public boolean wasNull() throws SQLException {
    return getResultSet().wasNull();
  }

  @Override
  public String getString(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_string), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (String) tryToInvoke(invoke);
  }

  @Override
  public boolean getBoolean(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_boolean), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Boolean) tryToInvoke(invoke);
  }

  @Override
  public byte getByte(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_byte), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Byte) tryToInvoke(invoke);
  }

  @Override
  public short getShort(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_short), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Short) tryToInvoke(invoke);
  }

  @Override
  public int getInt(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_int), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Integer) tryToInvoke(invoke);
  }

  @Override
  public long getLong(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_long), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Long) tryToInvoke(invoke);
  }

  @Override
  public float getFloat(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_float), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Float) tryToInvoke(invoke);
  }

  @Override
  public double getDouble(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_double), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Double) tryToInvoke(invoke);
  }

  @Override
  @Deprecated
  public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getBigDecimal11), new Object[]{columnIndex, scale});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (BigDecimal) tryToInvoke(invoke);
  }

  @Override
  public byte[] getBytes(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_bytes), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (byte[]) tryToInvoke(invoke);
  }

  @Override
  public Date getDate(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_date), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Date) tryToInvoke(invoke);
  }

  @Override
  public Time getTime(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_time), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Time) tryToInvoke(invoke);
  }

  @Override
  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_timestamp), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Timestamp) tryToInvoke(invoke);
  }

  @Override
  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_asciiStream), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (InputStream) tryToInvoke(invoke);
  }

  @Override
  @Deprecated
  public InputStream getUnicodeStream(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_unicodeStream), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (InputStream) tryToInvoke(invoke);
  }

  @Override
  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_binaryStream), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (InputStream) tryToInvoke(invoke);
  }

  @Override
  public String getString(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getString38), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (String) tryToInvoke(invoke);
  }

  @Override
  public boolean getBoolean(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getBoolean16), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Boolean) tryToInvoke(invoke);
  }

  @Override
  public byte getByte(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getByte17), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Byte) tryToInvoke(invoke);
  }

  @Override
  public short getShort(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getShort36), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Short) tryToInvoke(invoke);
  }

  @Override
  public int getInt(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getInt26), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Integer) tryToInvoke(invoke);
  }

  @Override
  public long getLong(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getLong27), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Long) tryToInvoke(invoke);
  }

  @Override
  public float getFloat(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getFloat25), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Float) tryToInvoke(invoke);
  }

  @Override
  public double getDouble(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getDouble24), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Double) tryToInvoke(invoke);
  }

  @Override
  @Deprecated
  public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getBigDecimal12), new Object[]{columnLabel, scale});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (BigDecimal) tryToInvoke(invoke);
  }

  @Override
  public byte[] getBytes(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getBytes18), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (byte[]) tryToInvoke(invoke);
  }

  @Override
  public Date getDate(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getDate21), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Date) tryToInvoke(invoke);
  }

  @Override
  public Time getTime(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getTime39), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Time) tryToInvoke(invoke);
  }

  @Override
  public Timestamp getTimestamp(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getTimestamp42), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Timestamp) tryToInvoke(invoke);
  }

  @Override
  public InputStream getAsciiStream(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getAsciiStream10), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (InputStream) tryToInvoke(invoke);
  }

  @Override
  @Deprecated
  public InputStream getUnicodeStream(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getUnicodeStream45), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (InputStream) tryToInvoke(invoke);
  }

  @Override
  public InputStream getBinaryStream(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getBinaryStream14), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (InputStream) tryToInvoke(invoke);
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_warnings), new Object[]{});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (SQLWarning) tryToInvoke(invoke);
  }

  @Override
  public void clearWarnings() throws SQLException {
    clearWarnings.timestamp();
    single.add(clearWarnings);
    tryToInvoke(clearWarnings);
  }

  @Override
  public String getCursorName() throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_cursorName), new Object[]{});
    return (String) tryToInvoke(invoke);
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_metaData), new Object[]{});
    return (ResultSetMetaData) tryToInvoke(invoke);
  }

  @Override
  public Object getObject(int columnIndex) throws SQLException {
    getObjectI.arguments = new Object[]{columnIndex};
    getObjectI.timestamp();
    if (reads.isEmpty()) {
      reads.add(getObjectI);
    } else {
      reads.set(0, getObjectI);
    }
    return (Object) tryToInvoke(getObjectI);
  }

  @Override
  public Object getObject(String columnLabel) throws SQLException {
    getObjectS.arguments = new Object[]{columnLabel};
    getObjectS.timestamp();
    if (reads.isEmpty()) {
      reads.add(getObjectS);
    } else {
      reads.set(0, getObjectS);
    }
    return (Object) tryToInvoke(getObjectS);
  }

  @Override
  public int findColumn(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_findColumn7), new Object[]{columnLabel});
    return (Integer) tryToInvoke(invoke);
  }

  @Override
  public Reader getCharacterStream(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_characterStream), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Reader) tryToInvoke(invoke);
  }

  @Override
  public Reader getCharacterStream(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getCharacterStream19), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Reader) tryToInvoke(invoke);
  }

  @Override
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_bigDecimal), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (BigDecimal) tryToInvoke(invoke);
  }

  @Override
  public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getBigDecimal13), new Object[]{columnLabel});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (BigDecimal) tryToInvoke(invoke);
  }

  @Override
  public boolean isBeforeFirst() throws SQLException {
    return (Boolean) tryToInvoke(isBeforeFirst, true);
  }

  @Override
  public boolean isAfterLast() throws SQLException {
    return (Boolean) tryToInvoke(isAfterLast, true);
  }

  @Override
  public boolean isFirst() throws SQLException {
    return (Boolean) tryToInvoke(isFirst, true);
  }

  @Override
  public boolean isLast() throws SQLException {
    return (Boolean) tryToInvoke(isLast, true);
  }

  @Override
  public void beforeFirst() throws SQLException {
    motion.clear();
    beforeFirst.timestamp();
    motion.add(beforeFirst);
    tryToInvoke(beforeFirst);
  }

  @Override
  public void afterLast() throws SQLException {
    motion.clear();
    afterLast.timestamp();
    motion.add(afterLast);
    tryToInvoke(afterLast);
  }

  @Override
  public boolean first() throws SQLException {
    motion.clear();
    first.timestamp();
    motion.add(first);
    try {
      return (Boolean) tryToInvoke(first);
    } finally {
      update.clear();
    }
  }

  @Override
  public boolean last() throws SQLException {
    motion.clear();
    last.timestamp();
    motion.add(last);
    try {
      return (Boolean) tryToInvoke(last);
    } finally {
      update.clear();
    }
  }

  @Override
  public int getRow() throws SQLException {
    return (Integer) tryToInvoke(getRow);
  }

  @Override
  public boolean absolute(int row) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_absolute0), new Object[]{row});
    motion.clear();
    motion.add(invoke);
    try {
      return (Boolean) tryToInvoke(invoke);
    } finally {
      update.clear();
    }
  }

  @Override
  public boolean relative(int rows) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_relative55), new Object[]{rows});
    motion.add(invoke);
    try {
      return (Boolean) tryToInvoke(invoke);
    } finally {
      update.clear();
    }
  }

  @Override
  public boolean previous() throws SQLException {
    last.timestamp();
    motion.add(previous);

    try {
      return (Boolean) tryToInvoke(previous);
    } finally {
      update.clear();
    }
  }

  @Override
  public void setFetchDirection(int direction) throws SQLException {
    setFetchDirection.arguments = new Object[]{direction};
    single.add(setFetchDirection);
    tryToInvoke(setFetchDirection);
  }

  @Override
  public int getFetchDirection() throws SQLException {
    return (Integer) tryToInvoke(getFetchDirection);
  }

  @Override
  public void setFetchSize(int rows) throws SQLException {
    setFetchSize.arguments = new Object[]{rows};
    single.add(setFetchSize);
    tryToInvoke(setFetchSize);
  }

  @Override
  public int getFetchSize() throws SQLException {
    return (Integer) tryToInvoke(getFetchSize);
  }

  @Override
  public int getType() throws SQLException {
    return (Integer) tryToInvoke(getType);
  }

  @Override
  public int getConcurrency() throws SQLException {
    return (Integer) tryToInvoke(getConcurrency);
  }

  @Override
  public boolean rowUpdated() throws SQLException {
    return (Boolean) tryToInvoke(rowUpdated);
  }

  @Override
  public boolean rowInserted() throws SQLException {
    return (Boolean) tryToInvoke(rowInserted);
  }

  @Override
  public boolean rowDeleted() throws SQLException {
    return (Boolean) tryToInvoke(rowDeleted);
  }

  @Override
  public void updateNull(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNull122), new Object[]{columnIndex});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBoolean(int columnIndex, boolean x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBoolean82), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateByte(int columnIndex, byte x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateByte84), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateShort(int columnIndex, short x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateShort133), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateInt(int columnIndex, int x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateInt106), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateLong(int columnIndex, long x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateLong108), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateFloat(int columnIndex, float x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateFloat104), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateDouble(int columnIndex, double x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateDouble102), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBigDecimal68), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateString(int columnIndex, String x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateString137), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBytes(int columnIndex, byte[] x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBytes86), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateDate(int columnIndex, Date x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateDate100), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateTime(int columnIndex, Time x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateTime139), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateTimestamp141), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateAsciiStream62), new Object[]{columnIndex, x, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBinaryStream70), new Object[]{columnIndex, x, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateCharacterStream88), new Object[]{columnIndex, x, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateObject124), new Object[]{columnIndex, x, scaleOrLength});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateObject(int columnIndex, Object x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateObject125), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateNull(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNull123), new Object[]{columnLabel});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBoolean(String columnLabel, boolean x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBoolean83), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateByte(String columnLabel, byte x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateByte85), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateShort(String columnLabel, short x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateShort134), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateInt(String columnLabel, int x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateInt107), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateLong(String columnLabel, long x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateLong109), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateFloat(String columnLabel, float x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateFloat105), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateDouble(String columnLabel, double x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateDouble103), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBigDecimal69), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateString(String columnLabel, String x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateString138), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBytes(String columnLabel, byte[] x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBytes87), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateDate(String columnLabel, Date x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateDate101), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateTime(String columnLabel, Time x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateTime140), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateTimestamp142), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateAsciiStream63), new Object[]{columnLabel, x, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBinaryStream71), new Object[]{columnLabel, x, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateCharacterStream89), new Object[]{columnLabel, reader, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateObject126), new Object[]{columnLabel, x, scaleOrLength});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateObject(String columnLabel, Object x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateObject127), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void insertRow() throws SQLException {
    insertRow.timestamp();
    tryToInvoke(insertRow);
    single.add(insertRow);
    update.clear();
  }

  @Override
  public void updateRow() throws SQLException {
    updateRow.timestamp();
    tryToInvoke(updateRow);
    single.add(updateRow);
    update.clear();
  }

  @Override
  public void deleteRow() throws SQLException {
    deleteRow.timestamp();
    tryToInvoke(deleteRow);
    single.add(deleteRow);
    update.clear();
  }

  @Override
  public void refreshRow() throws SQLException {
    refreshRow.timestamp();
    tryToInvoke(refreshRow);
    single.add(refreshRow);
    update.clear();
  }

  @Override
  public void cancelRowUpdates() throws SQLException {
    deleteRow.timestamp();
    tryToInvoke(deleteRow);
    single.add(deleteRow);
    update.clear();
  }

  @Override
  public void moveToInsertRow() throws SQLException {
    moveToInsertRow.timestamp();
    tryToInvoke(moveToInsertRow);
    single.add(moveToInsertRow);
    update.clear();
  }

  @Override
  public void moveToCurrentRow() throws SQLException {
    moveToCurrentRow.timestamp();
    tryToInvoke(moveToCurrentRow);
    single.add(moveToCurrentRow);
    update.clear();
  }

  @Override
  public Statement getStatement() throws SQLException {
    return (Statement) tryToInvoke(moveToCurrentRow);
  }

  @Override
  public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getObject32), new Object[]{columnIndex, map});
    return tryToInvoke(invoke);
  }

  @Override
  public Ref getRef(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_ref), new Object[]{columnIndex});
    return (Ref) tryToInvoke(invoke);
  }

  @Override
  public Blob getBlob(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_blob), new Object[]{columnIndex});
    return (Blob) tryToInvoke(invoke);
  }

  @Override
  public Clob getClob(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_clob), new Object[]{columnIndex});
    return (Clob) tryToInvoke(invoke);
  }

  @Override
  public Array getArray(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_array), new Object[]{columnIndex});
    return (Array) tryToInvoke(invoke);
  }

  @Override
  public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getObject33), new Object[]{columnLabel, map});
    return tryToInvoke(invoke);
  }

  @Override
  public Ref getRef(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getRef34), new Object[]{columnLabel});
    return (Ref) tryToInvoke(invoke);
  }

  @Override
  public Blob getBlob(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getBlob15), new Object[]{columnLabel});
    return (Blob) tryToInvoke(invoke);
  }

  @Override
  public Clob getClob(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getClob20), new Object[]{columnLabel});
    return (Clob) tryToInvoke(invoke);
  }

  @Override
  public Array getArray(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getArray9), new Object[]{columnLabel});
    return (Array) tryToInvoke(invoke);
  }

  @Override
  public Date getDate(int columnIndex, Calendar cal) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getDate22), new Object[]{columnIndex, cal});
    return (Date) tryToInvoke(invoke);
  }

  @Override
  public Date getDate(String columnLabel, Calendar cal) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getDate23), new Object[]{columnLabel, cal});
    return (Date) tryToInvoke(invoke);
  }

  @Override
  public Time getTime(int columnIndex, Calendar cal) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getTime40), new Object[]{columnIndex, cal});
    return (Time) tryToInvoke(invoke);
  }

  @Override
  public Time getTime(String columnLabel, Calendar cal) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getTime41), new Object[]{columnLabel, cal});
    return (Time) tryToInvoke(invoke);
  }

  @Override
  public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getTimestamp43), new Object[]{columnIndex, cal});
    return (Timestamp) tryToInvoke(invoke);
  }

  @Override
  public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getTimestamp44), new Object[]{columnLabel, cal});
    return (Timestamp) tryToInvoke(invoke);
  }

  @Override
  public URL getURL(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_URL), new Object[]{columnIndex});
    return (URL) tryToInvoke(invoke);
  }

  @Override
  public URL getURL(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getURL46), new Object[]{columnLabel});
    return (URL) tryToInvoke(invoke);
  }

  @Override
  public void updateRef(int columnIndex, Ref x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateRef128), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateRef(String columnLabel, Ref x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateRef129), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBlob(int columnIndex, Blob x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBlob76), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBlob(String columnLabel, Blob x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBlob77), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateClob(int columnIndex, Clob x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateClob94), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateClob(String columnLabel, Clob x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateClob95), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateArray(int columnIndex, Array x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateArray60), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateArray(String columnLabel, Array x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateArray61), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public RowId getRowId(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_rowId), new Object[]{columnIndex});
    return (RowId) tryToInvoke(invoke);
  }

  @Override
  public RowId getRowId(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getRowId35), new Object[]{columnLabel});
    return (RowId) tryToInvoke(invoke);
  }

  @Override
  public void updateRowId(int columnIndex, RowId x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateRowId131), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateRowId(String columnLabel, RowId x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateRowId132), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public int getHoldability() throws SQLException {
    return (Integer) tryToInvoke(getHoldability);
  }

  @Override
  public boolean isClosed() throws SQLException {
    return (Boolean) tryToInvoke(isClosed);
  }

  @Override
  public void updateNString(int columnIndex, String nString) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNString120), new Object[]{columnIndex, nString});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateNString(String columnLabel, String nString) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNString121), new Object[]{columnLabel, nString});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNClob114), new Object[]{columnIndex, nClob});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNClob115), new Object[]{columnLabel, nClob});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public NClob getNClob(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_NClob), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (NClob) tryToInvoke(invoke);
  }

  @Override
  public NClob getNClob(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getNClob29), new Object[]{});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (NClob) tryToInvoke(invoke);
  }

  @Override
  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_SQLXML), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (SQLXML) tryToInvoke(invoke);
  }

  @Override
  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getSQLXML37), new Object[]{});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (SQLXML) tryToInvoke(invoke);
  }

  @Override
  public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateSQLXML135), new Object[]{columnIndex, xmlObject});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateSQLXML136), new Object[]{columnLabel, xmlObject});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public String getNString(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_NString), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (String) tryToInvoke(invoke);
  }

  @Override
  public String getNString(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getNString30), new Object[]{});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (String) tryToInvoke(invoke);
  }

  @Override
  public Reader getNCharacterStream(int columnIndex) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getReadMethod(ResultSetProxyBeanInfo.PROPERTY_NCharacterStream), new Object[]{columnIndex});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Reader) tryToInvoke(invoke);
  }

  @Override
  public Reader getNCharacterStream(String columnLabel) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_getNCharacterStream28), new Object[]{});
    if (reads.isEmpty()) {
      reads.add(invoke);
    } else {
      reads.set(0, invoke);
    }
    return (Reader) tryToInvoke(invoke);
  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNCharacterStream110), new Object[]{columnIndex, x, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNCharacterStream111), new Object[]{columnLabel, reader, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateAsciiStream64), new Object[]{columnIndex, x, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBinaryStream72), new Object[]{columnIndex, x, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateCharacterStream90), new Object[]{columnIndex, x, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateAsciiStream64), new Object[]{columnLabel, x, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBinaryStream72), new Object[]{columnLabel, x, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateCharacterStream90), new Object[]{columnLabel, reader, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBlob78), new Object[]{columnIndex, inputStream, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBlob79), new Object[]{columnLabel, inputStream, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateClob96), new Object[]{columnIndex, reader, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateClob97), new Object[]{columnLabel, reader, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNClob116), new Object[]{columnIndex, reader, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNClob117), new Object[]{columnLabel, reader, length});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNCharacterStream112), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNCharacterStream113), new Object[]{columnLabel, reader});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateAsciiStream66), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBinaryStream74), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateCharacterStream92), new Object[]{columnIndex, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateAsciiStream67), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBinaryStream75), new Object[]{columnLabel, x});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateCharacterStream93), new Object[]{columnLabel, reader});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBlob80), new Object[]{columnIndex, inputStream});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateBlob81), new Object[]{columnLabel, inputStream});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateClob(int columnIndex, Reader reader) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateClob98), new Object[]{columnIndex, reader});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateClob(String columnLabel, Reader reader) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateClob99), new Object[]{columnLabel, reader});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNClob118), new Object[]{columnIndex, reader});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public void updateNClob(String columnLabel, Reader reader) throws SQLException {
    Invocation<ResultSet> invoke = new Invocation<ResultSet>(getMethod(ResultSetProxyBeanInfo.METHOD_updateNClob119), new Object[]{columnLabel, reader});
    update.add(invoke);
    tryToInvoke(invoke);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    unwrap.arguments = new Object[]{iface};
    return (T) tryToInvoke(unwrap);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    isWrapperFor.arguments = new Object[]{iface};
    return (Boolean) tryToInvoke(isWrapperFor);
  }
}
