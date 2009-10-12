/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.sql;

import com.openitech.CollectionKey;
import com.openitech.NamedValue;
import com.openitech.db.ConnectionManager;
import com.openitech.db.model.DbDataSource;
import com.openitech.ref.events.PropertyChangeWeakListener;
import com.openitech.sql.FieldValue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class PendingSqlParameter extends DbDataSource.SubstSqlParameter implements PropertyChangeListener {

  private java.util.Set<String> pendingFields = new java.util.LinkedHashSet<String>();
  private java.util.Set<String> parentKeyFields = new java.util.LinkedHashSet<String>();

  public PendingSqlParameter(String replace) {
    super(replace);
    addPropertyChangeListener("value", new PropertyChangeWeakListener(this));
  }
  private String immediateSQL;

  /**
   * Get the value of immediateSQL
   *
   * @return the value of immediateSQL
   */
  public String getImmediateSQL() {
    return immediateSQL;
  }

  /**
   * Set the value of immediateSQL
   *
   * @param immediateSQL new value of immediateSQL
   */
  public void setImmediateSQL(String immediateSQL) {
    this.immediateSQL = immediateSQL;
    updateValue();
  }
  private String pendingSQL;

  /**
   * Get the value of pendingSQL
   *
   * @return the value of pendingSQL
   */
  public String getPendingSQL() {
    return pendingSQL;
  }

  /**
   * Set the value of pendingSQL
   *
   * @param pendingSQL new value of pendingSQL
   */
  public void setPendingSQL(String pendingSQL) {
    setPendingSQL(pendingSQL, (String[]) null);
  }

  /**
   * Set the value of pendingSQL
   *
   * @param pendingSQL new value of pendingSQL
   */
  public void setPendingSQL(String pendingSQL, String... fieldNames) {
    this.pendingSQL = pendingSQL;
    if (fieldNames != null) {
      setPendingFields(fieldNames);
    }
    updateValue();
  }
  private String deferredSQL;

  /**
   * Get the value of deferredSQL
   *
   * @return the value of deferredSQL
   */
  public String getDeferredSQL() {
    return deferredSQL;
  }

  /**
   * Set the value of deferredSQL
   *
   * @param deferredSQL new value of deferredSQL
   */
  public void setDeferredSQL(String deferredSQL) {
    setDeferredSQL(deferredSQL, (String[]) null);
  }

  /**
   * Set the value of deferredSQL
   *
   * @param deferredSQL new value of deferredSQL
   */
  public void setDeferredSQL(String deferredSQL, String... parentFieldNames) {
    this.deferredSQL = deferredSQL;
    if (deferredStatement != null) {
      try {
        deferredStatement.close();
      } catch (SQLException ex) {
        Logger.getLogger(PendingSqlParameter.class.getName()).log(Level.SEVERE, null, ex);
      }
      deferredStatement = null;
    }
    if (parentFieldNames != null) {
      setParentFields(parentFieldNames);
    }
    updateValue();
  }
  PreparedStatement deferredStatement;

//  public java.util.List<PendingValue> getPendingValues(DbDataSource parentDataSource) throws SQLException {
//    java.util.List<Object> deferredSqlParameters = new java.util.ArrayList<Object>();
//    for (String keyField : parentKeyFields) {
//      deferredSqlParameters.add(parentDataSource.getObject(keyField));
//    }
//
//    return getPendingValues(deferredSqlParameters);
//  }
  public Set<String> getParentKeyFields() {
    return Collections.unmodifiableSet(parentKeyFields);
  }

  public void setParentKeyFields(Set<String> parentKeyFields) {
    this.parentKeyFields.clear();
    this.parentKeyFields.addAll(parentKeyFields);
  }
  private boolean supportsMultipleKeys = false;

  /**
   * Get the value of supportsMultipleKeys
   *
   * @return the value of supportsMultipleKeys
   */
  public boolean isSupportsMultipleKeys() {
    return supportsMultipleKeys;
  }

  /**
   * Set the value of supportsMultipleKeys
   *
   * @param supportsMultipleKeys new value of supportsMultipleKeys
   */
  public void setSupportsMultipleKeys(boolean supportsMultipleKeys) {
    this.supportsMultipleKeys = supportsMultipleKeys;
  }
  private int multipleKeysLimit = Integer.MIN_VALUE;

  /**
   * Get the value of multipleKeysLimit
   *
   * @return the value of multipleKeysLimit
   */
  public int getMultipleKeysLimit() {
    return multipleKeysLimit;
  }

  /**
   * Set the value of multipleKeysLimit
   *
   * @param multipleKeysLimit new value of multipleKeysLimit
   */
  public void setMultipleKeysLimit(int multipleKeysLimit) {
    this.multipleKeysLimit = multipleKeysLimit;
  }

  private java.util.List<PendingValue> getPendingValues(java.util.List<Object> parameters) throws SQLException {
    java.util.List<PendingValue> result = new java.util.ArrayList<PendingValue>();

    if (deferredStatement == null) {
      String sql = DbDataSource.substParameters(deferredSQL, parameters);
      this.deferredStatement = ConnectionManager.getInstance().getConnection().prepareStatement(sql,
              ResultSet.TYPE_SCROLL_INSENSITIVE,
              ResultSet.CONCUR_READ_ONLY,
              ResultSet.HOLD_CURSORS_OVER_COMMIT);
    }

    ResultSet executeQuery = DbDataSource.executeQuery(deferredStatement, parameters);
    try {
      if (executeQuery.next()) {
        for (String pendingField : pendingFields) {
          Object fieldValue = executeQuery.getObject(pendingField);
          result.add(new PendingValue(pendingField, executeQuery.wasNull() ? null : fieldValue));
        }
      }
    } finally {
      executeQuery.close();
    }

    return result;
  }
  private transient Map<CollectionKey<NamedValue>, java.util.List<PendingValue>> pendingValuesCache = new HashMap<CollectionKey<NamedValue>, java.util.List<PendingValue>>();

  public java.util.Map<CollectionKey<NamedValue>, java.util.List<PendingValue>> getPendingValues(java.util.Map<CollectionKey<NamedValue>, java.util.List<Object>> query, boolean cache) throws SQLException {
    java.util.Map<CollectionKey<NamedValue>, java.util.List<PendingValue>> result = new java.util.HashMap<CollectionKey<NamedValue>, java.util.List<PendingValue>>();

    if (!supportsMultipleKeys) {
      for (java.util.Map.Entry<CollectionKey<NamedValue>, java.util.List<Object>> entry : query.entrySet()) {
        result.put(entry.getKey(), getPendingValues(entry.getValue()));
      }
    } else if ((multipleKeysLimit > 0) && (query.size() > multipleKeysLimit)) {
      //koda ni stestirana, ker ni bilo se potrebe
      java.util.Map<CollectionKey<NamedValue>, java.util.List<Object>> subQuery = new java.util.HashMap<CollectionKey<NamedValue>, java.util.List<Object>>();
      for (java.util.Map.Entry<CollectionKey<NamedValue>, java.util.List<Object>> entry : query.entrySet()) {
        if (cache && pendingValuesCache.containsKey(entry.getKey())) {
          result.put(entry.getKey(), pendingValuesCache.get(entry.getKey()));
        } else {
          subQuery.put(entry.getKey(), entry.getValue());
          if (subQuery.size() == multipleKeysLimit) {
            result.putAll(getPendingValues(subQuery, false));
            subQuery.clear();
          }
        }
      }
      if (subQuery.size() > 0) {
        result.putAll(getPendingValues(subQuery, false));
        subQuery.clear();
      }
      if (cache) {
        pendingValuesCache = result;
      }
    } else if (cache) {
      return pendingValuesCache;
    } else {
      java.util.List<Object> parameters = new java.util.ArrayList<Object>();
      for (java.util.List<Object> parameterSet : query.values()) {
        parameters.addAll(parameterSet);
      }
      if (deferredStatement == null) {
        String sql = DbDataSource.substParameters(deferredSQL, parameters);
        this.deferredStatement = ConnectionManager.getInstance().getConnection().prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY,
                ResultSet.HOLD_CURSORS_OVER_COMMIT);
      }

      ResultSet executeQuery = DbDataSource.executeQuery(deferredStatement, parameters);
      try {

        while (executeQuery.next()) {
          java.util.List<PendingValue> values = new java.util.ArrayList<PendingValue>();
          CollectionKey<NamedValue> key = new CollectionKey<NamedValue>(parentKeyFields.size());

          for (String parentKeyField : parentKeyFields) {
            Object fieldValue = executeQuery.getObject(parentKeyField);
            key.add(new NamedValue(parentKeyField, executeQuery.wasNull() ? null : fieldValue));
          }

          for (String pendingField : pendingFields) {
            Object fieldValue = executeQuery.getObject(pendingField);
            values.add(new PendingValue(pendingField, executeQuery.wasNull() ? null : fieldValue));
          }

          result.put(key, values);
        }
      } finally {
        executeQuery.close();
      }
    }

    if (cache) {
      pendingValuesCache = result;
    }

    return result;
  }
  protected boolean immediate = false;

  /**
   * Get the value of immediate
   *
   * @return the value of immediate
   */
  public boolean isImmediate() {
    return immediate;
  }

  /**
   * Set the value of immediate
   *
   * @param immediate new value of immediate
   */
  public void setImmediate(boolean immediate) {
    if (this.immediate != immediate) {
      this.immediate = immediate;
      updateValue();
    }
  }

  private void updateValue() {
    if (isImmediate()) {
      setValue(getImmediateSQL());
    } else {
      setValue(getPendingSQL());
    }
  }

  public boolean isPending(String fieldName) {
    return getPendingFields().contains(fieldName.toUpperCase());
  }

  public Set<String> getPendingFields() {
    if (immediate) {
      return Collections.unmodifiableSet(new LinkedHashSet<String>());
    } else {
      return Collections.unmodifiableSet(pendingFields);
    }
  }

  public void setPendingFields(Set<String> pendingFields) {
    this.pendingFields.clear();
    this.pendingFields.addAll(pendingFields);
  }

  private void setPendingFields(String... fieldNames) {
    pendingFields.clear();
    for (String fieldName : fieldNames) {
      pendingFields.add(fieldName.toUpperCase());
    }
  }

  private void setParentFields(String... fieldNames) {
    parentKeyFields.clear();
    for (String fieldName : fieldNames) {
      parentKeyFields.add(fieldName.toUpperCase());
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("value")) {
      firePropertyChange("query", evt.getOldValue(), evt.getNewValue());
    }
  }
}
