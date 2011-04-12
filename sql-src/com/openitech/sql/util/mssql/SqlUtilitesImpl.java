/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.util.mssql;

import com.openitech.value.events.EventType;
import com.openitech.db.model.xml.config.MaterializedView;
import com.openitech.db.model.xml.config.TemporaryTable;
import com.openitech.text.CaseInsensitiveString;
import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.components.DbNaslovDataModel;
import com.openitech.db.connection.DbConnection;
import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSource.SqlParameter;
import com.openitech.db.model.DbDataSourceIndex;
import com.openitech.db.model.factory.DataSourceConfig;
import com.openitech.db.model.factory.DataSourceFactory;
import com.openitech.db.model.factory.DataSourceParametersFactory;
import com.openitech.db.model.sql.SQLDataSource;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.value.fields.Field;
import com.openitech.value.events.Event;
import com.openitech.value.events.EventQuery;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.fields.FieldValue;
import com.openitech.value.fields.ValueType;
import com.openitech.value.events.ActivityEvent;
import com.openitech.io.ReadInputStream;
import com.openitech.jdbc.proxy.DataSourceProxy;
import com.openitech.ref.SoftHashMap;
import com.openitech.sql.cache.CachedTemporaryTablesManager;
import com.openitech.util.Equals;
import com.openitech.value.StringValue;
import com.openitech.value.VariousValue;
import com.openitech.value.events.EventPK;
import com.openitech.value.events.EventQueryParameter;
import com.openitech.value.events.SqlEventPK;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author uros
 */
public class SqlUtilitesImpl extends SqlUtilities {

  protected boolean override = Boolean.parseBoolean(ConnectionManager.getInstance().getProperty(ConnectionManager.DB_OVERRIDE_CACHED_VIEWS, "false"));
  PreparedStatement logChanges;
  PreparedStatement logValues;
  PreparedStatement logChangedValues;
  PreparedStatement updateEvents;
  PreparedStatement delete_event;
  PreparedStatement insertEvents;
  PreparedStatement findEventValue;
  PreparedStatement findEventById;
  PreparedStatement insertEventValues;
  PreparedStatement updateEventValues;
  PreparedStatement deleteEventValues;
  PreparedStatement find_datevalue;
  PreparedStatement find_intvalue;
  PreparedStatement find_realvalue;
  PreparedStatement find_stringvalue;
  PreparedStatement find_clobvalue;
  PreparedStatement get_field;
  PreparedStatement get_fields;
  PreparedStatement insertNeznaniNaslov;
  PreparedStatement findHsNeznanaId;
  PreparedStatement insertEventsOpombe;
  PreparedStatement findOpomba;
  PreparedStatement insertVersion;
  PreparedStatement insertEventVersion;
  PreparedStatement findVersion;
  PreparedStatement getVersionedEventIds;
  PreparedStatement insertUpdatedVersion;
  PreparedStatement findVersionTypes;
  String getEventVersionSQL;
  PreparedStatement storeCachedTemporaryTable;
  PreparedStatement deleteCachedTemporaryTable;
  PreparedStatement storeCachedEventObject;
  PreparedStatement updateCachedEventObject;
  PreparedStatement deleteCachedEventObject;
  PreparedStatement delete_eventPK;
  PreparedStatement insert_eventPK;
  PreparedStatement find_eventPK;
  PreparedStatement update_eventPK;
  PreparedStatement insert_eventPK_versions;
  PreparedStatement find_eventPK_versions;
  PreparedStatement find_eventPK_versions_byValues;
  PreparedStatement update_eventPK_versions;
  PreparedStatement insert_eventLookupKeys;
  PreparedStatement update_eventLookupKeys;
  PreparedStatement find_eventLookupKeys;
  PreparedStatement findValue;
  CallableStatement callStoredValue;
  PreparedStatement insertScheduler;
  PreparedStatement delete_eventPKVersions;
  PreparedStatement update_eventPK_versions_byValue;
  PreparedStatement search_by_PK;
  PreparedStatement find_event_by_PK;
  PreparedStatement findIdentityEventId;
  Map<CaseInsensitiveString, Field> preparedFields;
  Map<EventType, List<EventCacheTemporaryParameter>> cachedEventObjects;

  @Override
  public long getScopeIdentity() throws SQLException {
    Statement statement = ConnectionManager.getInstance().getTxConnection().createStatement();
    try {

      ResultSet result = statement.executeQuery("SELECT SCOPE_IDENTITY() AS ScopeIdentity");
      result.next();

      return result.getLong(1);
    } finally {
      statement.close();
    }
  }

  @Override
  public long getCurrentIdentity(String tableName) throws SQLException {
    Statement statement = ConnectionManager.getInstance().getTxConnection().createStatement();
    try {

      ResultSet result = statement.executeQuery("SELECT IDENT_CURRENT(" + tableName + ") AS CurrentIdentity");
      result.next();

      return result.getLong(1);
    } finally {
      statement.close();
    }
  }

  @Override
  public long getLastIdentity() throws SQLException {
    Statement statement = ConnectionManager.getInstance().getTxConnection().createStatement();
    try {
      ResultSet result = statement.executeQuery("SELECT @@IDENTITY AS [Identity]");
      result.next();

      return result.getLong(1);
    } finally {
      statement.close();
    }
  }

  @Override
  protected void logChanges(String application, String database, String tableName, Operation operation, List<FieldValue> newValues, List<FieldValue> oldValues) throws SQLException {
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (logChanges == null) {
      logChanges = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insert_change_log.sql", "cp1250"));
    }
    if (logValues == null) {
      logValues = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insert_values.sql", "cp1250"));
    }

    if (logChangedValues == null) {
      logChangedValues = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insert_changed_values.sql", "cp1250"));
    }

    FieldValue[] fieldValues = new FieldValue[]{
      new FieldValue("application", java.sql.Types.VARCHAR, application),
      new FieldValue("database", java.sql.Types.VARCHAR, database),
      new FieldValue("tableName", java.sql.Types.VARCHAR, tableName),
      new FieldValue("operation", java.sql.Types.VARCHAR, operation.toString())
    };

    executeUpdate(logChanges, fieldValues);

    long changeId = getLastIdentity();

    for (int fieldno = 0; fieldno < newValues.size(); fieldno++) {
      FieldValue newValue = newValues.get(fieldno);
      FieldValue oldValue = oldValues.get(fieldno);

      final Object value = newValue.getValue();
      final int fieldType = newValue.getValueType().getTypeIndex();

      Long newValueId = storeValue(fieldType, value);
      Long oldValueId = storeValue(fieldType, oldValue.getValue());

      fieldValues = new FieldValue[]{
                new FieldValue("ChangeId", Types.BIGINT, changeId),
                new FieldValue("FieldName", Types.VARCHAR, newValue.getName()),
                new FieldValue("NewValueId", Types.BIGINT, newValueId),
                new FieldValue("OldValueId", Types.BIGINT, oldValueId)
              };

      executeUpdate(logChangedValues, fieldValues);
    }
  }

  private boolean isVersioned(List<EventPK> eventPKs) {
    boolean result = true;
    for (EventPK eventPK : eventPKs) {
      result = result && eventPK.isVersioned();
      if (!result) {
        break;
      }
    }
    return result;
  }

  private static class VersionType extends EventType {

    public VersionType(int sifrant, String sifra, Integer versionId) {
      super(sifrant, sifra);
      this.versionId = versionId;
    }
    protected Integer versionId;

    /**
     * Get the value of versionId
     *
     * @return the value of versionId
     */
    public Integer getVersionId() {
      return versionId;
    }

    /**
     * Set the value of versionId
     *
     * @param versionId new value of versionId
     */
    public void setVersionId(Integer versionId) {
      this.versionId = versionId;
    }
//    @Override
//    public boolean equals(Object obj) {
//      if (obj == null) {
//        return false;
//      }
//      final EventType other = (EventType) obj;
//      if (this.sifrant != other.getSifrant()) {
//        return false;
//      }
//      if ((this.sifra == null) ? (other.getSifra() != null) : !this.sifra.equals(other.getSifra())) {
//        return false;
//      }
//      return true;
//    }
  }

  @Override
  protected Integer assignEventVersion(EventType parent, List<EventPK> eventPKs) throws SQLException {
    if (!eventPKs.isEmpty()) {
      //iskanje verzije, ampak se naj nebi noben event dodal oz spremenil,
      //zato je to brez veze
      Integer versionId = getVersion(eventPKs);

      if (versionId == null) {

        if (isVersioned(eventPKs)) {
          //najprej dodaj verzijo (tabela Versions)
          versionId = new Integer((int) storeVersion(parent));
          //nato v tabelo EventVersions vpisi z gornjo verzijo vse podane eventId-je
          List<Long> storedEventIds = new ArrayList<Long>();
          for (EventPK eventPK : eventPKs) {
            if (!storedEventIds.contains(eventPK.getEventId())) {
              storedEventIds.add(eventPK.getEventId());
              storeEventVersion(versionId, eventPK.getEventId());
              eventPK.setVersionID(versionId);
              storePrimaryKeyVersions(eventPK);
            }
          }
        }
      }

      List<Long> oldEventIds1 = new ArrayList<Long>(eventPKs.size());
      for (EventPK eventPK : eventPKs) {
        final Long oldEventId = eventPK.getOldEventId();
        if (oldEventId != null) {
          oldEventIds1.add(oldEventId);
        }
      }

      Map<Long, List<VersionType>> versionTypesMap = findVersionTypes(oldEventIds1);

      if (versionTypesMap != null && versionTypesMap.size() > 0) {
        //seznam verzij, ki jih bo potrebno updejtat
        Map<VersionType, List<EventPK>> updateEventVersions = new HashMap<VersionType, List<EventPK>>();

        //cez vse shranjene EventId-je, vkljuèno s starimi
        for (EventPK eventPK : eventPKs) {
          if (eventPK.getOldEventId() != null) {
            List<VersionType> versionTypes = versionTypesMap.get(eventPK.getOldEventId());//findVersionTypes(eventPK.getOldEventId());
            if (versionTypes != null) {
              for (VersionType versionType : versionTypes) {
                //parent je sifrant, ki ja najvisji za trenutno workarea
                //iscem samo tiste versionirane dogodke, ki niso parent, ker parent se sam updejta
                if (!versionType.equals(parent)) {
                  if (!updateEventVersions.containsKey(versionType)) {
                    updateEventVersions.put(versionType, new ArrayList<EventPK>());
                  }
                  updateEventVersions.get(versionType).add(eventPK);
                }
              }
            }
          }
        }

        for (Map.Entry<VersionType, List<EventPK>> entry : updateEventVersions.entrySet()) {
          List<Long> newEventIds = new ArrayList<Long>(entry.getValue().size());
          List<Long> oldEventIds = new ArrayList<Long>(entry.getValue().size());

          for (EventPK eventPK : entry.getValue()) {
            if ((eventPK.getEventId() != null)
                    && (eventPK.getOldEventId() != null)) {
              newEventIds.add(eventPK.getEventId());
              oldEventIds.add(eventPK.getOldEventId());
            }
          }
          updateVersion(entry.getKey().getVersionId(), newEventIds, oldEventIds);
        }
      }
      return versionId;
    } else {
      return null;
    }
  }

  private Integer getVersion(List<EventPK> eventPKs) throws SQLException {

    final Connection connection = ConnectionManager.getInstance().getTxConnection();


    if (getEventVersionSQL == null) {
      getEventVersionSQL = com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "getEventVersion.sql", "cp1250");
    }

    StringBuilder sb = new StringBuilder();

    List<Long> eventIds = new ArrayList<Long>(eventPKs.size());
    for (EventPK eventPK : eventPKs) {
      sb.append(sb.length() > 0 ? ", " : "").append("?");
      eventIds.add(eventPK.getEventId());
    }

    CachedRowSet versions = new com.sun.rowset.CachedRowSetImpl();
    ResultSet rs = SQLDataSource.executeQuery(getEventVersionSQL.replaceAll("<%EVENTS_LIST%>", sb.toString()).replaceAll("<%EVENT_LIST_SIZE%>", Integer.toString(eventIds.size())), eventIds, connection);
    try {
      versions.populate(rs);
    } finally {
      rs.close();
    }

    if ((versions.size() == 1) && (versions.first())) {
      return versions.getInt(1);
    } else {
      return null;
    }
  }

  @Override
  protected long storeVersion(EventType eventType) throws SQLException {
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (insertVersion == null) {
      insertVersion = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insertVersion.sql", "cp1250"));
    }

    synchronized (insertVersion) {
      int param = 1;
      insertVersion.clearParameters();
      insertVersion.setInt(param++, eventType.getSifrant());
      insertVersion.setString(param++, eventType.getSifra());
      insertVersion.executeUpdate();
      return getLastIdentity();
    }
  }

  private void storeEventVersion(long versionId, Long eventID) throws SQLException {
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (insertEventVersion == null) {
      insertEventVersion = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insertEventVersion.sql", "cp1250"));
    }

    synchronized (insertEventVersion) {
      int param = 1;
      insertEventVersion.clearParameters();
      insertEventVersion.setLong(param++, versionId);
      insertEventVersion.setLong(param++, eventID);
      System.out.println("versionId = " + versionId + ", eventId = " + eventID);
      insertEventVersion.executeUpdate();
    }
  }

  private Map<Long, List<VersionType>> findVersionTypes(List<Long> eventIds) throws SQLException {
    if (eventIds == null || eventIds.isEmpty()) {
      return null;
    }
    Map<Long, List<VersionType>> result = new HashMap<Long, List<VersionType>>();
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    StringBuilder sbEventIds = new StringBuilder();
    for (Long eventId : eventIds) {
      sbEventIds.append(sbEventIds.length() > 0 ? "," : "").append(eventId);
    }
    String sql = ReadInputStream.getResourceAsString(getClass(), "findVersionTypes.sql", "cp1250");
    sql = sql.replaceAll("<%eventIds%>", sbEventIds.toString());

    findVersionTypes = connection.prepareStatement(sql);
    ResultSet rs_findVersionTypes = findVersionTypes.executeQuery();
    while (rs_findVersionTypes.next()) {
      VersionType versionType = new VersionType(rs_findVersionTypes.getInt("IdSifranta"), rs_findVersionTypes.getString("IdSifre"), rs_findVersionTypes.getInt("Id"));
      Long eventId = rs_findVersionTypes.getLong("EventId");
      if (!result.containsKey(eventId)) {
        result.put(eventId, new ArrayList<VersionType>());
      }
      List<VersionType> type = result.get(eventId);
      if (!type.contains(versionType)) {
        type.add(versionType);
      }
    }

    return result;
  }

  @Override
  public Integer findVersion(Long eventId) throws SQLException {
    Integer result = null;
    if (eventId != null) {
      final Connection connection = ConnectionManager.getInstance().getTxConnection();
      if (findVersion == null) {
        findVersion = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_version.sql", "cp1250"));
      }
      synchronized (findVersion) {
        int param = 1;
        findVersion.clearParameters();
        findVersion.setLong(param++, eventId);
        ResultSet rs_findVersion = findVersion.executeQuery();
        try {
          if (rs_findVersion.next()) {
            result = rs_findVersion.getInt(1);
            if (rs_findVersion.wasNull()) {
              result = null;
            }
          }
        } finally {
          rs_findVersion.close();
        }
      }
    }

    return result;
  }

  @Override
  protected void updateVersion(int oldVersion, List<Long> eventIds, List<Long> oldEventIds) throws SQLException {
    int param;
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (getVersionedEventIds == null) {
      getVersionedEventIds = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "getVersionedEventIds.sql", "cp1250"));
    }
    if (insertUpdatedVersion == null) {
      insertUpdatedVersion = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insertUpdatedVersion.sql", "cp1250"));
    }
    long newVersionID;
    param = 1;
    insertUpdatedVersion.clearParameters();
    insertUpdatedVersion.setInt(param++, oldVersion);
    insertUpdatedVersion.executeUpdate();
    newVersionID = getLastIdentity();

    param = 1;
    getVersionedEventIds.clearParameters();
    getVersionedEventIds.setInt(param++, oldVersion);
    ResultSet storedVersionedEventIds = getVersionedEventIds.executeQuery();

    //shranim vse nespremenjene eventId-je
    while (storedVersionedEventIds.next()) {
      Long storedEventId = storedVersionedEventIds.getLong("EventId");
      if (!oldEventIds.contains(storedEventId)) {
        storeEventVersion(newVersionID, storedEventId);
      }
    }
    //shranim se vse nove EventId-je
    for (Long eventId : eventIds) {
      storeEventVersion(newVersionID, eventId);
    }
  }

  private Map<EventType, List<EventCacheTemporaryParameter>> getCachedEventObjects() throws SQLException {
    Map<EventType, List<EventCacheTemporaryParameter>> result = new HashMap<EventType, List<EventCacheTemporaryParameter>>();
    if (cachedEventObjects == null) {
      Statement statement = ConnectionManager.getInstance().getConnection().createStatement();
      try {
        Map<String, TemporaryTable> cachedTemporaryTables = getCachedTemporaryTables();
        TemporaryParametersFactory temporaryParametersFactory = new TemporaryParametersFactory();
        ResultSet rs = statement.executeQuery(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "getCachedEventObjects.sql", "cp1250"));
        while (rs.next()) {
          String object = rs.getString("Object");

          if (cachedTemporaryTables.containsKey(object)) {
            EventType key = new EventType(rs.getInt("IdSifranta"), rs.getString("IdSifre"));
            List<EventCacheTemporaryParameter> parameters;

            if (result.containsKey(key)) {
              parameters = result.get(key);
            } else {
              parameters = new ArrayList<EventCacheTemporaryParameter>();
              result.put(key, parameters);
            }

            EventCacheTemporaryParameter tt = (EventCacheTemporaryParameter) temporaryParametersFactory.createTemporaryTable(cachedTemporaryTables.get(object));
            parameters.add(tt);
          }
        }
      } finally {
        statement.close();
      }

      if (Boolean.parseBoolean(ConnectionManager.getInstance().getProperty(DbConnection.DB_CACHEFIELDS, "true"))) {
        this.cachedEventObjects = result;
      }
    } else {
      result = this.cachedEventObjects;
    }

    return result;
  }

  @Override
  protected void cacheEvent(Event event) throws SQLException {
    Map<EventType, List<EventCacheTemporaryParameter>> eventObjects = getCachedEventObjects();
    EventType key = new EventType(event);

    if (eventObjects.containsKey(key)) {
      if (DbDataSource.DUMP_SQL) {
        System.out.println("Caching:" + key);
      }
      final Connection txConnection = ConnectionManager.getInstance().getTxConnection();

      if (updateCachedEventObject == null) {
        updateCachedEventObject = txConnection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "updateCachedEventObjects.sql", "cp1250"));
      }

      synchronized (updateCachedEventObject) {
        updateCachedEventObject.setBoolean(1, false);
        updateCachedEventObject.setInt(2, key.getSifrant());
        updateCachedEventObject.setString(3, key.getSifra());
        updateCachedEventObject.executeUpdate();
      }

      if (event.getOperation().isUpdateCache()) {
        boolean cache = true;
        for (EventCacheTemporaryParameter tt : eventObjects.get(key)) {
          if (tt.getSqlMaterializedView().getCacheEventTypes().contains(key)) {
            for (EventType eventType : tt.getSqlMaterializedView().getCacheEventTypes()) {
              cache = cache && eventType.isCacheOnUpdate();
            }
          }
          if (cache) {
            tt.executeQuery(txConnection, new ArrayList<Object>());
          }
        }

        if (cache) {
          synchronized (updateCachedEventObject) {
            updateCachedEventObject.setBoolean(1, true);
            updateCachedEventObject.setInt(2, key.getSifrant());
            updateCachedEventObject.setString(3, key.getSifra());
            updateCachedEventObject.executeUpdate();
          }
        }
      }
    }
  }

  @Override
  public EventPK storeEvent(Event event, Event oldEvent) throws SQLException {
    EventPK result;
    EventPK oldEventPK = (oldEvent != null) ? oldEvent.getEventPK() : null;
    if ((oldEvent != null) && oldEvent.equalEventValues(event) && event.getOperation() == Event.EventOperation.UPDATE) {
      result = oldEvent.getEventPK();
    } else {
      final Connection connection = ConnectionManager.getInstance().getTxConnection();
      if (insertEvents == null) {
        insertEvents = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insertEvents.sql", "cp1250"));
//      insertEvents.setQueryTimeout(15);
      }
      if (updateEvents == null) {
        updateEvents = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "updateEvents.sql", "cp1250"));
//      updateEvents.setQueryTimeout(15);
      }
      if (findEventValue == null) {
        findEventValue = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_eventvalue.sql", "cp1250"));
      }
      if (insertEventValues == null) {
        insertEventValues = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insertEventValues.sql", "cp1250"));
//      insertEventValues.setQueryTimeout(15);
      }
      if (updateEventValues == null) {
        updateEventValues = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "updateEventValue.sql", "cp1250"));
//      updateEventValues.setQueryTimeout(15);
      }
      if (deleteEventValues == null) {
        deleteEventValues = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "deleteEventValue.sql", "cp1250"));
      }
      if (get_field == null) {
        get_field = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "get_field.sql", "cp1250"));
      }
      if (delete_eventPK == null) {
        delete_eventPK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "delete_eventPK.sql", "cp1250"));
      }
      if (delete_eventPKVersions == null) {
        delete_eventPKVersions = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "delete_eventPKVersions.sql", "cp1250"));
      }
      int param;
      boolean success = true;
      boolean commit = false;
      boolean isTransaction = isTransaction();
      // <editor-fold defaultstate="collapsed" desc="Shrani">

      connection.clearWarnings();
      Long events_ID = null;
      try {
        if (!isTransaction) {
          beginTransaction();
        }

        if (event.getOperation() == Event.EventOperation.UPDATE) {
          boolean insert = (event.getId() == null) || (event.getId() == -1) || event.isVersioned();

          if (insert) {
//        System.out.println("event:" + event.getSifrant() + "-" + event.getSifra() + ":inserting");

            if (event.isVersioned() && (oldEvent != null)) {
              //updataj stari event
              //oz.deletej valid = false
              synchronized (updateEvents) {
                param = 1;
                updateEvents.clearParameters();
                updateEvents.setInt(param++, oldEvent.getSifrant());
                updateEvents.setString(param++, oldEvent.getSifra());
                if (oldEvent.getEventSource() == Integer.MIN_VALUE) {
                  updateEvents.setNull(param++, java.sql.Types.INTEGER);
                } else {
                  updateEvents.setInt(param++, oldEvent.getEventSource());
                }
                updateEvents.setTimestamp(param++, new java.sql.Timestamp(oldEvent.getDatum().getTime()));
                updateEvents.setString(param++, oldEvent.getOpomba());
                updateEvents.setTimestamp(param++, new java.sql.Timestamp(System.currentTimeMillis()));
                updateEvents.setBoolean(param++, false);
                updateEvents.setTimestamp(param++, new Timestamp(System.currentTimeMillis()));
                updateEvents.setLong(param++, oldEvent.getId());

                success = success && updateEvents.executeUpdate() > 0;
              }

              synchronized (delete_eventPK) {
                param = 1;
                delete_eventPK.clearParameters();
                delete_eventPK.setLong(param++, oldEvent.getId());
                delete_eventPK.executeUpdate();
              }

              synchronized (delete_eventPKVersions) {
                param = 1;
                delete_eventPKVersions.clearParameters();
                delete_eventPKVersions.setLong(param++, oldEvent.getId());
                delete_eventPKVersions.executeUpdate();
              }
              //success = success && delete_eventPK.executeUpdate() > 0;
            }

            //insertaj event
            synchronized (insertEvents) {
              param = 1;
              insertEvents.clearParameters();
              insertEvents.setInt(param++, event.getSifrant());
              insertEvents.setString(param++, event.getSifra());
              if (event.getEventSource() == Integer.MIN_VALUE) {
                insertEvents.setNull(param++, java.sql.Types.INTEGER);
              } else {
                insertEvents.setInt(param++, event.getEventSource());
              }
              Date eventDatum = event.getDatum();
              if (eventDatum != null) {
                insertEvents.setTimestamp(param++, new java.sql.Timestamp(event.getDatum().getTime()));
              } else {
                insertEvents.setTimestamp(param++, new java.sql.Timestamp(System.currentTimeMillis()));
              }
              insertEvents.setString(param++, event.getOpomba());
              success = success && insertEvents.executeUpdate() > 0;

              if (success) {
                events_ID = getLastIdentity();
              } else {
                throw new SQLException("Neuspešno dodajanje dogodka!");
              }
            }


          } else {
            events_ID = event.getId();
//        System.out.println("event:" + event.getSifrant() + "-" + event.getSifra() + ":updating:" + events_ID);
            synchronized (updateEvents) {
              param = 1;
              updateEvents.clearParameters();
              updateEvents.setInt(param++, event.getSifrant());
              updateEvents.setString(param++, event.getSifra());
              if (event.getEventSource() == Integer.MIN_VALUE) {
                updateEvents.setNull(param++, java.sql.Types.INTEGER);
              } else {
                updateEvents.setInt(param++, event.getEventSource());
              }
              long time = System.currentTimeMillis();
              if (event != null && event.getDatum() != null) {
                time = event.getDatum().getTime();
              }
              updateEvents.setTimestamp(param++, new java.sql.Timestamp(time));
              updateEvents.setString(param++, event.getOpomba());
              updateEvents.setTimestamp(param++, new java.sql.Timestamp(System.currentTimeMillis()));
              updateEvents.setBoolean(param++, (event.getOperation() != null && event.getOperation() == Event.EventOperation.DELETE) ? false : true);
              updateEvents.setTimestamp(param++, (event.getOperation() != null && event.getOperation() == Event.EventOperation.DELETE) ? new Timestamp(System.currentTimeMillis()) : null);
              updateEvents.setLong(param++, events_ID);

              success = success && updateEvents.executeUpdate() > 0;
            }
          }

          if (success) {
            success = success && storeOpomba(events_ID, event.getOpomba());
          }

          if (success) {
            if (event.getVeljavnost() != null && event.getVeljavnost() > 0) {
              success = success && storeVeljavnost(events_ID, event.getVeljavnost());
            }
          }

          if (success) {
            List<FieldValue> fieldValuesList = new ArrayList<FieldValue>(event.getEventValues().size());

            EventPK eventPK = new EventPK();

            Map<Field, List<FieldValue>> eventValues = event.getEventValues();
            final Map<CaseInsensitiveString, Field> fields = event.getPreparedFields() == null ? this.getPreparedFields() : event.getPreparedFields();

            for (Field field : eventValues.keySet()) {
              List<FieldValue> fieldValues = eventValues.get(field);
              for (int i = 0; i < fieldValues.size(); i++) {
                FieldValue value = fieldValues.get(i);

                Long valueId = null;
                //ce ze imamo valueId, potem ne rabimo vec shranjevat,
                //ker se vrednosti in valueId ne spreminja
                if (value.getValueId() == null || value.getValueId() <= 0) {
                  valueId = storeValue(value.getValueType(), value.getValue());
                  value.setValueId(valueId);
                } else {
                  valueId = value.getValueId();
                }
                fieldValuesList.add(value);

                String fieldName = field.getName();
                String fieldNameWithIndex = field.getName();
                int fieldValueIndex = field.getFieldIndex();

                int field_id;
                if ((field.getIdPolja() == null)
                        || (field.getIdPolja() < 0)) {
                  if (fieldValueIndex > 1) {
                    Field nonIndexed = field.getNonIndexedField();
                    fieldName = nonIndexed.getName();
                  }
                  Field newField = Field.getField(fieldName, fieldValueIndex, fields);
                  if (newField.getIdPolja() == null) {
                    newField = getField(fieldName);
                  }
                  if (newField == null) {
                    throw new SQLException("Cannot find IDPolja! FieldName=" + fieldName);
                  }
                  field_id = newField.getIdPolja();
                  field.setIdPolja(newField.getIdPolja());
                } else {
                  field_id = field.getIdPolja();
                }

                if (valueId != null) {
                  if (field.getLookupType() == null) {

                    synchronized (findEventValue) {
                      param = 1;
                      findEventValue.clearParameters();
                      findEventValue.setLong(param++, events_ID);
                      findEventValue.setInt(param++, field_id);
                      findEventValue.setInt(param++, fieldValueIndex);  //indexPolja

                      ResultSet rs = findEventValue.executeQuery();
                      try {
                        rs.next();

                        if (rs.getInt(1) == 0) {
                          //insertaj event value
                          param = 1;
                          insertEventValues.clearParameters();
                          insertEventValues.setLong(param++, events_ID);
                          insertEventValues.setInt(param++, field_id);
                          insertEventValues.setInt(param++, fieldValueIndex);  //indexPolja
                          if (valueId == null) {
                            insertEventValues.setNull(param++, java.sql.Types.BIGINT);
                          } else {
                            insertEventValues.setLong(param++, valueId);
                          }

                          success = success && insertEventValues.executeUpdate() > 0;
                        } else {
                          //updataj event value
                          param = 1;
                          updateEventValues.clearParameters();
                          if (valueId == null) {
                            updateEventValues.setNull(param++, java.sql.Types.BIGINT);
                          } else {
                            updateEventValues.setLong(param++, valueId);
                          }
                          updateEventValues.setLong(param++, events_ID);
                          updateEventValues.setInt(param++, field_id);
                          updateEventValues.setInt(param++, fieldValueIndex);  //indexPolja

                          success = success && updateEventValues.executeUpdate() > 0;
                        }
                      } finally {
                        rs.close();
                      }

                    }
                  }

                  if (event.getPrimaryKey() != null && event.getPrimaryKey().length > 0) {
                    FieldValue fieldValuePK = new FieldValue(field_id, fieldNameWithIndex, field.getType(), fieldValueIndex, new VariousValue(valueId, value.getType(), value.getValue()));
                    for (Field field1 : event.getPrimaryKey()) {
                      if (field1.equals(fieldValuePK)) {
                        eventPK.addPrimaryKeyField(fieldValuePK);
                        break;
                      }
                    }
                  }
                } else {
                  if (!event.isVersioned()) {
                    //delete eventValue
                    synchronized (deleteEventValues) {
                      param = 1;
                      deleteEventValues.clearParameters();
                      deleteEventValues.setLong(param++, events_ID);
                      deleteEventValues.setInt(param++, field_id);
                      deleteEventValues.setInt(param++, fieldValueIndex);
                      deleteEventValues.executeUpdate();
                    }
                  }
                }
              }
            }
            if (event.getPrimaryKey() != null && event.getPrimaryKey().length > 0) {
              eventPK.setEventId(events_ID);
              eventPK.setIdSifranta(event.getSifrant());
              eventPK.setIdSifre(event.getSifra());
              eventPK.setEventOperation(event.getOperation());
              success = success && storePrimaryKey(eventPK);
            }

            success = success && storeEventLookUpKeys(events_ID, fieldValuesList);
          }

          commit = success;
        } else if (event.getOperation() == Event.EventOperation.DELETE) {
          if ((event.getId() != null) && (event.getId() > 0)) {
            events_ID = event.getId();
            //updataj stari event
            //oz.deletej valid = false
            synchronized (updateEvents) {
              param = 1;
              updateEvents.clearParameters();
              updateEvents.setInt(param++, event.getSifrant());
              updateEvents.setString(param++, event.getSifra());
              if (oldEvent.getEventSource() == Integer.MIN_VALUE) {
                updateEvents.setNull(param++, java.sql.Types.INTEGER);
              } else {
                updateEvents.setInt(param++, event.getEventSource());
              }
              updateEvents.setTimestamp(param++, new java.sql.Timestamp(oldEvent.getDatum().getTime()));
              updateEvents.setString(param++, event.getOpomba());
              updateEvents.setTimestamp(param++, new java.sql.Timestamp(System.currentTimeMillis()));
              updateEvents.setBoolean(param++, false);
              updateEvents.setTimestamp(param++, new Timestamp(System.currentTimeMillis()));
              updateEvents.setLong(param++, event.getId());

              success = success && updateEvents.executeUpdate() > 0;
            }

            synchronized (delete_eventPK) {
              //
              param = 1;
              delete_eventPK.clearParameters();
              delete_eventPK.setLong(param++, event.getId());
              delete_eventPK.executeUpdate();
              //success = success && delete_eventPK.executeUpdate() > 0;
            }

            synchronized (delete_eventPKVersions) {
              //
              param = 1;
              delete_eventPKVersions.clearParameters();
              delete_eventPKVersions.setLong(param++, event.getId());
              delete_eventPKVersions.executeUpdate();
              //success = success && delete_eventPK.executeUpdate() > 0;
            }

            commit = success;
          }
        } else {
          events_ID = event.getId();
        }
      } finally {
        if (!isTransaction) {
          endTransaction(commit);
        }
        event.setId(events_ID);
      }

      result = event.getEventPK();
    }

    if (oldEventPK != null) {
      result.setOldEventId(oldEventPK.getEventId());
      result.setVersioned(oldEventPK.isVersioned());
    }
    return result;
  }

  @Override
  public boolean storeEventLookUpKeys(Long eventId, List<FieldValue> fieldValues) throws SQLException {
    if (insert_eventLookupKeys == null) {
      insert_eventLookupKeys = ConnectionManager.getInstance().getTxConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "insert_eventLookupKeys.sql", "cp1250"));
    }
    if (update_eventLookupKeys == null) {
      update_eventLookupKeys = ConnectionManager.getInstance().getTxConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "update_eventLookupKeys.sql", "cp1250"));
    }
    Map<Integer, List<FieldValue>> lookupFields = new HashMap<Integer, List<FieldValue>>();
    for (FieldValue fieldValue : fieldValues) {
      if (fieldValue.getLookupType() != null) {
        Integer idPolja = fieldValue.getIdPolja();
        if (!lookupFields.containsKey(idPolja)) {
          lookupFields.put(idPolja, new ArrayList<FieldValue>());
        }
        lookupFields.get(idPolja).add(fieldValue);
      }
    }
    boolean success = true;
    int numberOfValues = 0;

    Integer versionId = null;
    int fieldValueIndex = -1;
    Integer idSifranta = null;
    String idSifre = null;
    String primaryKey = null;
    for (Integer lookupIdPolja : lookupFields.keySet()) {
      numberOfValues = 0;
      for (FieldValue fieldValue : lookupFields.get(lookupIdPolja)) {
        fieldValueIndex = fieldValue.getFieldIndex();

        Object value = fieldValue.getValue();
        if (fieldValue.getValue() instanceof VariousValue) {
          value = ((VariousValue) value).getValue();
        }
        switch (fieldValue.getLookupType()) {
          case VERSION_ID:
            versionId = (Integer) value;
            numberOfValues++;
            break;
          case ID_SIFRANTA:
            idSifranta = (Integer) value;
            numberOfValues++;
            break;
          case ID_SIFRE:
            idSifre = (String) value;
            if (idSifre != null) {
              numberOfValues++;
            }
            break;
          case PRIMARY_KEY:
            primaryKey = (String) value;
            numberOfValues++;
            break;
        }
      }
      //vse vrednosti za lookup morajo biti izpolnjene
      if (Field.LookupType.values().length == numberOfValues) {
        if (findLookupKeys(lookupIdPolja, fieldValueIndex, eventId)) {
          synchronized (update_eventLookupKeys) {
            int param = 1;
            update_eventLookupKeys.clearParameters();

            if (versionId != null) {
              update_eventLookupKeys.setInt(param++, versionId);
            } else {
              update_eventLookupKeys.setNull(param++, java.sql.Types.INTEGER);
            }
            update_eventLookupKeys.setInt(param++, idSifranta);
            update_eventLookupKeys.setString(param++, idSifre);
            update_eventLookupKeys.setString(param++, primaryKey);

            update_eventLookupKeys.setLong(param++, eventId);
            update_eventLookupKeys.setInt(param++, lookupIdPolja);
            update_eventLookupKeys.setInt(param++, fieldValueIndex);

            success = success && update_eventLookupKeys.executeUpdate() > 0;
          }
        } else {
          //insert
          synchronized (insert_eventLookupKeys) {
            int param = 1;
            insert_eventLookupKeys.clearParameters();
            insert_eventLookupKeys.setLong(param++, eventId);
            insert_eventLookupKeys.setInt(param++, lookupIdPolja);
            insert_eventLookupKeys.setInt(param++, fieldValueIndex);
            if (versionId != null) {
              insert_eventLookupKeys.setInt(param++, versionId);
            } else {
              insert_eventLookupKeys.setNull(param++, java.sql.Types.INTEGER);
            }
            insert_eventLookupKeys.setInt(param++, idSifranta);
            insert_eventLookupKeys.setString(param++, idSifre);
            insert_eventLookupKeys.setString(param++, primaryKey);

            Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.WARNING, "Inserting lookupKeys: {0} , {1} , {2} , {3} , {4} , {5} , ", new Object[]{lookupIdPolja, fieldValueIndex, versionId, idSifranta, idSifre, primaryKey});

            success = success && insert_eventLookupKeys.executeUpdate() > 0;
          }
        }
      } else {
        if (numberOfValues > 0) {
          // throw new SQLNotificationException("Napaka pri shranjevanju lookup polj! Niso vsa polja izpolnjena");
        }
      }
    }
    return success;
  }

  public boolean findLookupKeys(int idPolja, int fieldValueIndex, Long eventId) throws SQLException {
    boolean result = false;
    if (find_eventLookupKeys == null) {
      find_eventLookupKeys = ConnectionManager.getInstance().getTxConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "find_eventLookupKeys.sql", "cp1250"));
    }
    synchronized (find_eventLookupKeys) {
      int param = 1;
      find_eventLookupKeys.clearParameters();
      find_eventLookupKeys.setLong(param++, eventId);
      find_eventLookupKeys.setInt(param++, idPolja);
      find_eventLookupKeys.setInt(param++, fieldValueIndex);
      ResultSet rs_find_eventLookupKeys = find_eventLookupKeys.executeQuery();
      try {
        if (rs_find_eventLookupKeys.next()) {
          result = true;
        }
      } finally {
        rs_find_eventLookupKeys.close();
      }
    }
    return result;
  }

  @Override
  public VariousValue findValue(long valueId) throws SQLException {
    VariousValue result = null;
    if (findValue == null) {
      findValue = ConnectionManager.getInstance().getTxConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "findValue.sql", "cp1250"));
    }
    synchronized (findValue) {
      int param = 1;
      findValue.clearParameters();
      findValue.setLong(param++, valueId);
      ResultSet rs_findValue = findValue.executeQuery();
      try {
        if (rs_findValue.next()) {
          int fieldType = rs_findValue.getInt("FieldType");
          ValueType type = ValueType.valueOf(fieldType);
          switch (type) {
            case IntValue:
              int intValue = rs_findValue.getInt("IntValue");
              result = new VariousValue(valueId, type.getTypeIndex(), new Integer(intValue));
              break;
            case RealValue:
              double realValue = rs_findValue.getDouble("RealValue");
              result = new VariousValue(valueId, type.getTypeIndex(), new Double(realValue));
              break;
            case StringValue:
              String stringValue = rs_findValue.getString("StringValue");
              result = new VariousValue(valueId, type.getTypeIndex(), stringValue);
              break;
            case DateValue:
              Date dateValue = rs_findValue.getDate("DateValue");
              result = new VariousValue(valueId, type.getTypeIndex(), dateValue);
              break;
            case LongValue:
              Long longValue = rs_findValue.getLong("IntValue");
              result = new VariousValue(valueId, type.getTypeIndex(), longValue);
              break;
            //TODO
            //object, clob, cclob
          }
        }
      } finally {
        rs_findValue.close();
      }
    }
    return result;
  }

  @Override
  public Long storeValue(ValueType fieldType, final Object value) throws SQLException {
    if (callStoredValue == null) {
      callStoredValue = ConnectionManager.getInstance().getTxConnection().prepareCall(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "callStoreValue.sql", "cp1250"));
      callStoredValue.setQueryTimeout(15);
    }

    int pos = 0;
    FieldValue[] fieldValues = new FieldValue[7];
    fieldValues[pos++] = new FieldValue("FieldType", Types.INTEGER, new Integer(fieldType.getTypeIndex()));
    Long newValueId = null;
    if (value != null) {
      switch (fieldType) {
        case BitValue:
        case IntValue:
        case LongValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, value);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.VARCHAR, null);
          break;
        case RealValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, value);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.VARCHAR, null);
          break;
        case StringValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, value);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.VARCHAR, null);
          break;
        case DateTimeValue:
        case MonthValue:
        case TimeValue:
        case DateValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, value);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.VARCHAR, null);
          break;
        case ObjectValue:
        case BlobValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, value);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.VARCHAR, null);
          break;
        case ClobValue:
          fieldValues[pos++] = new FieldValue("IntValue", Types.BIGINT, null);
          fieldValues[pos++] = new FieldValue("RealValue", Types.DECIMAL, null);
          fieldValues[pos++] = new FieldValue("StringValue", Types.VARCHAR, null);
          fieldValues[pos++] = new FieldValue("DateValue", Types.TIMESTAMP, null);
          fieldValues[pos++] = new FieldValue("ObjectValue", Types.LONGVARBINARY, null);
          fieldValues[pos++] = new FieldValue("ClobValue", Types.VARCHAR, value);
          break;
      }
      if (newValueId == null) {
        synchronized (callStoredValue) {
          execute(callStoredValue, fieldValues);

          ResultSet resultSet = callStoredValue.getResultSet();
          if (resultSet != null) {
            try {
              if (resultSet.next()) {
                newValueId = resultSet.getLong("Id");
              }
            } finally {
              resultSet.close();
            }
          }
        }
      }
    }
    return newValueId;
  }

  public boolean storeOpomba(Long eventId, String opomba) throws SQLException {
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (insertEventsOpombe == null) {
      insertEventsOpombe = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insert_EventsOpombe.sql", "cp1250"));
    }
    if (findOpomba == null) {
      findOpomba = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_opomba.sql", "cp1250"));
    }

    int param = 1;
    boolean success = true;
    boolean saveOpomba = true;

    synchronized (findOpomba) {
      param = 1;
      findOpomba.clearParameters();
      findOpomba.setLong(param++, eventId);
      ResultSet rsFindOpomba = findOpomba.executeQuery();
      try {
        if (rsFindOpomba.next()) {
          Clob oldOpombaClob = rsFindOpomba.getClob("ClobValue");
          if (oldOpombaClob != null) {
            String oldOpomba = oldOpombaClob.getSubString(1L, (int) oldOpombaClob.length());
            if (oldOpomba.equals(opomba)) {
              //opomba se ni spemenila
              saveOpomba = false;
            } else {
              //update event in opomba se je spremenila
            }
          }
        }
      } finally {
        rsFindOpomba.close();
      }

      if (saveOpomba) {
        if (opomba == null) {
          return true;
        }
        Long opombaId = storeValue(ValueType.ClobValue, opomba);
        synchronized (insertEventsOpombe) {
          param = 1;
          insertEventsOpombe.clearParameters();
          if (eventId == null) {
            return false;
          } else {
            insertEventsOpombe.setLong(param++, eventId.longValue());
          }
          if (opombaId == null) {
            //null ni dovoljeno, zato bo vrgel sql napako
            return false;
          } else {
            insertEventsOpombe.setLong(param++, opombaId.longValue());
          }
          success = success && insertEventsOpombe.executeUpdate() > 0;
        }
      }
    }
    return success;
  }

  @Override
  public DbDataSource getDsSifrantModel(String dataBase, List<Object> parameters) throws SQLException {
    DbDataSource dsSifrant = new DbDataSource();

    dsSifrant.setCanAddRows(false);
    dsSifrant.setCanDeleteRows(false);
    dsSifrant.setReadOnly(true);

    java.util.List params = new java.util.ArrayList();
    params.add(parameters.get(0));

    DbDataSource.SubstSqlParameter tb_sifranti = new DbDataSource.SubstSqlParameter("<%tb_sifranti%>");
    if ((dataBase == null) || (dataBase.length() == 0)) {
      tb_sifranti.setValue("");
    } else {
      tb_sifranti.setValue(dataBase + ".[Sifranti] AS ");
    }
    params.add(tb_sifranti);
    DbDataSource.SubstSqlParameter tb_seznam_sifrantov = new DbDataSource.SubstSqlParameter("<%tb_seznam_sifrantov%>");
    if ((dataBase == null) || (dataBase.length() == 0)) {
      tb_seznam_sifrantov.setValue("");
    } else {
      tb_seznam_sifrantov.setValue(dataBase + ".[SeznamSifrantov] AS ");
    }
    params.add(tb_seznam_sifrantov);
    for (int i = 1; i < parameters.size(); i++) {
      params.add(parameters.get(i));
    }

    dsSifrant.setParameters(params);
    dsSifrant.setCountSql(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "sifrant_c.sql", "cp1250"));
    dsSifrant.setSelectSql(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "sifrant.sql", "cp1250"));
    dsSifrant.setQueuedDelay(0);

    return dsSifrant;
  }

  @Override
  public DbNaslovDataModel.Naslov storeAddress(DbNaslovDataModel.Naslov address) throws SQLException {

    SqlUtilities sqlUtility = SqlUtilities.getInstance();
    Connection connection = ConnectionManager.getInstance().getTxConnection();
    int param;

    if (address.getHsMID() == null || address.getHsMID().getValue() == null) {
      param = 1;
      if (findHsNeznanaId == null) {
        findHsNeznanaId = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "findHsNeznanaId.sql", "cp1250"));
      }

      ResultSet rsFindHsNeznanaId = executeQuery(findHsNeznanaId,
              address.getPostnaStevilka(),
              address.getPosta(),
              address.getNaselje(),
              address.getUlica(),
              address.getHisnaStevilka(),
              address.getHisnaStevilkaDodatek());

      Long hs_neznana_id = null;

      if (rsFindHsNeznanaId.next()) {
        hs_neznana_id = rsFindHsNeznanaId.getLong(1);
        if (rsFindHsNeznanaId.wasNull()) {
          hs_neznana_id = null;
        }
      } else {
        if (insertNeznaniNaslov == null) {
          insertNeznaniNaslov = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insertNeznaniNaslov.sql", "cp1250"));
        }
        boolean commit = false;
        boolean isTransaction = isTransaction();
        if (!isTransaction) {
          sqlUtility.beginTransaction();
        }
        try {
          synchronized (insertNeznaniNaslov) {
            param = 1;
            insertNeznaniNaslov.clearParameters();


            insertNeznaniNaslov.setObject(param++, address.getPostnaStevilkaMID().getValue(), java.sql.Types.INTEGER);
            insertNeznaniNaslov.setObject(param++, address.getPostnaStevilka().getValue(), java.sql.Types.INTEGER);
            insertNeznaniNaslov.setObject(param++, address.getPosta().getValue().toString().toUpperCase(), java.sql.Types.VARCHAR); //pt_ime
            insertNeznaniNaslov.setObject(param++, address.getPosta().getValue(), java.sql.Types.VARCHAR);  //pt_uime

            insertNeznaniNaslov.setObject(param++, address.getNaseljeMID().getValue(), java.sql.Types.INTEGER);
            if ((address.getNaselje() == null) || (address.getNaselje().getValue() == null)) {
              insertNeznaniNaslov.setNull(param++, java.sql.Types.VARCHAR);
              insertNeznaniNaslov.setNull(param++, java.sql.Types.VARCHAR);
            } else {
              insertNeznaniNaslov.setObject(param++, address.getNaselje().getValue().toString().toUpperCase(), java.sql.Types.VARCHAR); //na_ime
              insertNeznaniNaslov.setObject(param++, address.getNaselje().getValue(), java.sql.Types.VARCHAR); //na_uime
            }

            insertNeznaniNaslov.setObject(param++, address.getUlicaMID().getValue(), java.sql.Types.INTEGER);
            insertNeznaniNaslov.setObject(param++, address.getUlica().getValue().toString().toUpperCase(), java.sql.Types.VARCHAR); //ul_ime
            insertNeznaniNaslov.setObject(param++, address.getUlica().getValue(), java.sql.Types.VARCHAR); //ul_uime

            if (address.getHisnaStevilka() == null) {
              insertNeznaniNaslov.setNull(param++, java.sql.Types.INTEGER);
            } else {
              insertNeznaniNaslov.setObject(param++, address.getHisnaStevilka().getValue(), java.sql.Types.INTEGER); //ul_uime
            }
            if (address.getHisnaStevilkaDodatek() == null) {
              insertNeznaniNaslov.setNull(param++, java.sql.Types.VARCHAR);
            } else {
              insertNeznaniNaslov.setObject(param++, address.getHisnaStevilkaDodatek().getValue(), java.sql.Types.VARCHAR); //ul_uime
            }
            insertNeznaniNaslov.setInt(param++, address.getIzvor());

            commit = insertNeznaniNaslov.executeUpdate() > 0;
          }
        } finally {
          if (commit) {
            if (isTransaction) {
              sqlUtility.endTransaction(commit);
            }

            hs_neznana_id = sqlUtility.getLastIdentity();
          } else {
            JOptionPane.showMessageDialog(null, "Napaka pri shranjevanju neznanega naslova!", "Napaka", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
      address.setHsNeznanaMID(hs_neznana_id);
    }

    return address;
  }

  public Event findEvent(Long eventId) throws SQLException {
    if (findEventById == null) {
      findEventById = ConnectionManager.getInstance().getTxConnection().prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_event_by_id.sql", "cp1250"));
    }
    synchronized (findEventById) {
      ResultSet rs = executeQuery(findEventById, new FieldValue("ID", java.sql.Types.BIGINT, eventId));
      try {
        if (rs.next()) {
          Event result = new Event(rs.getInt("IdSifranta"),
                  rs.getString("IdSifre"));
          List<Field> primaryKey = new ArrayList<Field>();
          result.setId(eventId);
          result.setEventSource(rs.getInt("IdEventSource"));
          result.setDatum(rs.getTimestamp("Datum"));
          java.sql.Clob opomba = rs.getClob("Opomba");
          if (!rs.wasNull()) {
            if (opomba.length() > 0) {
              result.setOpomba(opomba.getSubString(1L, (int) opomba.length()));
            } else {
              result.setOpomba(null);
            }
          }
          do {
            FieldValue fv = null;
            switch (rs.getInt("FieldType")) {
              case 1:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.INTEGER, rs.getInt("FieldValueIndex"), rs.getInt("IntValue"), rs.getLong("ValueId")));
                break;
              case 2:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.DOUBLE, rs.getInt("FieldValueIndex"), rs.getDouble("RealValue"), rs.getLong("ValueId")));
                break;
              case 3:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.VARCHAR, rs.getInt("FieldValueIndex"), rs.getString("StringValue"), rs.getLong("ValueId")));
                break;
              case 4:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.TIMESTAMP, rs.getInt("FieldValueIndex"), rs.getTimestamp("DateValue"), rs.getLong("ValueId")));
                break;
              case 5:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.BLOB, rs.getInt("FieldValueIndex"), rs.getBlob("ObjectValue"), rs.getLong("ValueId")));
                break;
              case 6:
                java.sql.Clob value = rs.getClob("ClobValue");
                if ((value != null) && (value.length() > 0)) {
                  result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.VARCHAR, rs.getInt("FieldValueIndex"), value.getSubString(1L, (int) value.length()), rs.getLong("ValueId")));
                } else {
                  result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.VARCHAR, rs.getInt("FieldValueIndex"), "", rs.getLong("ValueId")));
                }
                break;
              case 7:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.BOOLEAN, rs.getInt("FieldValueIndex"), rs.getInt("IntValue") != 0, rs.getLong("ValueId")));
                break;
              case 8:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.TIMESTAMP, rs.getInt("FieldValueIndex"), rs.getTimestamp("DateValue"), rs.getLong("ValueId")));
                break;
              case 9:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.TIME, rs.getInt("FieldValueIndex"), rs.getTime("DateValue"), rs.getLong("ValueId")));
                break;
              case 10:
                result.addValue(fv = new FieldValue(rs.getInt("IdPolja"), rs.getString("ImePolja"), java.sql.Types.DATE, rs.getInt("FieldValueIndex"), rs.getDate("DateValue"), rs.getLong("ValueId")));
                break;
            }
            if (fv != null && rs.getBoolean("PrimaryKey")) {
              primaryKey.add(new Field(fv));
            }
          } while (rs.next());
          if (primaryKey.size() > 0) {
            result.setPrimaryKey(primaryKey.toArray(new Field[primaryKey.size()]));
          }
          return result;
        } else {
          return null;
        }
      } finally {
        rs.close();
      }
    }

  }

  @Override
  public MaterializedView getCacheDefinition(String table) {
    MaterializedView result = new MaterializedView();
    result.setValue("[MViewCache].[dbo].[" + table + "]");
    result.setIsViewValidSql(com.openitech.text.Document.identText(
            "\nSELECT [MViewCache].[DBO].[isValidCachedObject]\n"
            + "    ('" + table + "'\n"
            + "     ,NULL\n"
            + "     ,NULL)", 15));
    result.setSetViewVersionSql("EXECUTE [MViewCache].[dbo].[updateRefreshDate] '" + table + "'");
    return result;
  }

  @Override
  public MaterializedView getCacheDefinition(String table, int idSifranta, String idSifre) {
    MaterializedView result = new MaterializedView();
    result.setValue("[MViewCache].[dbo].[" + table + "]");
    result.setIsViewValidSql(com.openitech.text.Document.identText(
            "\nEXECUTE [MViewCache].[dbo].[isValidCachedEvent]"
            + "\n         N'[MViewCache].[dbo].[" + table + "]'"
            + "\n        ," + idSifranta
            + "\n        ,'" + idSifre + "'", 15));
//    result.setIsViewValidSql(com.openitech.text.Document.identText(
//              "\nSELECT CAST(CASE WHEN count(*) = 0 THEN 1 ELSE 0 END AS BIT) AS Valid FROM " + changeLog + ".[dbo].[Events] ev WITH (NOLOCK) "
//            + "\n    WHERE ev.[IdSifranta] = " + idSifranta + " AND"
//            + "\n          ev.[IdSifre] = '" + idSifre + "' AND"
//            + "\n          ev.valid = 1 AND"
//            + "\n          NOT EXISTS (SELECT Id FROM [MViewCache].[dbo].[" + table + "] cached WITH (NOLOCK) WHERE cached.Id = ev.Id AND cached.Version=ev.Version );"
//            + "\n"
//            + "\nSELECT CAST(CASE WHEN count(*) = 0 THEN 1 ELSE 0 END AS BIT) AS Valid FROM [MViewCache].[dbo].[" + table + "] WITH (NOLOCK) "
//            + "\n    WHERE [Version] NOT IN (SELECT ev.Version FROM " + changeLog + ".[dbo].[Events] ev WITH (NOLOCK) WHERE ev.[IdSifranta] = " + idSifranta + " AND ev.[IdSifre] = '" + idSifre + "' AND ev.valid = 1)"
//            + "\n", 15));
    result.setSetViewVersionSql("EXECUTE [MViewCache].[dbo].[updateRefreshDate] '[MViewCache].[dbo].[" + table + "]'");
    result.setCacheEvents(new MaterializedView.CacheEvents());
    MaterializedView.CacheEvents.Event event = new MaterializedView.CacheEvents.Event();
    event.setSifra(idSifre);
    event.setSifrant(idSifranta);
    event.setCacheOnUpdate(false);

    result.getCacheEvents().getEvent().add(event);

    return result;
  }

  @Override
  public Map<String, TemporaryTable> getCachedTemporaryTables() {
    Map<String, TemporaryTable> result = new HashMap<String, TemporaryTable>();
    try {
      Statement statement = ConnectionManager.getInstance().getConnection().createStatement();
      try {
        ResultSet cachedObjects = statement.executeQuery(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "getCachedTemporaryTables.sql", "cp1250"));

        while (cachedObjects.next()) {
          if (cachedObjects.getObject("CachedObjectXML") != null) {
            String object = cachedObjects.getString("Object");
            com.openitech.db.model.xml.config.CachedTemporaryTable temporaryTable;

            try {
              Unmarshaller unmarshaller = JAXBContext.newInstance(com.openitech.db.model.xml.config.CachedTemporaryTable.class).createUnmarshaller();
              temporaryTable = (com.openitech.db.model.xml.config.CachedTemporaryTable) unmarshaller.unmarshal(cachedObjects.getClob("CachedObjectXML").getCharacterStream());

              result.put(object, temporaryTable.getTemporaryTable());
            } catch (JAXBException ex) {
              Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.WARNING, ex.getMessage());
            }
          }
        }
      } finally {
        statement.close();
      }
    } catch (SQLException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    }

    return result;
  }

  @Override
  public void storeCachedTemporaryTable(TemporaryTable tt) {
    if (tt.getMaterializedView() != null) {
      try {
        JAXBContext context = JAXBContext.newInstance(com.openitech.db.model.xml.config.CachedTemporaryTable.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();

        com.openitech.db.model.xml.config.CachedTemporaryTable ctt = new com.openitech.db.model.xml.config.CachedTemporaryTable();
        ctt.setTemporaryTable(tt);

        marshaller.marshal(ctt, sw);
        final Connection connection = ConnectionManager.getInstance().getTxConnection();
        if (storeCachedTemporaryTable == null) {
          storeCachedTemporaryTable = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "storeCachedTemporaryTable.sql", "cp1250"));
        }

        if (override) {
          if (deleteCachedTemporaryTable == null) {
            deleteCachedTemporaryTable = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "deleteCachedTemporaryTable.sql", "cp1250"));
          }
          synchronized (deleteCachedTemporaryTable) {
            deleteCachedTemporaryTable.setString(1, tt.getMaterializedView().getValue());
            deleteCachedTemporaryTable.executeUpdate();
          }
        }

        synchronized (storeCachedTemporaryTable) {
          storeCachedTemporaryTable.setString(1, tt.getMaterializedView().getValue());
          storeCachedTemporaryTable.setString(2, sw.toString());
          storeCachedTemporaryTable.executeUpdate();
        }

        if (tt.getMaterializedView().getCacheEvents() != null) {
          for (MaterializedView.CacheEvents.Event event : tt.getMaterializedView().getCacheEvents().getEvent()) {
            if (deleteCachedEventObject == null) {
              deleteCachedEventObject = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "deleteCachedEventObject.sql", "cp1250"));
            }

            synchronized (deleteCachedEventObject) {
              deleteCachedEventObject.setInt(1, event.getSifrant());
              deleteCachedEventObject.setString(2, event.getSifra());
              deleteCachedEventObject.setString(3, tt.getMaterializedView().getValue());
              deleteCachedEventObject.executeUpdate();
            }

            if (storeCachedEventObject == null) {
              storeCachedEventObject = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "storeCachedEventObject.sql", "cp1250"));
            }

            synchronized (storeCachedEventObject) {
              storeCachedEventObject.setInt(1, event.getSifrant());
              storeCachedEventObject.setString(2, event.getSifra());
              storeCachedEventObject.setString(3, tt.getMaterializedView().getValue());
              storeCachedEventObject.setBoolean(4, event.isCacheOnUpdate());
              storeCachedEventObject.executeUpdate();
            }
          }
        }
      } catch (SQLException ex) {
        Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
      } catch (JAXBException ex) {
        Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
      }

    }
  }

  @Override
  public boolean storePrimaryKey(EventPK eventPK) throws SQLException {
    boolean success = false;
    int param = 1;

    final long eventId = eventPK.getEventId();
    final int idSifranta = eventPK.getIdSifranta();
    final String idSifre = eventPK.getIdSifre();
    final String primaryKey = eventPK.toHexString();
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (insert_eventPK == null) {
      insert_eventPK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insert_eventPK.sql", "cp1250"));
    }
    if (update_eventPK == null) {
      update_eventPK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "update_eventPK.sql", "cp1250"));
    }
    if (delete_eventPK == null) {
      delete_eventPK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "delete_eventPK.sql", "cp1250"));
    }


    if (findEventPK(eventId) != null) {
      if (eventPK.getEventOperation().equals(Event.EventOperation.DELETE)) {
        synchronized (delete_eventPK) {
          param = 1;
          delete_eventPK.clearParameters();
          delete_eventPK.setLong(param++, eventId);
          success = delete_eventPK.executeUpdate() > 0;
        }
      } else {
        //insert
        synchronized (update_eventPK) {
          param = 1;
          update_eventPK.clearParameters();
          update_eventPK.setInt(param++, idSifranta);
          update_eventPK.setString(param++, idSifre);
          update_eventPK.setString(param++, primaryKey);
          update_eventPK.setLong(param++, eventId);

          success = update_eventPK.executeUpdate() > 0;
        }
      }
    } else {
      //insert
      synchronized (insert_eventPK) {
        param = 1;
        insert_eventPK.clearParameters();
        insert_eventPK.setLong(param++, eventId);
        insert_eventPK.setInt(param++, idSifranta);
        insert_eventPK.setString(param++, idSifre);
        insert_eventPK.setString(param++, primaryKey);
        success = insert_eventPK.executeUpdate() > 0;
      }
    }

    return success;
  }

  @Override
  public boolean storePrimaryKeyVersions(EventPK eventPK) throws SQLException {

    boolean success = true;
    int param = 1;

    final long eventId = eventPK.getEventId();
    final int idSifranta = eventPK.getIdSifranta();
    final String idSifre = eventPK.getIdSifre();
    final String primaryKey = eventPK.toHexString();
    final Integer versionId = eventPK.getVersionID();

    final Connection connection = ConnectionManager.getInstance().getTxConnection();

    if (primaryKey != null && primaryKey.length() > 0) {
      ///////////versions
      if (insert_eventPK_versions == null) {
        insert_eventPK_versions = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "insert_eventPK_versions.sql", "cp1250"));
      }

      if (update_eventPK_versions == null) {
        update_eventPK_versions = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "update_eventPK_versions.sql", "cp1250"));
      }

      if (update_eventPK_versions_byValue == null) {
        update_eventPK_versions_byValue = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "update_eventPK_versions_byValues.sql", "cp1250"));
      }
      EventPK findEventPKVersions;

      if (versionId == null) {
        findEventPKVersions = findEventPKVersions(idSifranta, idSifre, versionId, primaryKey);
      } else {
        findEventPKVersions = findEventPKVersions(eventId, versionId);
      }

      if (findEventPKVersions != null) {
        if (versionId == null) {
          synchronized (update_eventPK_versions_byValue) {
            param = 1;
            update_eventPK_versions_byValue.clearParameters();

            update_eventPK_versions_byValue.setLong(param++, eventId);
            update_eventPK_versions_byValue.setInt(param++, 1);
            update_eventPK_versions_byValue.setNull(param++, java.sql.Types.INTEGER);
            update_eventPK_versions_byValue.setInt(param++, 0);
            update_eventPK_versions_byValue.setInt(param++, idSifranta);
            update_eventPK_versions_byValue.setString(param++, idSifre);
            update_eventPK_versions_byValue.setString(param++, primaryKey);

            success = success && update_eventPK_versions_byValue.executeUpdate() > 0;
          }
        } else {
          synchronized (update_eventPK_versions) {
            param = 1;
            update_eventPK_versions.clearParameters();

            update_eventPK_versions.setInt(param++, idSifranta);
            update_eventPK_versions.setString(param++, idSifre);
            update_eventPK_versions.setString(param++, primaryKey);
            update_eventPK_versions.setLong(param++, eventId);
            if (versionId == null) {
              update_eventPK_versions.setInt(param++, -1);
              update_eventPK_versions.setInt(param++, 1);

            } else {
              update_eventPK_versions.setInt(param++, versionId.intValue());
              update_eventPK_versions.setInt(param++, 0);
            }
            success = success && update_eventPK_versions.executeUpdate() > 0;
          }
        }
      } else {
        synchronized (insert_eventPK_versions) {
          //insert
          param = 1;

          insert_eventPK_versions.clearParameters();
          insert_eventPK_versions.setLong(param++, eventId);
          if (versionId == null) {
            insert_eventPK_versions.setNull(param++, java.sql.Types.INTEGER);
          } else {
            insert_eventPK_versions.setInt(param++, versionId.intValue());
          }
          insert_eventPK_versions.setInt(param++, idSifranta);
          insert_eventPK_versions.setString(param++, idSifre);
          insert_eventPK_versions.setString(param++, primaryKey);
          System.out.println(eventId + "," + versionId + "," + idSifranta + "," + idSifre + "," + primaryKey);
          success = success && insert_eventPK_versions.executeUpdate() > 0;
        }
      }
    }
    return success;
  }

  @Override
  public EventPK findEventPK(long eventId) throws SQLException {
    EventPK result = null;
    final Connection connection = ConnectionManager.getInstance().getTxConnection();

    if (find_eventPK == null) {
      find_eventPK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_eventPK.sql", "cp1250"));
    }

    synchronized (find_eventPK) {
      int param = 1;
      find_eventPK.clearParameters();
      find_eventPK.setLong(param++, eventId);
      ResultSet rs_findEventPK = find_eventPK.executeQuery();
      try {
        if (rs_findEventPK.next()) {
          int idSifranta = rs_findEventPK.getInt("IdSifranta");
          String idSifre = rs_findEventPK.getString("IdSifre");
          String primaryKey = rs_findEventPK.getString("PrimaryKey");
          result = new SqlEventPK(eventId, idSifranta, idSifre, primaryKey);
        }
      } finally {
        rs_findEventPK.close();
      }
    }
    return result;
  }

  @Override
  public EventPK findEventPKVersions(long eventId, Integer versionId) throws SQLException {
    EventPK result = null;
    final Connection connection = ConnectionManager.getInstance().getTxConnection();

    if (find_eventPK_versions == null) {
      find_eventPK_versions = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_eventPK_versions.sql", "cp1250"));
    }

    synchronized (find_eventPK_versions) {
      int param = 1;
      find_eventPK_versions.clearParameters();
      find_eventPK_versions.setLong(param++, eventId);
      if (versionId == null) {
        find_eventPK_versions.setInt(param++, 1);
        find_eventPK_versions.setNull(param++, java.sql.Types.INTEGER);
      } else {
        find_eventPK_versions.setInt(param++, 0);
        find_eventPK_versions.setInt(param++, versionId.intValue());
      }
      ResultSet rs_findEventPK = find_eventPK_versions.executeQuery();
      try {
        if (rs_findEventPK.next()) {
          Integer eventsPK_versionId = rs_findEventPK.getInt("VersionId");
          if (rs_findEventPK.wasNull()) {
            eventsPK_versionId = null;
          }
          int eventsPK_idSifranta = rs_findEventPK.getInt("IdSifranta");
          String eventsPK_idSifre = rs_findEventPK.getString("IdSifre");
          String eventsPK_primaryKey = rs_findEventPK.getString("PrimaryKey");

          result = new SqlEventPK(eventId, eventsPK_idSifranta, eventsPK_idSifre, eventsPK_primaryKey, eventsPK_versionId);
        }
      } finally {
        rs_findEventPK.close();
      }
    }
    return result;
  }

  @Override
  public EventPK findEventPKVersions(Integer idSifranta, String idSifre, Integer versionId, String primaryKey) throws SQLException {
    EventPK result = null;
    final Connection connection = ConnectionManager.getInstance().getTxConnection();

    if (find_eventPK_versions_byValues == null) {
      find_eventPK_versions_byValues = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "find_eventPK_versions_byValues.sql", "cp1250"));
    }

    synchronized (find_eventPK_versions_byValues) {
      int param = 1;
      find_eventPK_versions_byValues.clearParameters();
      if (versionId == null) {
        find_eventPK_versions_byValues.setInt(param++, 1);
        find_eventPK_versions_byValues.setNull(param++, java.sql.Types.INTEGER);
        find_eventPK_versions_byValues.setInt(param++, 0);
      } else {
        find_eventPK_versions_byValues.setInt(param++, 0);
        find_eventPK_versions_byValues.setInt(param++, versionId.intValue());
        find_eventPK_versions_byValues.setInt(param++, 1);
      }
      find_eventPK_versions_byValues.setInt(param++, idSifranta);
      find_eventPK_versions_byValues.setString(param++, idSifre);
      find_eventPK_versions_byValues.setString(param++, primaryKey);

      ResultSet rs_findEventPK = find_eventPK_versions_byValues.executeQuery();
      try {
        if (rs_findEventPK.next()) {
          Integer eventsPK_eventId = rs_findEventPK.getInt("EventId");
          if (rs_findEventPK.wasNull()) {
            eventsPK_eventId = null;
          }

          result = new SqlEventPK(eventsPK_eventId, idSifranta, idSifre, primaryKey, versionId);
        }
      } finally {
        rs_findEventPK.close();
      }

    }
    return result;
  }

  @Override
  public boolean deleteEvent(long eventId) throws SQLException {
    boolean success = false;
    final Connection connection = ConnectionManager.getInstance().getTxConnection();
    if (delete_event == null) {
      delete_event = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "delete_event.sql", "cp1250"));
    }
    synchronized (delete_event) {
      int param = 1;
      delete_event.clearParameters();
      delete_event.setLong(param++, eventId);
      success = delete_event.executeUpdate() > 0;
    }
    return success;
  }

  /**
   *
   * @return
   * @deprecated <p>Uporabi getPPSelectFields namesto tega!</p>
   *
   */
  @Deprecated
  @Override
  public String getPPJoinFields() {
    String result = "";
    try {
      PreparedStatement findPPPolja = ConnectionManager.getInstance().getConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "findPPPolja.sql", "cp1250"), java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
      ResultSet rsFindPPPolja = findPPPolja.executeQuery();
      try {
        StringBuilder sbSelect = new StringBuilder(200);
        rsFindPPPolja.beforeFirst();
        while (rsFindPPPolja.next()) {
          int idPolja = rsFindPPPolja.getInt("IdPolja");
          String imePolja = rsFindPPPolja.getString("ImePolja");
          final String ev_alias = "[ev_" + imePolja + "]";
          final String val_alias = "[val_" + imePolja + "]";
          sbSelect.append("\nLEFT OUTER JOIN ").append(SqlUtilities.getDataBase()).append(".[dbo].[ValuesPP] ").append(ev_alias).append(" ON (");
          sbSelect.append("PP.[PPID] = ").append(ev_alias).append(".[PPId]");
          sbSelect.append(" AND ").append(ev_alias).append(".[IdPolja] = ").append(idPolja).append(")");
          sbSelect.append("\nLEFT OUTER JOIN ").append(SqlUtilities.getDataBase()).append(".[dbo].[VariousValues] ").append(val_alias).append(" ON (");
          sbSelect.append(ev_alias).append(".[ValueId] = ").append(val_alias).append(".[Id] )");
        }
        result = sbSelect.toString();
      } finally {
        rsFindPPPolja.close();
      }
    } catch (SQLException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);

    }
    return result;
  }

  @Override
  public String getPPSelectFields() {
    String result = "";
    try {
      StringBuilder sbresult = new StringBuilder(200);
      Statement findPPPolja = ConnectionManager.getInstance().getConnection().createStatement(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
      try {
        ResultSet rsFindPPPolja = findPPPolja.executeQuery(ReadInputStream.getResourceAsString(getClass(), "findPPPolja.sql", "cp1250"));
        try {
          while (rsFindPPPolja.next()) {
            String imePolja = rsFindPPPolja.getString("ImePolja");
            final String val_alias = "[val_" + imePolja + "]";
            final String ev_alias = "[ev_" + imePolja + "]";
            int tipPolja = rsFindPPPolja.getInt("TipPolja");
            int idPolja = rsFindPPPolja.getInt("IdPolja");
            sbresult.append(",\n( SELECT (SELECT ");
            switch (tipPolja) {
              case 1:
                sbresult.append(val_alias).append(".IntValue AS [").append(imePolja).append("]");
                break;
              case 2:
                //Real
                sbresult.append(val_alias).append(".RealValue AS [").append(imePolja).append("]");
                break;
              case 3:
                //String
                sbresult.append(val_alias).append(".StringValue AS [").append(imePolja).append("]");
                break;
              case 4:
                //Date
                sbresult.append(val_alias).append(".DateValue AS [").append(imePolja).append("]");
                break;
              case 6:
                //Clob
                sbresult.append(val_alias).append(".ClobValue AS [").append(imePolja).append("]");
                break;
              case 7:
                //Boolean
                sbresult.append("CAST(").append(val_alias).append(".IntValue AS BIT) AS [").append(imePolja).append("]");
            }
            sbresult.append(" FROM  [ChangeLog].[dbo].[VariousValues] AS ").append(val_alias).append(" WHERE  ").append(val_alias).append(".[Id] = ").append(ev_alias).append(".[ValueId]  ) AS [").append(imePolja).append("]  FROM  [ChangeLog].[dbo].[ValuesPP] ").append(ev_alias).append("  WHERE  ").append(ev_alias).append(".[IdPolja]  = ").append(idPolja).append(" AND ").append(ev_alias).append(".[PPId] = PP.[PPID]     ) AS [").append(imePolja).append("] ");
          }
          result = sbresult.toString();
        } finally {
          rsFindPPPolja.close();
        }
      } finally {
        findPPPolja.close();
      }
    } catch (SQLException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  private boolean storeVeljavnost(Long events_ID, Long veljavnost) throws SQLException {
    boolean success = true;

    if (insertScheduler == null) {
      insertScheduler = ConnectionManager.getInstance().getTxConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "insertScheduler.sql", "cp1250"));
    }

    synchronized (insertScheduler) {
      int param = 1;
      insertScheduler.clearParameters();

      insertScheduler.setLong(param++, veljavnost);
      insertScheduler.setLong(param++, events_ID);
      success = success && insertScheduler.executeUpdate() > 0;
    }
    return success;
  }

  public Field getField(String fieldName) throws SQLException {
    Field result = null;
    if (get_field == null) {
      final Connection connection = ConnectionManager.getInstance().getTxConnection();
      get_field = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "get_field.sql", "cp1250"));
    }

    synchronized (get_field) {
      int param = 1;
      get_field.clearParameters();
      get_field.setString(param, fieldName);

      ResultSet rs_field = get_field.executeQuery();
      try {
        if (rs_field.next()) {
          int idPolja = rs_field.getInt("Id");
          int tipPolja = rs_field.getInt("TipPolja");
          result = new Field(idPolja, fieldName, ValueType.valueOf(tipPolja).getSqlType(), 1);
        }
      } finally {
        rs_field.close();
      }
    }

    return result;
  }

  @Override
  public boolean getSearchByEventPK(int idSifranta, String... idSifre) {
    boolean result = false;
    try {
      if (search_by_PK == null) {
        final Connection connection = ConnectionManager.getInstance().getTxConnection();
        search_by_PK = connection.prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "search_by_pk.sql", "cp1250"));
      }

      result = true;
      for (String sifra : idSifre) {
        synchronized (search_by_PK) {
          int param = 1;
          search_by_PK.clearParameters();
          search_by_PK.setInt(param++, idSifranta);
          search_by_PK.setString(param++, sifra);

          ResultSet rs = search_by_PK.executeQuery();
          try {
            if (rs.next()) {
              result = result && (rs.getInt(1) == 0);
            }
          } finally {
            rs.close();
          }
        }
      }
    } catch (SQLException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.WARNING, null, ex);
      result = false;
    }

    return result;
  }

  @Override
  public DbDataSource joinSecondaryDataSources(List<DbDataSource> dataSources) throws SQLException {
    MergedSecondaryDataSource result = null;
    EventFilterSearch mergedFilter = null;
    Set<Integer> sifranti = new LinkedHashSet<Integer>(dataSources.size());
    Map<DbDataSource, Collection<Integer>> ds = new HashMap<DbDataSource, Collection<Integer>>();

    for (DbDataSource dbDataSource : dataSources) {
      if (dbDataSource instanceof MergedSecondaryDataSource) {
        result = (MergedSecondaryDataSource) dbDataSource;
        for (Object object : dbDataSource.getParameters()) {
          if (object instanceof EventFilterSearch) {
            mergedFilter = (EventFilterSearch) object;
            break;
          }
        }
      } else {
        for (Object object : dbDataSource.getParameters()) {
          if (object instanceof EventFilterSearch) {
            ds.put(dbDataSource, ((EventFilterSearch) object).getSifrant());
            sifranti.addAll(((EventFilterSearch) object).getSifrant());
            break;
          }
        }
      }
    }

    if (result == null) {
      DbDataSource model = dataSources.get(0);

      result = new MergedSecondaryDataSource();
      List<Object> parameters = new ArrayList<Object>(model.getParameters());
      EventFilterSearch evfModel = null;
      EventLimit eventLimitModel = null;
      for (Object object : parameters) {
        if (object instanceof EventFilterSearch) {
          evfModel = (EventFilterSearch) object;
        } else if (object instanceof EventLimit) {
          eventLimitModel = (EventLimit) object;
          eventLimitModel.setValue("");
        }
      }

      EventFilterSearch newFilter = new EventFilterSearch(evfModel, sifranti);
      parameters.set(parameters.indexOf(evfModel), newFilter);

      result.setParameters(parameters);
//      result.setCountSql(model.getCountSql());
      result.setSelectSql(model.getSelectSql());

    } else {
      mergedFilter.addSifranti(sifranti);
      result.filterChanged();
    }
    result.setName("MergedSecondary " + sifranti.toString());


    for (DbDataSource dbDataSource : dataSources) {
      if (!(dbDataSource instanceof MergedSecondaryDataSource)) {
        //zamenjaj implementacion v dbDataSource (ce ni ze spremenjena)
//        if (!(dbDataSource.getImplementation() instanceof DataSourceProxy)) {

        DbDataSourceIndex index = new DbDataSourceIndex();
        index.setDataSource(result);

        Map<String, Collection<Object>> keyFilters = new LinkedHashMap<String, Collection<Object>>();
        Collection<Object> allowedValues = new ArrayList<Object>();
        for (Integer allowedValue : ds.get(dbDataSource)) {
          allowedValues.add(allowedValue);
        }
        keyFilters.put("IdSifranta", allowedValues);

        index.setKeysFilter(keyFilters);

        //pripravi novo implementacijo
        dbDataSource.setImplementation(new DataSourceProxy(dbDataSource, index));
      }
//      }
    }

    return result;
  }

  public static class MergedSecondaryDataSource extends DbDataSource {
  }

//  public  boolean isSuperDataSource(Set<DbDataSource> secondaryDataSources, DbDataSource joinedSecondaryDataSource) {
//    boolean result = false;
//    Collection<Integer> newSifranti = null;
//    for (Object parameter : joinedSecondaryDataSource.getParameters()) {
//      if (parameter instanceof EventFilterSearch) {
//        newSifranti = ((EventFilterSearch) parameter).getSifrant();
//      }
//    }
//
//    for (DbDataSource dbDataSource : secondaryDataSources) {
//      Collection<Integer> oldSifranti = null;
//      for (Object parameter : dbDataSource.getParameters()) {
//      if (parameter instanceof EventFilterSearch) {
//        oldSifranti = ((EventFilterSearch) parameter).getSifrant();
//        oldSifranti.removeAll(newSifranti);
//        //ce sem uspel vse odstraniti, potem je ta datasource podmnozica in ga je potrebno odstraniti
//        if(oldSifranti.isEmpty()){
//          result = true;
//        }
//      }
//    }
//    }
//
//    return result;
//  }
  public static class EventFilterSearch extends EventQueryParameter {

    private static final String EV_VERSIONED_SUBQUERY = ReadInputStream.getResourceAsString(EventFilterSearch.class, "find_event_by_values_versioned.sql", "cp1250");
    private static final String EV_NONVERSIONED_SUBQUERY = ReadInputStream.getResourceAsString(EventFilterSearch.class, "find_event_by_values_valid.sql", "cp1250");
    private static final String EV_SEARCH_BY_PK_SUBQUERY = ReadInputStream.getResourceAsString(EventFilterSearch.class, "find_event_by_PK.sql", "cp1250");
    private static final String EV_SEARCH_BY_VERSION_PK_SUBQUERY = ReadInputStream.getResourceAsString(EventFilterSearch.class, "find_event_by_version_PK.sql", "cp1250");
    DbDataSource.SubstSqlParameter sqlFindEventVersion = new DbDataSource.SubstSqlParameter("<%ev_version_filter%>");
    DbDataSource.SubstSqlParameter sqlFindEventType = new DbDataSource.SubstSqlParameter("<%ev_type_filter%>");
    DbDataSource.SubstSqlParameter sqlFindEventValid = new DbDataSource.SubstSqlParameter("<%ev_valid_filter%>");
    DbDataSource.SubstSqlParameter sqlFindEventSource = new DbDataSource.SubstSqlParameter("<%ev_source_filter%>");
    DbDataSource.SubstSqlParameter sqlFindEventDate = new DbDataSource.SubstSqlParameter("<%ev_date_filter%>");
    DbDataSource.SubstSqlParameter sqlFindEventPk = new DbDataSource.SubstSqlParameter("<%ev_pk_filter%>");
    DbDataSource.SubstSqlParameter sqlFindEventVersionPk = new DbDataSource.SubstSqlParameter("<%ev_version_pk_filter%>");
    DbDataSource.SubstSqlParameter sqlEventSifrant = new DbDataSource.SubstSqlParameter("<%ev_sifrant%>");
    DbDataSource.SubstSqlParameter sqlEventSifra = new DbDataSource.SubstSqlParameter("<%ev_sifra%>");
    String evVersionedSubquery;
    String evNonVersionedSubquery;
    List<Object> evNonVersionedParameters = new ArrayList<Object>();
    List<Object> evVersionedParameters = new ArrayList<Object>();
    Integer eventSource;
    java.util.Date eventDatum;
    Collection<Integer> sifrant = new LinkedHashSet<Integer>();
    String[] sifra;
    boolean validOnly;

    public EventFilterSearch(EventFilterSearch eventFilterSearch, Set<Integer> sifranti) {
      super(eventFilterSearch.namedParameters);
      init(eventFilterSearch.eventSource, eventFilterSearch.eventDatum, sifranti, null, eventFilterSearch.validOnly, eventFilterSearch.eventPK);
    }

    public EventFilterSearch(Map<Field, DbDataSource.SqlParameter<Object>> namedParameters, Integer eventSource, java.util.Date eventDatum, int sifrant, String[] sifra, boolean validOnly, EventPK eventPK) {
      super(namedParameters);
      init(eventSource, eventDatum, Arrays.asList(new Integer[]{sifrant}), sifra, validOnly, eventPK);
    }

    public EventFilterSearch addSifranti(Set<Integer> sifranti) {
      sifrant.addAll(sifranti);
      init(eventSource, eventDatum, sifrant, sifra, validOnly, eventPK);
      return this;
    }

    private void init(Integer eventSource, java.util.Date eventDatum, Collection<Integer> sifranti, String[] sifra, boolean validOnly, EventPK eventPK) {
      this.eventSource = eventSource;
      this.eventDatum = eventDatum;
      this.sifrant = new LinkedHashSet<Integer>(sifranti);
      this.sifra = sifra;
      this.validOnly = validOnly;
      this.eventPK = eventPK;

      sqlFindEventVersion.clearParameters();
      sqlFindEventType.clearParameters();
      sqlFindEventValid.clearParameters();
      sqlFindEventSource.clearParameters();
      sqlFindEventDate.clearParameters();
      sqlFindEventPk.clearParameters();
      sqlFindEventVersionPk.clearParameters();
      sqlEventSifrant.clearParameters();
      sqlEventSifra.clearParameters();

      sqlFindEventVersion.setValue("?");
      sqlFindEventVersion.addParameter(this.versionId);

      sqlFindEventValid.setValue(validOnly ? " AND ev.valid = 1 " : "");
      if (sifra == null) {
        StringBuilder sbSifrant = new StringBuilder();
        for (Integer s : sifranti) {
          sqlFindEventType.addParameter(new SqlParameter<Integer>(java.sql.Types.INTEGER, s));
          sbSifrant.append(sbSifrant.length() > 0 ? "," : "").append(" ? ");
        }
        if (sifranti.size() > 1) {
          sbSifrant.insert(0, " ev.[IdSifranta] IN (").append(" ) ");
        } else {
          sbSifrant.insert(0, " ev.[IdSifranta] = ");
        }

        sqlFindEventType.setValue(sbSifrant.toString());

      } else {
        final SqlParameter<Integer> qpSifrant = new SqlParameter<Integer>(java.sql.Types.INTEGER, sifranti.toArray(new Integer[sifranti.size()])[0]);

        if (sifra.length == 1) {
          if (ConnectionManager.getInstance().isConvertToVarchar()) {
            sqlFindEventType.setValue("ev.[IdSifranta] = ? AND ev.[IdSifre] = CAST(? AS VARCHAR)");
          } else {
            sqlFindEventType.setValue("ev.[IdSifranta] = ? AND ev.[IdSifre] = ?");
          }
          sqlFindEventType.addParameter(qpSifrant);
          sqlFindEventType.addParameter(new SqlParameter<String>(java.sql.Types.VARCHAR, sifra[0]));
        } else {
          StringBuilder sbet = new StringBuilder();
          sqlFindEventType.addParameter(qpSifrant);
          for (String s : sifra) {
            sbet.append(sbet.length() > 0 ? ", " : "");
            if (ConnectionManager.getInstance().isConvertToVarchar()) {
              sbet.append("CAST(? AS VARCHAR)");
            } else {
              sbet.append("?");
            }
            sqlFindEventType.addParameter(new SqlParameter<String>(java.sql.Types.VARCHAR, s));
          }
          sbet.insert(0, "ev.[IdSifranta] = ? AND ev.[IdSifre] IN (").append(") ");
          sqlFindEventType.setValue(sbet.toString());
        }
      }

      sqlEventSifrant.setValue("ev.[IdSifranta]");
      sqlEventSifra.setValue("ev.[IdSifre]");

      if (eventSource == null) {
        sqlFindEventSource.setValue("");
      } else {
        sqlFindEventSource.setValue(" AND ev.[IdEventSource] = ?");
        sqlFindEventSource.addParameter(new SqlParameter<Integer>(java.sql.Types.INTEGER, eventSource));
      }
      if (eventDatum == null) {
        sqlFindEventDate.setValue("");
      } else {
        sqlFindEventDate.setValue(" AND ev.DATUM = ?");
        sqlFindEventDate.addParameter(new SqlParameter<Date>(java.sql.Types.TIMESTAMP, eventDatum));
      }

      //  setEventPK(eventPK);

      evVersionedParameters.add(sqlFindEventVersion);
      evVersionedParameters.add(sqlFindEventVersion);
      evVersionedParameters.add(sqlFindEventType);
      evVersionedParameters.add(sqlFindEventSource);
      evVersionedParameters.add(sqlFindEventDate);
      evVersionedParameters.add(sqlFindEventVersionPk);
      evVersionedSubquery = SQLDataSource.substParameters(EV_VERSIONED_SUBQUERY, evVersionedParameters);

      evNonVersionedParameters.add(sqlFindEventType);
      evNonVersionedParameters.add(sqlFindEventValid);
      evNonVersionedParameters.add(sqlFindEventSource);
      evNonVersionedParameters.add(sqlFindEventDate);
      evNonVersionedParameters.add(sqlFindEventPk);
      evNonVersionedSubquery = SQLDataSource.substParameters(EV_NONVERSIONED_SUBQUERY, evNonVersionedParameters);
    }

    @Override
    public boolean hasVersionId() {
      return versionId.getValue() != null && (((Long) versionId.getValue()) > 0);
    }
    protected EventPK eventPK;

    /**
     * Get the value of eventPK
     *
     * @return the value of eventPK
     */
    protected EventPK getEventPK() {
      return eventPK;
    }

    /**
     * Set the value of eventPK
     *
     * @param eventPK new value of eventPK
     */
    protected void setEventPK(EventPK eventPK) {
      this.eventPK = eventPK;
      if (eventPK == null) {
        sqlFindEventVersion.setValue(" null ");
        sqlFindEventVersion.clearParameters();
        sqlFindEventPk.setValue("");
        sqlFindEventPk.clearParameters();
      } else {
        sqlFindEventPk.setValue("ev.[Id] IN (" + EV_SEARCH_BY_PK_SUBQUERY + ")");
        sqlFindEventPk.addParameter(sqlEventSifrant);
        sqlFindEventPk.addParameter(sqlEventSifra);
        sqlFindEventPk.addParameter(eventPK.getPrimaryKey());

        sqlFindEventVersionPk.setValue("ev.[Id] IN (" + EV_SEARCH_BY_VERSION_PK_SUBQUERY + ")");
        sqlFindEventVersionPk.addParameter(sqlEventSifrant);
        sqlFindEventVersionPk.addParameter(sqlEventSifra);
        sqlFindEventVersionPk.addParameter(sqlFindEventVersion);
        sqlFindEventVersionPk.addParameter(eventPK.getPrimaryKey());
      }
      evVersionedSubquery = SQLDataSource.substParameters(EV_VERSIONED_SUBQUERY, evVersionedParameters);
      evNonVersionedSubquery = SQLDataSource.substParameters(EV_NONVERSIONED_SUBQUERY, evNonVersionedParameters);
    }

    @Override
    public List<Object> getParameters() {
      return Collections.unmodifiableList(hasVersionId() ? evVersionedParameters : evNonVersionedParameters);
    }

    @Override
    public String getValue() {
      return hasVersionId() ? evVersionedSubquery : evNonVersionedSubquery;
    }

    public Collection<Integer> getSifrant() {
      return sifrant;
    }
  }

  private static class EventLimit extends DbDataSource.SubstSqlParameter {

    private EventLimit(String replace) {
      super(replace);
    }
  }

  private static class EventCacheTemporaryParameter extends TemporarySubselectSqlParameter {

    public EventCacheTemporaryParameter(String replace) {
      super(replace);
    }

    @Override
    public void executeQuery(Connection connection, List<Object> parameters) throws SQLException {
      long timer = System.currentTimeMillis();

      String DB_USER = ConnectionManager.getInstance().getProperty(ConnectionManager.DB_USER, "");
      List<Object> qparams = new ArrayList<Object>(parameters.size());
      qparams.addAll(getParameters());
      qparams.addAll(parameters);
      qparams.add(getSubstSqlParameter());
      if (sqlMaterializedView != null) {
        qparams.add(sqlMaterializedView);
      }

      Statement statement = connection.createStatement();
      try {
        try {
          if (getCheckTableSql() != null) {
            statement.executeQuery(SQLDataSource.substParameters(getCheckTableSql(), qparams));
          }
        } catch (SQLException ex) {
          String context = connection.getCatalog();

          if (getCatalog() != null) {
            connection.setCatalog(getCatalog());
          }

          for (String sql : getCreateTableSqls()) {
            String createSQL = SQLDataSource.substParameters(sql.replaceAll("<%TS%>", "_" + DB_USER + Long.toString(System.currentTimeMillis())), qparams);
            if (DbDataSource.DUMP_SQL) {
              System.out.println(createSQL + ";");
              System.out.println("-- -- -- --");
            }
            statement.execute(createSQL);
          }

          if (getCatalog() != null) {
            connection.setCatalog(context);
          }
        }

        qparams = SQLDataSource.preprocessParameters(qparams, connection);


        try {
          if (getEmptyTableSql().length() > 0) {
            String query = SQLDataSource.substParameters(getEmptyTableSql(), qparams);
            if (!Equals.equals(this.connection, connection)
                    || !Equals.equals(this.qEmptyTable, query)) {
              String[] sqls = query.split(";");
              for (PreparedStatement preparedStatement : this.psEmptyTable) {
                preparedStatement.close();
              }
              this.psEmptyTable.clear();
              for (String sql : sqls) {
                this.psEmptyTable.add(connection.prepareStatement(sql,
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY,
                        ResultSet.HOLD_CURSORS_OVER_COMMIT));
              }
              this.qEmptyTable = query;
            }

            if (DbDataSource.DUMP_SQL) {
              System.out.println("############## empty");
              System.out.println(this.qEmptyTable);
            }
            int rowsDeleted = 0;
            for (PreparedStatement preparedStatement : psEmptyTable) {
              rowsDeleted += SQLDataSource.executeUpdate(preparedStatement, qparams);
            }
            if (DbDataSource.DUMP_SQL) {
              System.out.println("Rows deleted:" + rowsDeleted);
            }
          }

          String query = SQLDataSource.substParameters(getFillTableSql(), qparams);
          if (!Equals.equals(this.connection, connection)
                  || !Equals.equals(this.qFillTable, query)) {
            if (this.psFillTable != null) {
              this.psFillTable.close();
            }
            this.psFillTable = connection.prepareStatement(query,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.HOLD_CURSORS_OVER_COMMIT);
            this.qFillTable = query;
          }

          if (DbDataSource.DUMP_SQL) {
            System.out.println("############## fill");
            System.out.println(this.qFillTable);
          }
          System.out.println("Rows added:" + SQLDataSource.executeUpdate(psFillTable, qparams));
          if (getCleanTableSqls() != null) {
            List<String> queries = new ArrayList<String>(getCleanTableSqls().length);
            for (String sql : getCleanTableSqls()) {
              queries.add(SQLDataSource.substParameters(sql, qparams));
            }

            if (!Equals.equals(this.connection, connection)
                    || !Equals.equals(this.qCleanTable, queries)) {
              for (PreparedStatement preparedStatement : this.psCleanTable) {
                preparedStatement.close();
              }
              this.psCleanTable.clear();
              for (String sql : queries) {
                this.psCleanTable.add(connection.prepareStatement(sql,
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY,
                        ResultSet.HOLD_CURSORS_OVER_COMMIT));
              }
              this.qCleanTable = queries;
            }

            if (DbDataSource.DUMP_SQL) {
              System.out.println("############## cleanup/update");
              for (String string : queries) {
                System.out.println(string);
              }
            }
            int rowsAffected = 0;
            for (PreparedStatement preparedStatement : psCleanTable) {
              rowsAffected += SQLDataSource.executeUpdate(preparedStatement, qparams);
            }
            if (DbDataSource.DUMP_SQL) {
              System.out.println("Rows cleaned/updated:" + rowsAffected);
            }
          }
          if ((sqlMaterializedView != null) && (sqlMaterializedView.getSetViewVersionSql() != null)) {
            SQLDataSource.execute(connection.prepareStatement(SQLDataSource.substParameters(sqlMaterializedView.getSetViewVersionSql(), qparams)), qparams);
          }
          if (DbDataSource.DUMP_SQL) {
            System.out.println("cached:event:fill:" + getValue() + "..." + (System.currentTimeMillis() - timer) + "ms");
            System.out.println("##############");
          }


        } catch (SQLException ex) {
          Logger.getLogger(TemporarySubselectSqlParameter.class.getName()).log(Level.SEVERE, "ERROR:cached:event:fill:" + getValue(), ex);
          throw new SQLException(ex);
        }

      } finally {
        statement.close();
      }
      this.connection = connection;
    }
  }

  private static class TemporaryParametersFactory extends DataSourceParametersFactory<DataSourceConfig> {

    public TemporaryParametersFactory() {
    }

    @Override
    protected TemporarySubselectSqlParameter createTemporaryTable(TemporaryTable tt) {
      return super.createTemporaryTable(tt);
    }

    @Override
    protected TemporarySubselectSqlParameter createTemporaryTableParameter(TemporaryTable tt) {
      return new EventCacheTemporaryParameter(tt.getReplace());
    }
  }

  private static class EventQueryKey {

    public EventQueryKey(Event event) {
      this.sifrant = event.getSifrant();
      this.sifra = event.getSifra();
      java.util.Set<Field> pk = new java.util.HashSet<Field>();
      if (event.getPrimaryKey() != null) {
        pk.addAll(Arrays.asList(event.getPrimaryKey()));
      } else {
        pk.addAll(event.getEventValues().keySet());
        //primaryKey.add(Event.EVENT_DATE);
        pk.add(Event.EVENT_SOURCE);
      }
      primaryKey = pk.toArray(new Field[pk.size()]);
    }
    private int sifrant;

    /**
     * Get the value of sifrant
     *
     * @return the value of sifrant
     */
    public int getSifrant() {
      return sifrant;
    }

    /**
     * Set the value of sifrant
     *
     * @param sifrant new value of sifrant
     */
    public void setSifrant(int sifrant) {
      this.sifrant = sifrant;
    }
    private String sifra;

    /**
     * Get the value of sifra
     *
     * @return the value of sifra
     */
    public String getSifra() {
      return sifra;
    }

    /**
     * Set the value of sifra
     *
     * @param sifra new value of sifra
     */
    public void setSifra(String sifra) {
      this.sifra = sifra;
    }
    private Field[] primaryKey;

    /**
     * Get the value of primaryKey
     *
     * @return the value of primaryKey
     */
    public Field[] getPrimaryKey() {
      return primaryKey;
    }

    /**
     * Set the value of primaryKey
     *
     * @param primaryKey new value of primaryKey
     */
    public void setPrimaryKey(Field... primaryKey) {
      this.primaryKey = primaryKey;
    }
    private PreparedStatement query;

    /**
     * Get the value of query
     *
     * @return the value of query
     */
    public PreparedStatement getQuery() {
      return query;
    }

    /**
     * Set the value of query
     *
     * @param query new value of query
     */
    public void setQuery(PreparedStatement query) {
      this.query = query;
    }
    private EventQuery eventQuery;

    /**
     * Get the value of eventQuery
     *
     * @return the value of eventQuery
     */
    public EventQuery getEventQuery() {
      return eventQuery;
    }

    /**
     * Set the value of eventQuery
     *
     * @param eventQuery new value of eventQuery
     */
    public void setEventQuery(EventQuery eventQuery) {
      this.eventQuery = eventQuery;
      this.searchByEventPK = eventQuery.isSearchByEventPK();
    }
    protected boolean searchByEventPK;

    /**
     * Get the value of searchByEventPK
     *
     * @return the value of searchByEventPK
     */
    public boolean isSearchByEventPK() {
      return searchByEventPK;
    }

    public ResultSet executeQuery(Event event) throws SQLException {
      List<FieldValue> parameters = new ArrayList<FieldValue>();
      for (Field f : this.primaryKey) {
        List<FieldValue> values;
        if (event.getEventValues().containsKey(f)) {
          values = event.getEventValues().get(f);
        } else {
          values = new java.util.ArrayList<FieldValue>();
          values.add(new FieldValue(f));
        }

        parameters.addAll(values);
      }

      return executeQuery(parameters);
    }

    public ResultSet executeQuery(List<FieldValue> fields) throws SQLException {
      for (FieldValue field : fields) {
        if (eventQuery.getNamedParameters().containsKey(field)) {
          eventQuery.getNamedParameters().get(field).setValue(field.getValue());
        }
      }

      return SQLDataSource.executeQuery(this.query, eventQuery.getParameters());
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final EventQueryKey other = (EventQueryKey) obj;
      if (this.sifrant != other.sifrant) {
        return false;
      }
      if ((this.sifra == null) ? (other.sifra != null) : !this.sifra.equals(other.sifra)) {
        return false;
      }
      if (!Arrays.deepEquals(this.primaryKey, other.primaryKey)) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 3;
      hash = 97 * hash + this.sifrant;
      hash = 97 * hash + (this.sifra != null ? this.sifra.hashCode() : 0);
      hash = 97 * hash + Arrays.deepHashCode(this.primaryKey);
      return hash;
    }
  }
  private Map<EventQueryKey, EventQueryKey> findEventStatements = new SoftHashMap<EventQueryKey, EventQueryKey>();
  private Map<EventType, Boolean> searchByPKMap = new HashMap<EventType, Boolean>();

  @Override
  public Event findEvent(Event event) throws SQLException {
    Long eventId = null;
    if (!((event.getId() == null) || (event.getId() <= 0))) {
      eventId = event.getId();
    } else {
      EventType eventType = new EventType(event);
      if (!searchByPKMap.containsKey(eventType)) {
        if (search_by_PK == null) {
          final Connection connection = ConnectionManager.getInstance().getTxConnection();
          search_by_PK = connection.prepareStatement(ReadInputStream.getResourceAsString(getClass(), "search_by_pk.sql", "cp1250"));
        }

        //prestejem evente, kateri nimajo shranjenega primarykey-a
        //ce je vsaj eden, ne iscem po PK ampak po vrednostih PK
        int param = 1;
        search_by_PK.clearParameters();
        search_by_PK.setInt(param++, event.getSifrant());
        search_by_PK.setString(param++, event.getSifra());
        ResultSet rs_search_by_PK = search_by_PK.executeQuery();
        try {
          Boolean search = Boolean.FALSE;
          if (rs_search_by_PK.next()) {
            int count = rs_search_by_PK.getInt(1);
            search = count == 0;
          }
          searchByPKMap.put(eventType, search);
        } finally {
          rs_search_by_PK.close();
        }
      }
      Boolean searchByPK = searchByPKMap.get(eventType);
      if (searchByPK != null && searchByPK) {
        //find by PK
        EventPK eventPK = event.getEventPK();
        int idSifranta = event.getSifrant();
        String idSifre = event.getSifra();
        String primaryKeyHex = eventPK.getPrimaryKey();

        if (find_event_by_PK == null) {
          final Connection connection = ConnectionManager.getInstance().getTxConnection();
          find_event_by_PK = connection.prepareStatement(ReadInputStream.getResourceAsString(getClass(), "find_event_by_PK.sql", "cp1250"));
        }
        int param = 1;
        find_event_by_PK.clearParameters();
        find_event_by_PK.setInt(param++, idSifranta);
        find_event_by_PK.setString(param++, idSifre);
        find_event_by_PK.setString(param++, primaryKeyHex);

        ResultSet rs = find_event_by_PK.executeQuery();
        try {
          if (rs.next()) {
            eventId = rs.getLong("EventId");
          }
        } finally {
          rs.close();
        }
      } else {
        ////////old
        EventQueryKey eqk = new EventQueryKey(event);
        boolean seek = false;

        if (findEventStatements.containsKey(eqk)) {
          eqk = findEventStatements.get(eqk);
          seek = true;
        } else {
          java.util.Set<Field> primaryKey = new java.util.HashSet<Field>();
          if (event.getPrimaryKey() != null) {
            primaryKey.addAll(Arrays.asList(event.getPrimaryKey()));
          } else {
            primaryKey.addAll(event.getEventValues().keySet());
            //primaryKey.add(Event.EVENT_DATE);
            primaryKey.add(Event.EVENT_SOURCE);
          }

          EventQuery eq = prepareEventQuery(event, primaryKey, new java.util.HashSet<Field>());
          int valuesSet = eq.getValuesSet();


          if (event.getPrimaryKey() != null) {
            //vedno iscemo po PK, cetudi se dogodek pojavlja v polju z vec vrednostmi
            //ce niso bile nastavljene vse vrednosti PK-ja potem ne iscemo
            seek = valuesSet == primaryKey.size();
          } else {
            java.util.List parametersVecVrednosti = new java.util.ArrayList<Object>();
            parametersVecVrednosti.add(event.getSifrant());
            parametersVecVrednosti.add(event.getSifra());

            ResultSet rsVecVrednosti = SQLDataSource.executeQuery(getCheckVecVrednostiEventSQL(), parametersVecVrednosti, ConnectionManager.getInstance().getTxConnection());
            try {
              if (rsVecVrednosti.next()) {
                if (rsVecVrednosti.getInt(1) > 0) {
                  seek = false;
                }
              }
            } finally {
              rsVecVrednosti.getStatement().close();
            }
          }

          if (seek) {
            String sql = SQLDataSource.substParameters(eq.getQuery(), eq.getParameters());
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Searching event : \n {0}", sql);
            PreparedStatement statement = ConnectionManager.getInstance().getTxConnection().prepareStatement(sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.HOLD_CURSORS_OVER_COMMIT);

            statement.setQueryTimeout(1800);

            eqk.setEventQuery(eq);
            eqk.setQuery(statement);

            findEventStatements.put(eqk, eqk);
          }
        }

        if (seek) {
          synchronized (eqk) {
            ResultSet rs = eqk.executeQuery(event);
            try {
              if (rs.next()) {
                eventId = rs.getLong("Id");
              }
            } finally {
              rs.close();
            }
          }
        }
      }
    }
    if (eventId != null) {
      event.setId(eventId);
      return findEvent(eventId);
    } else {
      return null;
    }
  }

  private static class GeneratedFieldFactory extends DataSourceFactory {

    private static final boolean CACHED_GGF = true;
    private static final boolean USE_INDEXED_CACHE = true;
    private final boolean DEVELOPMENT;
    private static GeneratedFieldFactory instance;
    public PreparedStatement getGeneratedFields;
    private DbDataSource dsGeneratedFields = null;
    private DbDataSourceIndex dbxAllVisibleGeneratedFields = null;
    private DbDataSourceIndex dbxAllGeneratedFields = null;
    private DbDataSourceIndex dbxAllVisibleGeneratedFieldsSifrant = null;
    private DbDataSourceIndex dbxAllGeneratedFieldsSifrant = null;
    private CachedRowSet crsAllGeneratedFields = null;
    private DataSourceFilters dsGeneratedFieldsFilter = new DataSourceFilters("<%GENERATED_FILTERS%>");
    private TemporarySubselectSqlParameter ttGeneratedFields;
    private java.util.List<Object> ttGeneratedFieldsParameters = new ArrayList<Object>();
    private DataSourceFilters.IntegerSeekType I_TYPE_ID_SIFANTA = new DataSourceFilters.IntegerSeekType("GeneratedFields.IdSifranta", DataSourceFilters.IntegerSeekType.EQUALS);
    private DataSourceFilters.SeekType I_TYPE_ID_SIFRE = new DataSourceFilters.SeekType("GeneratedFields.IdSifre", DataSourceFilters.SeekType.EQUALS, 1);
    private DataSourceFilters.IntegerSeekType I_TYPE_HIDDEN = new DataSourceFilters.IntegerSeekType("GeneratedFields.Hidden", DataSourceFilters.IntegerSeekType.EQUALS);
    private DbDataSource.SubstSqlParameter S_ACTIVITY = new DbDataSource.SubstSqlParameter("<%ACTIVITY_ID%>");

    public GeneratedFieldFactory() {
      super(null);
      DEVELOPMENT = Boolean.parseBoolean(ConnectionManager.getInstance().getProperty("application.mode.development", "false"));
    }

    private static GeneratedFieldFactory getInstance() throws JAXBException, UnsupportedEncodingException, IOException, SQLException {
      if (instance == null) {
        instance = (new GeneratedFieldFactory()).init();
      }

      return instance;
    }

    private GeneratedFieldFactory init() throws JAXBException, UnsupportedEncodingException, IOException, SQLException {
      if (CACHED_GGF) {
        dsGeneratedFields = new DbDataSource();
        dsGeneratedFields.setQueuedDelay(0);
        dsGeneratedFields.setCacheRowSet(true);

        dsGeneratedFieldsFilter.setOperator("WHERE");
        dsGeneratedFieldsFilter.addRequired(I_TYPE_ID_SIFANTA);

        ttGeneratedFields = CachedTemporaryTablesManager.getInstance().getCachedTemporarySubselectSqlParameter(this.getClass(), "generatedFields.xml");

        java.util.List<Object> parameters = new ArrayList<Object>();

        parameters.add(ttGeneratedFields);
        parameters.add(dsGeneratedFieldsFilter);
        parameters.add(S_ACTIVITY);

        ttGeneratedFieldsParameters.add(dsGeneratedFieldsFilter);
        ttGeneratedFieldsParameters.add(S_ACTIVITY);

        dsGeneratedFields.setName("DS:GET_GENERATED_FIELDS");

        dsGeneratedFields.setConnection(ConnectionManager.getInstance().getTxConnection());

        dsGeneratedFields.setParameters(parameters);
        dsGeneratedFields.setSelectSql(ReadInputStream.getResourceAsString(getClass(), "getGeneratedFieldsCached.sql", "cp1250"));

        if (USE_INDEXED_CACHE) {
          dsGeneratedFields.loadData();

          dbxAllVisibleGeneratedFields = new DbDataSourceIndex();
          dbxAllVisibleGeneratedFields.addKeys("IdSifranta", "IdSifre", "Hidden", "ActivityId");
          dbxAllVisibleGeneratedFields.setDataSource(dsGeneratedFields);

          dbxAllGeneratedFields = new DbDataSourceIndex();
          dbxAllGeneratedFields.addKeys("IdSifranta", "IdSifre", "ActivityId");
          dbxAllGeneratedFields.setDataSource(dsGeneratedFields);

          dbxAllVisibleGeneratedFieldsSifrant = new DbDataSourceIndex();
          dbxAllVisibleGeneratedFieldsSifrant.addKeys("IdSifranta", "Hidden", "ActivityId");
          dbxAllVisibleGeneratedFieldsSifrant.setDataSource(dsGeneratedFields);

          dbxAllGeneratedFieldsSifrant = new DbDataSourceIndex();
          dbxAllGeneratedFieldsSifrant.addKeys("IdSifranta", "ActivityId");
          dbxAllGeneratedFieldsSifrant.setDataSource(dsGeneratedFields);

          crsAllGeneratedFields = dsGeneratedFields.getCachedRowSet();
        }
      }

      return this;
    }

    public synchronized CachedRowSet getGeneratedFields(int idSifranta, String idSifre, boolean visibleOnly, ActivityEvent activityEvent) throws SQLException {
      if (CACHED_GGF) {
        CachedRowSet rs;
        if (USE_INDEXED_CACHE) {
          long start = System.currentTimeMillis();
          if (DEVELOPMENT && !ttGeneratedFields.getSqlMaterializedView().isViewValid(dsGeneratedFields.getConnection(), ttGeneratedFieldsParameters)) {
            dsGeneratedFields.reload();
            crsAllGeneratedFields = dsGeneratedFields.getCachedRowSet();
          }
          rs = new com.openitech.sql.rowset.CachedRowSetImpl();
          Set<Integer> rows;

          if (idSifre == null) {
            if (visibleOnly) {
              rows = dbxAllVisibleGeneratedFieldsSifrant.findRows(idSifranta, !visibleOnly, (activityEvent == null) ? null : activityEvent.getActivityId());
              if (rows == null || rows.isEmpty()) {
                rows = dbxAllVisibleGeneratedFieldsSifrant.findRows(idSifranta, !visibleOnly, null);
              }
            } else {
              rows = dbxAllGeneratedFieldsSifrant.findRows(idSifranta, (activityEvent == null) ? null : activityEvent.getActivityId());
              if (rows == null || rows.isEmpty()) {
                rows = dbxAllGeneratedFieldsSifrant.findRows(idSifranta, null);
              }
            }
          } else {
            if (visibleOnly) {
              rows = dbxAllVisibleGeneratedFields.findRows(idSifranta, idSifre, !visibleOnly, (activityEvent == null) ? null : activityEvent.getActivityId());
              if (rows == null || rows.isEmpty()) {
                rows = dbxAllVisibleGeneratedFields.findRows(idSifranta, idSifre, !visibleOnly, null);
              }
            } else {
              rows = dbxAllGeneratedFields.findRows(idSifranta, idSifre, (activityEvent == null) ? null : activityEvent.getActivityId());
              if (rows == null || rows.isEmpty()) {
                rows = dbxAllGeneratedFields.findRows(idSifranta, idSifre, null);
              }
            }
          }
          ((com.openitech.sql.rowset.CachedRowSetImpl) rs).populate(crsAllGeneratedFields, rows);

          long end = System.currentTimeMillis();
          System.out.println("getGeneratedFields::" + (end - start) + " ms.");

          return rs;
        } else {
          long start = System.currentTimeMillis();
          dsGeneratedFieldsFilter.setSeekValue(I_TYPE_ID_SIFANTA, idSifranta);
          dsGeneratedFieldsFilter.setSeekValue(I_TYPE_ID_SIFRE, idSifre);
          dsGeneratedFieldsFilter.setSeekValue(I_TYPE_HIDDEN, visibleOnly ? 0 : Integer.MIN_VALUE);
          if (activityEvent == null) {
            S_ACTIVITY.setValue("AND GeneratedFields.[ActivityId] IS NULL");
            S_ACTIVITY.clearParameters();
          } else {
            S_ACTIVITY.setValue("AND GeneratedFields.[ActivityId]=?");
            S_ACTIVITY.addParameter(activityEvent.getActivityId());
          }
          dsGeneratedFields.filterChanged();
          if ((activityEvent != null) && (dsGeneratedFields.getRowCount() == 0)) {
            S_ACTIVITY.setValue("AND GeneratedFields.[ActivityId] IS NULL");
            S_ACTIVITY.clearParameters();
            dsGeneratedFields.filterChanged();
          }

          rs = new com.sun.rowset.CachedRowSetImpl();
          ResultSet rs_generatedFields = dsGeneratedFields.getResultSet();
          try {
            rs.populate(rs_generatedFields);
          } finally {
            rs_generatedFields.close();
          }
          long end = System.currentTimeMillis();
          System.out.println("getGeneratedFields::" + (end - start) + " ms.");


          return rs;
        }
      } else {
        if (getGeneratedFields == null) {
          getGeneratedFields = ConnectionManager.getInstance().getConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "getGeneratedFields.sql", "cp1250"), java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        }
        getGeneratedFields.clearParameters();
        int param = 1;
        if (activityEvent != null) {
          getGeneratedFields.setLong(param++, activityEvent.getActivityId());
          getGeneratedFields.setInt(param++, activityEvent.getIdSifranta());
          getGeneratedFields.setString(param++, activityEvent.getIdSifre());
          getGeneratedFields.setLong(param++, activityEvent.getActivityId());
          getGeneratedFields.setInt(param++, activityEvent.getIdSifranta());
          getGeneratedFields.setString(param++, activityEvent.getIdSifre());
        } else {
          getGeneratedFields.setNull(param++, java.sql.Types.INTEGER);
          getGeneratedFields.setNull(param++, java.sql.Types.INTEGER);
          getGeneratedFields.setNull(param++, java.sql.Types.VARCHAR);
          getGeneratedFields.setNull(param++, java.sql.Types.INTEGER);
          getGeneratedFields.setNull(param++, java.sql.Types.INTEGER);
          getGeneratedFields.setNull(param++, java.sql.Types.VARCHAR);
        }
        getGeneratedFields.setInt(param++, idSifranta);
        getGeneratedFields.setBoolean(param++, idSifre == null);
        getGeneratedFields.setString(param++, idSifre);
        getGeneratedFields.setBoolean(param++, !visibleOnly);
        CachedRowSet rs = new com.sun.rowset.CachedRowSetImpl();
        ResultSet rs_generatedFields = getGeneratedFields.executeQuery();
        try {
          rs.populate(rs_generatedFields);
        } finally {
          rs_generatedFields.close();
        }

        return rs;
      }
    }
  }

  @Override
  public synchronized CachedRowSet getGeneratedFields(int idSifranta, String idSifre, boolean visibleOnly, ActivityEvent activityEvent) throws SQLException {
    CachedRowSet result = null;
    try {
      result = GeneratedFieldFactory.getInstance().getGeneratedFields(idSifranta, idSifre, visibleOnly, activityEvent);
    } catch (JAXBException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } catch (UnsupportedEncodingException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    }

    return result;
  }

  private Map<NamedFieldIds, NamedFieldIds> getEventFields(int idSifranta, String idSifre) throws SQLException {
    Map<NamedFieldIds, NamedFieldIds> result = new java.util.HashMap<NamedFieldIds, NamedFieldIds>();

    ResultSet rs = getGeneratedFields(idSifranta, idSifre);
    try {
      rs.beforeFirst();
      while (rs.next()) {
        result.put(new NamedFieldIds(rs.getString("ImePolja"), Integer.MIN_VALUE), new NamedFieldIds(rs.getString("ImePolja"), rs.getInt("IdPolja")));
      }
    } finally {
      rs.close();
    }

    return result;
  }
  private final static Event SYSTEM_IDENTITIES = new Event(0, "ID01", -1);
  private final Semaphore lock = new Semaphore(1);

  @Override
  public FieldValue getNextIdentity(Field field, Object initValue) throws SQLException {
    try {
      //ta motoda mora imeti lock, èeprav se zaenkrat naj nebi klicala iz veè threadov
      lock.acquire();

      try {
        if (findIdentityEventId == null) {
          findIdentityEventId = ConnectionManager.getInstance().getTxConnection().prepareStatement(ReadInputStream.getReplacedSql("SELECT TOP 1 Id FROM <%ChangeLog%>.[dbo].[Events] ev WITH (NOLOCK) WHERE IdSifranta = 0 AND IdSifre = 'ID01'"));
        }
        ResultSet rsFindIdentityEventId = findIdentityEventId.executeQuery();
        Event system = null;
        try {
          if (rsFindIdentityEventId.next()) {
            system = findEvent(rsFindIdentityEventId.getLong(1));
          }
        } finally {
          rsFindIdentityEventId.close();
        }
        SYSTEM_IDENTITIES.setPrimaryKey(new Field[]{});
        system = system == null ? SYSTEM_IDENTITIES : system;
        List<FieldValue> get = system.getEventValues().get(field);
        if ((get == null) || (get.isEmpty())) {
          ValueType type = ValueType.getType(field.getType());
          FieldValue start = new FieldValue(field);
          switch (type) {
            case RealValue:
            case IntValue:
            case LongValue:
              if ((initValue != null) && (initValue instanceof Number)) {
                start.setValue(initValue);
              } else {
                start.setValue(1);
              }
              break;
            case StringValue:
              if (initValue != null) {
                start.setValue(initValue);
              } else {
                start.setValue("AAA000000001");
              }
              break;
            default:
              start = null;
          }
          if (start != null) {
            system.addValue(start);
            storeEvent(system);
          }
          return start;
        } else {
          FieldValue value = get.get(0);
          ValueType type = ValueType.getType(value.getType());
          switch (type) {
            case RealValue:
              value.setValue(((Number) value.getValue()).doubleValue() + 1);
              if ((initValue != null) && (initValue instanceof Number)) {
                if (((Number) initValue).doubleValue() >= ((Number) value.getValue()).doubleValue()) {
                  value.setValue(initValue);
                }
              }
              break;
            case IntValue:
            case LongValue:
              value.setValue(((Number) value.getValue()).longValue() + 1);
              if ((initValue != null) && (initValue instanceof Number)) {
                if (((Number) initValue).longValue() >= ((Number) value.getValue()).longValue()) {
                  value.setValue(initValue);
                }
              }
              break;
            case StringValue:
              value.setValue(StringValue.getNextSifra((String) value.getValue()));
              if (initValue != null) {
                if (initValue.toString().compareToIgnoreCase(value.getValue().toString()) >= 0) {
                  value.setValue(initValue);
                }
              }
              break;
            default:
              value = null;
          }
          if (value != null) {
            storeEvent(system);
          }
          return value;
        }
      } finally {
        lock.release();
      }
    } catch (InterruptedException ex) {
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
  }

  @Override
  public String getCreateTableSQL(String tableName, ResultSet rs) throws SQLException {
    ResultSetMetaData rsmd = rs.getMetaData();
    StringBuilder sb = new StringBuilder();
    sb.append("CREATE TABLE ").append(tableName).append(" (\n");
    sb.append("     [__RsID] bigint identity NOT NULL\n");
    for (int column = 1; column <= rsmd.getColumnCount(); column++) {
      //sb.append("    ").append(column == 1 ? ' ' : ',');
      sb.append("    ,");
      sb.append('[').append(rsmd.getColumnName(column)).append("] ");
      String columnTypeName = rsmd.getColumnTypeName(column);


      //MS SQL-fix
      boolean scaled = true;
      if (columnTypeName.equals("timestamp")) {
        if (rsmd.isNullable(column) == ResultSetMetaData.columnNoNulls) {
          columnTypeName = "binary";
        } else if (rsmd.isNullable(column) == ResultSetMetaData.columnNullable) {
          columnTypeName = "varbinary";
        }
      } else if (columnTypeName.indexOf(" identity") != -1) {
        columnTypeName = columnTypeName.replace(" identity", "");
      } else if (columnTypeName.equals("text")) {
        columnTypeName = "varchar(max)";
        scaled = false;
      }

      if (columnTypeName.equals("datetime")
              || columnTypeName.equals("int")
              || columnTypeName.equals("bigint")) {
        scaled = false;
      }
      sb.append(columnTypeName);


      if (scaled) {
        final int precision = rsmd.getPrecision(column);
        final int scale = rsmd.getScale(column);

        if (precision > 0) {
          sb.append('(').append(precision);
          if (scale > 0) {
            sb.append(',').append(scale);
          }
          sb.append(")");
        }
      }

      if (rsmd.isNullable(column) == ResultSetMetaData.columnNoNulls) {
        sb.append(" NOT NULL");
      } else if (rsmd.isNullable(column) == ResultSetMetaData.columnNullable) {
        sb.append(" NULL");
      }
      sb.append('\n');
    }
    sb.append("    ,CONSTRAINT [<%PK%>_<%TS%>] PRIMARY KEY CLUSTERED\n").append(" (\n").append("   [ID] ASC\n").append(")\n");
    sb.append(")");

    return sb.toString();
  }

  @Override
  public Map<CaseInsensitiveString, Field> getPreparedFields() throws SQLException {
    Map<CaseInsensitiveString, Field> result = new HashMap<CaseInsensitiveString, Field>();
    if (preparedFields == null) {
      if (get_fields == null) {
        get_fields = ConnectionManager.getInstance().getConnection().prepareStatement(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "get_fields.sql", "cp1250"));
      }

      ResultSet fields = get_fields.executeQuery();
      try {
        while (fields.next()) {
          CaseInsensitiveString key = new CaseInsensitiveString(fields.getString("ImePolja"));
          Field field = new Field(fields.getInt("Id"), key.toString(), ValueType.valueOf(fields.getInt("TipPolja")).getSqlType(), -1);

          result.put(key, field);
        }
      } finally {
        fields.close();
      }

      if (Boolean.parseBoolean(ConnectionManager.getInstance().getProperty(DbConnection.DB_CACHEFIELDS, "true"))) {
        this.preparedFields = result;
      }
    } else {
      result.putAll(preparedFields);
    }

    return result;
  }

  private static class NamedFieldIds {

    CaseInsensitiveString fieldName;
    Integer fieldId;

    public NamedFieldIds(String fieldName, Integer fieldId) {
      this.fieldName = CaseInsensitiveString.valueOf(fieldName);
      this.fieldId = fieldId;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final NamedFieldIds other = (NamedFieldIds) obj;
      if (this.fieldName != other.fieldName && (this.fieldName == null || !this.fieldName.equals(other.fieldName))) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 61 * hash + (this.fieldName != null ? this.fieldName.hashCode() : 0);
      return hash;
    }
  }

  private int prepareSearchParameters(List parameters, Map<Field, DbDataSource.SqlParameter<Object>> namedParameters, Event event, Set<Field> searchFields, Set<Field> resultFields, int sifrant, String[] sifra, boolean validOnly, boolean lastEntryOnly, EventPK eventPK) {
    StringBuilder sbSearch = new StringBuilder(500);
    StringBuilder sbWhere = new StringBuilder(500);
    StringBuilder sbresult = new StringBuilder(500);
    EventLimit sqlResultLimit = new EventLimit("<%ev_result_limit%>");
    parameters.add(sqlResultLimit);
    sqlResultLimit.setValue(lastEntryOnly ? " TOP 1 " : "  ");
    DbDataSource.SubstSqlParameter sqlResultFields = new DbDataSource.SubstSqlParameter("<%ev_field_results%>");
    parameters.add(sqlResultFields);
    EventFilterSearch eventSearchFilter = new EventFilterSearch(namedParameters, searchFields.contains(Event.EVENT_SOURCE) ? event.getEventSource() : null, searchFields.contains(Event.EVENT_DATE) ? event.getDatum() : null, sifrant, sifra, validOnly, eventPK);
    parameters.add(eventSearchFilter);
    DbDataSource.SubstSqlParameter sqlFind = new DbDataSource.SubstSqlParameter("<%ev_values_filter%>");
    parameters.add(sqlFind);
    DbDataSource.SubstSqlParameter sqlValidOnly = new DbDataSource.SubstSqlParameter("<%ev_valid_filter%>");
    parameters.add(sqlValidOnly);
//    if (validOnly) {
//      sqlValidOnly.setValue("WHERE (ev.[validTo] IS NULL OR ev.[ValidTo] >= GETDATE())");
//    } else {
    sqlValidOnly.setValue("");
//    }

    int valuesSet = 0;
    Map<NamedFieldIds, NamedFieldIds> fieldNames = new java.util.HashMap<NamedFieldIds, NamedFieldIds>();
    try {
      if (sifra == null) {
        fieldNames = getEventFields(sifrant, null);
      } else {
        for (String s : sifra) {
          fieldNames.putAll(getEventFields(sifrant, s));
        }
      }

    } catch (SQLException ex) {
      fieldNames = new java.util.HashMap<NamedFieldIds, NamedFieldIds>();
      Logger.getLogger(SqlUtilitesImpl.class.getName()).log(Level.SEVERE, null, ex);
    }

    final String eventValues = SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB) + ".[dbo].[EventValues]";
    final String sifrantVnosnihPolj = SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB) + ".[dbo].[SifrantVnosnihPolj]";
    final String variousValues = SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB) + ".[dbo].[VariousValues]";

    String searchTemplate = "EXISTS (SELECT [VariousValues].Id FROM <%VARIOUS_VALUES%> WITH (NOLOCK) "
            + "WHERE [VariousValues].[Id] = "
            + "(SELECT [ValueId] FROM <%EVENT_VALUES%> WITH (NOLOCK) "
            + "WHERE [EventValues].[EventId] = ev.[Id] AND"
            + " [EventValues].[IdPolja] = {1} AND"
            + " [EventValues].[FieldValueIndex] = {2})"
            + "AND [VariousValues].{0} = {3} )";
    searchTemplate = searchTemplate.replaceAll("<%VARIOUS_VALUES%>", variousValues);
    searchTemplate = searchTemplate.replaceAll("<%EVENT_VALUES%>", eventValues);


    MessageFormat searchFormat = new MessageFormat(searchTemplate);


    for (Field f : searchFields) {
      if (!(Event.EVENT_SOURCE.equals(f)
              || Event.EVENT_DATE.equals(f))) {
        valuesSet++;

        if (resultFields.contains(f)) {

          final String ev_alias = "[ev_" + f.getName() + "]";
          final String vp_alias = "[vp_" + f.getName() + "]";
          final String val_alias = "[val_" + f.getName() + "]";
          sbSearch.append("\nLEFT OUTER JOIN ").append(eventValues).append(" ").append(ev_alias).append(" WITH (NOLOCK) ON (");
          sbSearch.append("ev.[Id] = ").append(ev_alias).append(".[EventId]");
          NamedFieldIds fn = new NamedFieldIds(f.getName(), Integer.MIN_VALUE);
          if (fieldNames.containsKey(fn)) {
            sbSearch.append(" AND ").append(ev_alias).append(".[IdPolja] = ").append(fieldNames.get(fn).fieldId);
            sbSearch.append(" AND ").append(ev_alias).append(".[FieldValueIndex] = ").append(f.getFieldIndex()).append(" )");
          } else {
            sbSearch.append(" AND ").append(ev_alias).append(".[FieldValueIndex] = ").append(f.getFieldIndex());
            sbSearch.append(") ");
            sbSearch.append("\nLEFT OUTER JOIN ").append(sifrantVnosnihPolj).append(" ").append(vp_alias).append(" WITH (NOLOCK) ON (");
            sbSearch.append(ev_alias).append(".[IdPolja] = ").append(vp_alias).append(".[Id]");
            sbSearch.append(" AND ").append(vp_alias).append(".ImePolja= '").append(f.getName()).append("' )");
          }
          sbSearch.append("\nINNER JOIN ").append(variousValues).append(" ").append(val_alias).append(" WITH (NOLOCK) ON (");
          sbSearch.append(ev_alias).append(".[ValueId] = ").append(val_alias).append(".[Id]");
          List<FieldValue> values;
          if (event.getEventValues().containsKey(f)) {
            values = event.getEventValues().get(f);
          } else {
            values = new java.util.ArrayList<FieldValue>();
            values.add(new FieldValue(f));
          }
//        if (values != null) {
          sbSearch.append(" AND (");
          boolean first = true;

          StringBuilder join = new StringBuilder();

          for (FieldValue fv : values) {
            int tipPolja = fv.getValueType().getTypeIndex();
            String value = null;
            if (first) {
              first = false;
            } else {
              sbSearch.append(" OR ");
            }
            switch (tipPolja) {
              case 1:
              case 11:
                //Integer //Long
                value = val_alias + ".IntValue";
                sbSearch.append(val_alias).append(".IntValue = ? ");
                if (resultFields.contains(f)) {
                  sbresult.append(",\n").append(val_alias).append(".IntValue AS [").append(f.getName()).append("]");
                }
                break;
              case 2:
                //Real
                value = val_alias + ".RealValue";
                sbSearch.append(val_alias).append(".RealValue = ? ");
                if (resultFields.contains(f)) {
                  sbresult.append(",\n").append(val_alias).append(".RealValue AS [").append(f.getName()).append("]");
                }
                break;
              case 3:
                //String
                value = val_alias + ".StringValue";
                if (ConnectionManager.getInstance().isConvertToVarchar()) {
                  sbSearch.append(val_alias).append(".StringValue = CAST(? AS VARCHAR)");
                } else {
                  sbSearch.append(val_alias).append(".StringValue = ?");
                }
                if (resultFields.contains(f)) {
                  sbresult.append(",\n").append(val_alias).append(".StringValue AS [").append(f.getName()).append("]");
                }
                break;
              case 4:
              case 8:
              case 9:
              case 10:
                //Date
                value = val_alias + ".DateValue";
                sbSearch.append(val_alias).append(".DateValue = ? ");
                if (resultFields.contains(f)) {
                  sbresult.append(",\n").append(val_alias).append(".DateValue AS [").append(f.getName()).append("]");
                }
                break;
              case 6:
                //Clob
                value = val_alias + ".ClobValue";
                sbSearch.append(val_alias).append(".ClobValue = ? ");
                if (resultFields.contains(f)) {
                  sbresult.append(",\n").append(val_alias).append(".ClobValue AS [").append(f.getName()).append("]");
                }
                break;
              case 7:
                //Boolean
                value = val_alias + ".IntValue";
                sbSearch.append(val_alias).append(".IntValue = ? ");
                if (resultFields.contains(f)) {
                  sbresult.append(",\n").append("CAST(").append(val_alias).append(".IntValue AS BIT) AS [").append(f.getName()).append("]");
                }
            }
            if (resultFields.contains(f)
                    && (f.getModel().getQuery() != null)) {
              if (f.getModel().getQuery().getSelect() != null) {
                for (String sql : f.getModel().getQuery().getSelect().getSQL()) {
                  sql = sql.replaceAll("<%ChangeLog%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB));
                  sql = sql.replaceAll("<%RPP%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPP_DB, SqlUtilities.RPP_DB));
                  sql = sql.replaceAll("<%RPE%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPE_DB, SqlUtilities.RPE_DB));
                  sql = sql.replaceAll(f.getModel().getReplace(), value);

                  sbresult.append(",\n").append(sql);
                }
              }
              if (f.getModel().getQuery().getJoin() != null) {
                for (String sql : f.getModel().getQuery().getJoin().getSQL()) {
                  sql = sql.replaceAll("<%ChangeLog%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB));
                  sql = sql.replaceAll("<%RPP%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPP_DB, SqlUtilities.RPP_DB));
                  sql = sql.replaceAll("<%RPE%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPE_DB, SqlUtilities.RPE_DB));
                  sql = sql.replaceAll(f.getModel().getReplace(), value);

                  join.append("\nLEFT OUTER JOIN ").append(sql);
                }
              }
            }
            DbDataSource.SqlParameter<Object> parameter = new DbDataSource.SqlParameter<Object>();
            parameter.setType(fv.getType());
            parameter.setValue(fv.getValue());
            parameters.add(parameter);
            namedParameters.put(f, parameter);
          }
          sbSearch.append(")");
//        }
          sbSearch.append(") ");
          sbSearch.append(join);
        } else {
          //dodaj v where brez joina
          StringBuilder qIdPolja = new StringBuilder(500);

          if (f.getIdPolja() != null && f.getIdPolja() > 0) {
            qIdPolja.append(f.getIdPolja().intValue());
          } else {
            NamedFieldIds fn = new NamedFieldIds(f.getName(), Integer.MIN_VALUE);
            if (fieldNames.containsKey(fn)) {
              qIdPolja.append(fieldNames.get(fn).fieldId);
            } else {
              qIdPolja.append("(SELECT [Id] FROM ").
                      append(sifrantVnosnihPolj).
                      append(" WITH (NOLOCK) WHERE [SifrantVnosnihPolj].ImePolja= '").append(f.getName()).append("' )");
            }
          }
          String valueColumn = "";
          String searchParameter = "?";
          ValueType valueType = ValueType.getType(f.getType());
          switch (valueType) {
            case IntValue:
            case LongValue:
            case BitValue:
              valueColumn = "IntValue";
              break;
            case RealValue:
              //Real
              valueColumn = "RealValue";
              break;
            case StringValue:
              //String
              valueColumn = "StringValue";
              if (ConnectionManager.getInstance().isConvertToVarchar()) {
                searchParameter = "CAST(? AS VARCHAR)";
              }
              break;
            case DateValue:
            case TimeValue:
            case MonthValue:
            case DateTimeValue:
              //Date
              valueColumn = "DateValue";
              break;
            case ClobValue:
              //Clob
              valueColumn = "ClobValue";
              break;
            case ObjectValue:
            case BlobValue:
              valueColumn = "ObjectValue";
              break;
          }
          sbWhere.append(sbWhere.length() > 0 ? " AND " : " WHERE ");
          sbWhere.append("\n");
          sbWhere.append(searchFormat.format(new Object[]{
                    valueColumn,
                    qIdPolja,
                    f.getFieldIndex(),
                    searchParameter
                  }));
          sbWhere.append("\n");

          DbDataSource.SqlParameter<Object> parameter = new DbDataSource.SqlParameter<Object>();
          parameter.setType(f.getType());
          //parameter.setValue(fv.getValue()); //null
          parameters.add(parameter);
          namedParameters.put(f, parameter);
        }


      } else if (Event.EVENT_SOURCE.equals(f)
              || Event.EVENT_DATE.equals(f)) {
        valuesSet++;
      }
    }
    sbSearch.insert(sbSearch.length(), sbWhere.toString());


    String template = "(SELECT [VariousValues].{0} FROM <%VARIOUS_VALUES%> WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM <%EVENT_VALUES%> WITH (NOLOCK) "
            + "WHERE [EventValues].[EventId] = ev.[Id] AND"
            + " [EventValues].[IdPolja] = {1} AND"
            + " [EventValues].[FieldValueIndex] = {2}))";
    template = template.replaceAll("<%VARIOUS_VALUES%>", variousValues);
    template = template.replaceAll("<%EVENT_VALUES%>", eventValues);

    MessageFormat resultFormat = new MessageFormat(template);

    for (Field f : resultFields) {
      if (!searchFields.contains(f)) {
        String fieldValueIndex = f.getFieldIndex() > 1 ? Integer.toString(f.getFieldIndex()) : "";
        if (f.getName().endsWith(fieldValueIndex)) {
          fieldValueIndex = "";
        }

        final String ev_alias = "[ev_" + f.getName() + fieldValueIndex + "]";
        final String vp_alias = "[vp_" + f.getName() + fieldValueIndex + "]";
        final String val_alias = "[val_" + f.getName() + fieldValueIndex + "]";

        if (f.getModel() == null
                || f.getModel().getQuery() == null) {

          StringBuilder qIdPolja = new StringBuilder(500);

          NamedFieldIds fn = new NamedFieldIds(f.getName(), Integer.MIN_VALUE);
          //TODO to ni uredu, ker ne da pravilnega rezultata, ce ne iscem po id polja
          if (fieldNames.containsKey(fn) || f.getIdPolja() != null) {
            qIdPolja.append(fieldNames.get(fn) != null ? fieldNames.get(fn).fieldId : f.getIdPolja().intValue());
          } else {
            qIdPolja.append("(SELECT [Id] FROM ").
                    append(sifrantVnosnihPolj).
                    append(" WITH (NOLOCK) WHERE [SifrantVnosnihPolj].ImePolja= '").append(f.getName()).append("' )");
          }

          int tipPolja = ValueType.getType(f.getType()).getTypeIndex();
          switch (tipPolja) {
            case 1:
              sbresult.append(",\n").append(resultFormat.format(new Object[]{
                        "IntValue",
                        qIdPolja,
                        f.getFieldIndex()
                      })).append(" AS [").append(f.getName() + fieldValueIndex).append("]");
              break;
            case 2:
              //Real
              sbresult.append(",\n").append(resultFormat.format(new Object[]{
                        "RealValue",
                        qIdPolja,
                        f.getFieldIndex()
                      })).append(" AS [").append(f.getName() + fieldValueIndex).append("]");
              break;
            case 3:
              //String
              sbresult.append(",\n").append(resultFormat.format(new Object[]{
                        "StringValue",
                        qIdPolja,
                        f.getFieldIndex()
                      })).append(" AS [").append(f.getName() + fieldValueIndex).append("]");
              break;
            case 4:
            case 8:
            case 9:
            case 10:
              //Date
              sbresult.append(",\n").append(resultFormat.format(new Object[]{
                        "DateValue",
                        qIdPolja,
                        f.getFieldIndex()
                      })).append(" AS [").append(f.getName() + fieldValueIndex).append("]");
              break;
            case 6:
              //Clob
              sbresult.append(",\n").append(resultFormat.format(new Object[]{
                        "ClobValue",
                        qIdPolja,
                        f.getFieldIndex()
                      })).append(" AS [").append(f.getName() + fieldValueIndex).append("]");
              break;
            case 7:
              //Boolean
              sbresult.append(",\n").append("CAST(").append(resultFormat.format(new Object[]{
                        "IntValue",
                        qIdPolja,
                        f.getFieldIndex()
                      })).append(" AS BIT) AS [").append(f.getName() + fieldValueIndex).append("]");
          }
        } else {
          sbSearch.append("\nLEFT OUTER JOIN ").append(eventValues).append(" ").append(ev_alias).append(" WITH (NOLOCK) ON (");
          sbSearch.append("ev.[Id] = ").append(ev_alias).append(".[EventId]");
          NamedFieldIds fn = new NamedFieldIds(f.getName(), Integer.MIN_VALUE);
          //TODO to ni uredu, ker ne da pravilnega rezultata, ce ne iscem po id polja
          if (fieldNames.containsKey(fn) || f.getIdPolja() != null) {
            long idPolja = fieldNames.get(fn) != null ? fieldNames.get(fn).fieldId : f.getIdPolja().intValue();
            sbSearch.append(" AND ").append(ev_alias).append(".[IdPolja] = ").append(idPolja);
            sbSearch.append(" AND ").append(ev_alias).append(".[FieldValueIndex] = ").append(f.getFieldIndex()).append(" )");
          } else {
            sbSearch.append(" AND ").append(ev_alias).append(".[FieldValueIndex] = ").append(f.getFieldIndex());
            sbSearch.append(") ");
            sbSearch.append("\nLEFT OUTER JOIN ").append(sifrantVnosnihPolj).append(" ").append(vp_alias).append(" WITH (NOLOCK) ON (");
            sbSearch.append(ev_alias).append(".[IdPolja] = ").append(vp_alias).append(".[Id]");
            sbSearch.append(" AND ").append(vp_alias).append(".ImePolja= '").append(f.getName()).append("' )");
          }
          sbSearch.append("\nLEFT OUTER JOIN ").append(variousValues).append(" ").append(val_alias).append(" WITH (NOLOCK) ON (");
          sbSearch.append(ev_alias).append(".[ValueId] = ").append(val_alias).append(".[Id] )");

          int tipPolja = ValueType.getType(f.getType()).getTypeIndex();
          String value = null;
          switch (tipPolja) {
            case 1:
              value = val_alias + ".IntValue";
              sbresult.append(",\n").append(val_alias).append(".IntValue AS [").append(f.getName() + fieldValueIndex).append("]");
              break;
            case 2:
              //Real
              value = val_alias + ".RealValue";
              sbresult.append(",\n").append(val_alias).append(".RealValue AS [").append(f.getName() + fieldValueIndex).append("]");
              break;
            case 3:
              //String
              value = val_alias + ".StringValue";
              sbresult.append(",\n").append(val_alias).append(".StringValue AS [").append(f.getName() + fieldValueIndex).append("]");
              break;
            case 4:
            case 8:
            case 9:
            case 10:
              //Date
              value = val_alias + ".DateValue";
              sbresult.append(",\n").append(val_alias).append(".DateValue AS [").append(f.getName() + fieldValueIndex).append("]");
              break;
            case 6:
              //Clob
              value = val_alias + ".ClobValue";
              sbresult.append(",\n").append(val_alias).append(".ClobValue AS [").append(f.getName() + fieldValueIndex).append("]");
              break;
            case 7:
              //Boolean
              value = val_alias + ".IntValue";
              sbresult.append(",\n").append("CAST(").append(val_alias).append(".IntValue AS BIT) AS [").append(f.getName() + fieldValueIndex).append("]");
          }

          if (f.getModel().getQuery().getSelect() != null) {
            for (String sql : f.getModel().getQuery().getSelect().getSQL()) {
              sql = sql.replaceAll("<%ChangeLog%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB));
              sql = sql.replaceAll("<%RPP%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPP_DB, SqlUtilities.RPP_DB));
              sql = sql.replaceAll("<%RPE%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPE_DB, SqlUtilities.RPE_DB));
              sql = sql.replaceAll(f.getModel().getReplace(), value);

              sbresult.append(",\n").append(sql);
            }
          }
          if (f.getModel().getQuery().getJoin() != null) {
            for (String sql : f.getModel().getQuery().getJoin().getSQL()) {
              sql = sql.replaceAll("<%ChangeLog%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB));
              sql = sql.replaceAll("<%RPP%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPP_DB, SqlUtilities.RPP_DB));
              sql = sql.replaceAll("<%RPE%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPE_DB, SqlUtilities.RPE_DB));
              sql = sql.replaceAll(f.getModel().getReplace(), value);

              sbSearch.append("\nLEFT OUTER JOIN ").append(sql);
            }
          }
        }
      }
    }
    sqlFind.setValue(sbSearch.toString());
    sqlResultFields.setValue(sbresult.toString());


    return valuesSet;
  }

  private static String getFindEventSQL() {
    return com.openitech.io.ReadInputStream.getResourceAsString(SqlUtilitesImpl.class, "find_event_by_values.sql", "cp1250");
  }

  private static String getCheckVecVrednostiEventSQL() {
    return com.openitech.io.ReadInputStream.getResourceAsString(SqlUtilitesImpl.class, "event_vecVrednosti.sql", "cp1250");
  }

  @Override
  public EventQuery prepareEventQuery(Event parent, Set<Field> searchFields, Set<Field> resultFields, int sifrant, String[] sifra, boolean validOnly, boolean lastEntryOnly) {
    EventQueryImpl result = new EventQueryImpl(parent);

    result.sifrant = sifrant;
    result.sifra = sifra;
    result.resultFields = Collections.unmodifiableSet(resultFields);
    result.searchFields = Collections.unmodifiableSet(searchFields);
    result.eventPK = null;
//    if (parent.getPrimaryKey() == null) {
//      result.searchByEventPK = false;
//    } else {
//      result.searchByEventPK = searchFields.containsAll(Arrays.asList(parent.getPrimaryKey()));
//      if (result.searchByEventPK) {
//        result.searchByEventPK = getSearchByEventPK(sifrant, sifra);
//      }
////      if (result.searchByEventPK) {
////        result.eventPK = new EventPK();
////      }
//    }
    result.valuesSet = prepareSearchParameters(result.parameters, result.namedParameters, parent, searchFields, resultFields, sifrant, sifra, validOnly, lastEntryOnly, result.eventPK);

    return result;
  }

  public static class EventQueryImpl implements EventQuery {

    private Event parent;
    private EventPK eventPK;
    private int valuesSet = 0;
    private List<Object> parameters = new ArrayList<Object>();
    private Map<Field, DbDataSource.SqlParameter<Object>> namedParameters = new HashMap<Field, DbDataSource.SqlParameter<Object>>();
    private String query = getFindEventSQL();
    private int sifrant;
    private String[] sifra;
    private Set<Field> searchFields;
    private Set<Field> resultFields;

    public EventQueryImpl(Event parent) {
      this.parent = parent;
    }

    /**
     * Get the value of valuesSet
     *
     * @return the value of valuesSet
     */
    @Override
    public int getValuesSet() {
      return valuesSet;
    }

    /**
     * Get the value of sifra
     *
     * @return the value of sifra
     */
    @Override
    public String[] getSifra() {
      return sifra;
    }

    /**
     * Get the value of sifrant
     *
     * @return the value of sifrant
     */
    @Override
    public int getSifrant() {
      return sifrant;
    }

    /**
     * Get the value of query
     *
     * @return the value of query
     */
    @Override
    public String getQuery() {
      return query;
    }

    /**
     * Get the value of event
     *
     * @return the value of event
     */
    public Event getParent() {
      return parent;
    }

    /**
     * Get the value of parameters
     *
     * @return the value of parameters
     */
    @Override
    public List<Object> getParameters() {
      return Collections.unmodifiableList(parameters);
    }

    /**
     * Get the value of namedParameters
     *
     * @return the value of namedParameters
     */
    @Override
    public Map<Field, SqlParameter<Object>> getNamedParameters() {
      return namedParameters;
    }

    /**
     * Get the value of resultFields
     *
     * @return the value of resultFields
     */
    @Override
    public Set<Field> getResultFields() {
      return resultFields;
    }

    /**
     * Get the value of searchFields
     *
     * @return the value of searchFields
     */
    @Override
    public Set<Field> getSearchFields() {
      return searchFields;
    }
    protected boolean searchByEventPK;

    /**
     * Get the value of searchByEventPK
     *
     * @return the value of searchByEventPK
     */
    @Override
    public boolean isSearchByEventPK() {
      return searchByEventPK;
    }
  }
}
