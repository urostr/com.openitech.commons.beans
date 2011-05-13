/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.sql;

import com.openitech.text.CaseInsensitiveString;
import com.openitech.value.CollectionKey;
import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.DbDataSource;
import com.openitech.ref.SoftHashMap;
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
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author uros
 */
public class SQLCache implements Serializable {

  private static SQLCache instance;
  private long ttl = 15000; //15s
  private transient Map<CollectionKey<Object>, PreparedStatement> sharedStatements = Collections.synchronizedMap(new SoftHashMap<CollectionKey<Object>, PreparedStatement>());
  private Map<CollectionKey<Object>, SharedEntry> sharedResults = Collections.synchronizedMap(new SoftHashMap<CollectionKey<Object>, SharedEntry>());

  public SQLCache() {
  }

  public static final SQLCache getInstance() {
    if (instance == null) {
      instance = new SQLCache();
    }
    return instance;
  }

  public void clearSharedResults() {
    sharedResults.clear();
  }

  private SharedEntry getSharedEntry(String query, List<Object> parameters, long TTL) throws SQLException {
    return new SharedEntry(null, query, parameters, TTL);
  }

  public CachedRowSet getSharedResult(String query, List<Object> parameters, boolean reload, long TTL) throws SQLException {
    SharedEntry result = getSharedEntry(query, parameters, TTL);
    CollectionKey<Object> key = result.entryKey;
    if (sharedResults.containsKey(key)) {
      result = sharedResults.get(key);
    } else {
      sharedResults.put(key, result);
    }

    return result.getEntry(reload);
  }

  public PreparedStatement getSharedStatement(Connection connection, String query) throws SQLException {
    CollectionKey<Object> statementKey = new CollectionKey<Object>(2);
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

  public void removeSharedResult(String query, List<Object> parameters) throws SQLException {
    if (query == null || query.equals("")) {
      return;
    }
    SharedEntry result = getSharedEntry(query, parameters, ttl);

    if (result != null) {
      sharedResults.remove(result.entryKey);
    }
  }

  public ResultSet getSharedResult(String query, List<Object> parameters) throws SQLException {
    return getSharedResult(query, parameters, false, ttl);
  }

  public ResultSet getSharedResult(String query, List<Object> parameters, boolean reload) throws SQLException {
    return getSharedResult(query, parameters, reload, ttl);
  }

  public PreparedStatement getSharedCall(Connection connection, String query) throws SQLException {
    CollectionKey<Object> statementKey = new CollectionKey<Object>(2);
    statementKey.add(connection);
    statementKey.add(query);

    PreparedStatement statement;


    if (sharedStatements.containsKey(statementKey)) {
      statement = sharedStatements.get(statementKey);
    } else {
      statement = connection.prepareCall(query,
              ResultSet.TYPE_FORWARD_ONLY,
              ResultSet.CONCUR_READ_ONLY);
      statement.setFetchSize(1008);

      sharedStatements.put(statementKey, statement);
    }

    return statement;
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
    private boolean createCopy = true;

    public SharedEntry(Connection connection, String query, List<Object> parameters, long ttl) throws SQLException {
      this.connection = connection;
      this.query = query;
      this.ttl = ttl;

      statementKey = new CollectionKey<Object>(2);
      if (connection != null) {
        statementKey.add(connection);
      }
      statementKey.add(query);

      List<Object> target = SQLDataSource.getParameters(parameters);

      Connection temporary = null;
      if (connection == null) {
        temporary = ConnectionManager.getInstance().getTemporaryConnection();
      }
      try {
        ParameterMetaData metaData = getStatement(temporary != null ? temporary : connection).getParameterMetaData();
        int parameterCount = metaData.getParameterCount();


        while (target.size() < parameterCount) {
          target.add(null);
        }

        while (target.size() > parameterCount) {
          target.remove(target.size() - 1);
        }
      } finally {
        if (temporary != null) {
          temporary.close();
        }
      }

      entryKey = new CollectionKey<Object>(target.size() + 1);

      entryKey.add(new CaseInsensitiveString(query));
      entryKey.addAll(target);

      this.parameters = Collections.unmodifiableList(target);
    }

    private PreparedStatement getStatement(Connection connection) throws SQLException {
      PreparedStatement statement;
//      if (!ConnectionManager.getInstance().isPooled() && sharedStatements.containsKey(statementKey)) {
//        statement = sharedStatements.get(statementKey);
//      } else {

      statement = connection.prepareStatement(query,
              ResultSet.TYPE_FORWARD_ONLY,
              ResultSet.CONCUR_READ_ONLY);
      statement.setFetchSize(1008);

//        if (!ConnectionManager.getInstance().isPooled()) {
//          sharedStatements.put(statementKey, statement);
//        }
//      }

      return statement;
    }

    private CachedRowSet reloadEntry() throws SQLException {
      try {
        lock.acquire();
        try {
          entry = new CachedRowSetImpl();
          Connection connection = (this.connection == null) ? ConnectionManager.getInstance().getTemporaryConnection() : this.connection;
          try {
            if (DbDataSource.DUMP_SQL) {
              Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Reloading cached entry ####################);");
              Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(query);
            }
            long start = System.currentTimeMillis();
            final ResultSet rs = SQLDataSource.executeQuery(getStatement(connection), parameters);
            try {
              entry.populate(rs);
            } finally {
              rs.close();
            }
            long end = System.currentTimeMillis();
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("reloadEntry::" + (end - start) + " ms.");
          } finally {
            if (this.connection == null) {
              connection.close();
            }
          }
          timestamp = System.currentTimeMillis();
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Reloaded cache entry ###########################");
        } finally {
          lock.release();
        }
      } catch (InterruptedException ex) {
        Logger.getLogger(SQLCache.class.getName()).log(Level.SEVERE, null, ex);
        //TODO Ali se lock vedno sprosti?
      }

      return entry;
    }

    public CachedRowSet getEntry(boolean reload) throws SQLException {
      boolean reloaded = false;
      if (reload || (entry == null) || (!isValid(ttl))) {
        reloadEntry();
        reloaded = true;
      }

      CachedRowSet result = null;

      if (createCopy) {
        try {
          result = entry.createCopy();
          //javi napako ko hocemo kopirat clobe
        } catch (SQLException ex) {
          Logger.getLogger(SQLCache.class.getName()).log(Level.WARNING, ex.getMessage());
          //zato pri tem sql-u disablamu uporabo create copy
          createCopy = false;
        }
      }

      if (!createCopy) {
        result = new CachedRowSetImpl();
        try {
          result.populate(entry);
          //stvar vcasih javi invalid cursor position, verjetno zaradi razlicnih threadov
        } catch (SQLException ex) {
          Logger.getLogger(SQLCache.class.getName()).log(Level.WARNING, ex.getMessage());

          result = reloaded ? entry : reloadEntry();
          entry = null;
        }
      }

      return result;
    }

    protected boolean isValid(long ttl) {
      if (ttl < 0) {
        return true;
      } else {
        return ttl > (System.currentTimeMillis() - timestamp);
      }
    }
  }
}
