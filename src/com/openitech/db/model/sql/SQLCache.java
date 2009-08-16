/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.sql;

import com.openitech.CollectionKey;
import com.openitech.db.ConnectionManager;
import com.sun.rowset.CachedRowSetImpl;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author uros
 */
public class SQLCache implements Serializable {
  private static final long TTL = 15000; //15s
  private static transient Map<CollectionKey<Object>, PreparedStatement> sharedStatements = new ConcurrentHashMap<CollectionKey<Object>, PreparedStatement>();
  private static Map<CollectionKey<Object>, SharedEntry> sharedResults = new ConcurrentHashMap<CollectionKey<Object>, SharedEntry>();

  public static CachedRowSet getSharedResult(Connection connection, String query, List<Object> parameters, boolean reload, long TTL) throws SQLException {
    CollectionKey<Object> key = new CollectionKey<Object>(parameters.size() + 2);
    connection = connection==null?ConnectionManager.getInstance().getConnection():connection;
    key.add(connection);
    key.add(query);
    key.addAll(parameters);

    SharedEntry result = new SharedEntry(connection, query, parameters, TTL);
    if (sharedResults.containsKey(result.entryKey)) {
      result = sharedResults.get(key);
    } else {
      sharedResults.put(key, result);
    }

    return result.getEntry(reload);
  }

  public static PreparedStatement getSharedStatement(Connection connection, String query) throws SQLException {
    CollectionKey<Object> statementKey = new CollectionKey<Object>(2);
    connection = connection==null?ConnectionManager.getInstance().getConnection():connection;
    statementKey.add(connection);
    statementKey.add(query);

    PreparedStatement statement;


    if (sharedStatements.containsKey(statementKey)) {
      statement = sharedStatements.get(statementKey);
    } else {
      statement = connection.prepareStatement(query,
              ResultSet.TYPE_FORWARD_ONLY,
              ResultSet.CONCUR_READ_ONLY);
      statement.setFetchSize(1008);

      sharedStatements.put(statementKey, statement);
    }

    return statement;
  }

  public static ResultSet getSharedResult(Connection connection, String query, List<Object> parameters) throws SQLException {
    return getSharedResult(connection, query, parameters, false, TTL);
  }

  public static ResultSet getSharedResult(Connection connection, String query, List<Object> parameters, boolean reload) throws SQLException {
    return getSharedResult(connection, query, parameters, reload, TTL);
  }

  public static class SharedEntry implements Serializable {

    private Semaphore lock = new Semaphore(1);
    private long timestamp = System.currentTimeMillis();
    private long ttl = 5000; //5s
    private CollectionKey<Object> statementKey;
    private CollectionKey<Object> entryKey;
    private CachedRowSet entry;
    private final Connection connection;
    private final String query;
    private final List<Object> parameters;

    public SharedEntry(Connection connection, String query, List<Object> parameters, long ttl) throws SQLException {
      this.connection = connection==null?ConnectionManager.getInstance().getConnection():connection;
      this.query = query;

      statementKey = new CollectionKey<Object>(2);
      statementKey.add(connection);
      statementKey.add(query);

      ParameterMetaData metaData = getStatement().getParameterMetaData();
      int parameterCount = metaData.getParameterCount();

      List<Object> target = SQLDataSource.getParameters(parameters);

      while (target.size()<parameterCount) {
        target.add(null);
      }

      while (target.size()>parameterCount) {
        target.remove(target.size()-1);
      }

      entryKey = new CollectionKey<Object>(target.size() + 1);

      entryKey.add(query);
      entryKey.addAll(target);

      this.parameters = Collections.unmodifiableList(target);
    }

    public PreparedStatement getStatement() throws SQLException {
      PreparedStatement statement;


      if (sharedStatements.containsKey(statementKey)) {
        statement = sharedStatements.get(statementKey);
      } else {
        statement = connection.prepareStatement(query,
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1008);

        sharedStatements.put(statementKey, statement);
      }

      return statement;
    }

    public CachedRowSet getEntry(boolean reload) throws SQLException {
      try {
        lock.acquire();
        try {
          if (reload || (entry == null) || (!isValid(ttl))) {
            entry = new CachedRowSetImpl();
            entry.populate(SQLDataSource.executeQuery( getStatement(), parameters));
            timestamp = System.currentTimeMillis();
          }
        } finally {
          lock.release();
        }
      } catch (InterruptedException ex) {
        Logger.getLogger(SQLCache.class.getName()).log(Level.SEVERE, null, ex);
      }

      CachedRowSet result = new CachedRowSetImpl();
      entry.beforeFirst();
      result.populate(entry);
      return result;
    }

    protected boolean isValid(long TTL) {
      return TTL > (System.currentTimeMillis() - timestamp);
    }
  }
}
