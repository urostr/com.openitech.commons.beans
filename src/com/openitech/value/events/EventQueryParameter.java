/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.value.events;

import com.openitech.db.model.DbDataSource;
import com.openitech.value.fields.Field;
import java.util.Map;

/**
 *
 * @author uros
 */
public abstract class EventQueryParameter extends DbDataSource.SubstSqlParameter {
  public static final String EV_QUERY_REPLACE = "<%ev_events_subquery%>";

  protected final DbDataSource.SqlParameter<Object> versionId;

  public EventQueryParameter(Map<Field, DbDataSource.SqlParameter<Object>> namedParameters) {
    super(EV_QUERY_REPLACE);

    this.versionId = namedParameters.containsKey(Field.VERSION) ? namedParameters.get(Field.VERSION) : new DbDataSource.SqlParameter<Object>(java.sql.Types.BIGINT, null);
    namedParameters.put(Field.VERSION, this.versionId);
  }

  public abstract boolean hasVersionId();

  @Override
  public abstract java.util.List<Object> getParameters();

  @Override
  public abstract String getValue();
}