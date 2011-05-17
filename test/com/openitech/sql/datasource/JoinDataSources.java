/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.datasource;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.sql.SQLDataSource;
import com.openitech.io.ReadInputStream;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.sql.util.mssql.SqlUtilitesImpl.EventFilterSearch;
import com.openitech.value.fields.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.codehaus.groovy.syntax.ReadException;

/**
 *
 * @author domenbasic
 */
public class JoinDataSources extends TestCase {

  public JoinDataSources(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  // TODO add test methods here. The name must begin with 'test'. For example:
  public void ctestDvaDataSourca() throws SQLException {
    DbDataSource.DUMP_SQL = true;
    DbConnection.register();

    DbDataSource dataSource1 = new DbDataSource();
    dataSource1.setName("dataSource1");
    EventFilterSearch eventFilterSearch = new EventFilterSearch(new HashMap<Field, DbDataSource.SqlParameter<Object>>(), null, null, 297, null, true, null);
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(eventFilterSearch);
    dataSource1.setParameters(parameters);
    dataSource1.setSelectSql(ReadInputStream.getResourceAsString(JoinDataSources.class, "sql/event.sql", "cp1250"));
    dataSource1.setCountSql(SQLDataSource.SELECT_1);
    dataSource1.loadData();

    DbDataSource dataSource2 = new DbDataSource();
    dataSource2.setName("dataSource2");
    EventFilterSearch eventFilterSearch2 = new EventFilterSearch(new HashMap<Field, DbDataSource.SqlParameter<Object>>(), null, null, 172, null, true, null);
    List<Object> parameters2 = new ArrayList<Object>();
    parameters2.add(eventFilterSearch2);
    dataSource2.setParameters(parameters2);
    dataSource2.setSelectSql(ReadInputStream.getResourceAsString(JoinDataSources.class, "sql/event.sql", "cp1250"));
    dataSource2.setCountSql(SQLDataSource.SELECT_1);
    dataSource2.loadData();

    List<DbDataSource> dataSources = new ArrayList<DbDataSource>();
    dataSources.add(dataSource1);
    dataSources.add(dataSource2);
    DbDataSource joinSecondaryDataSources = SqlUtilities.getInstance().joinSecondaryDataSources(dataSources);

    dataSource1.beforeFirst();
    while (dataSource1.next()) {
      String string = dataSource1.getString("EventId");
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(string);
    }

    dataSource2.beforeFirst();
    while (dataSource2.next()) {
      String string = dataSource2.getString("EventId");
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(string);
    }

  }

  public void aatestTrijedataSourci() throws SQLException {
    DbDataSource.DUMP_SQL = true;
    DbConnection.register();

    DbDataSource dataSource1 = new DbDataSource();
    dataSource1.setName("dataSource1");
    EventFilterSearch eventFilterSearch = new EventFilterSearch(new HashMap<Field, DbDataSource.SqlParameter<Object>>(), null, null, 297, null, true, null);
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(eventFilterSearch);
    dataSource1.setParameters(parameters);
    dataSource1.setSelectSql(ReadInputStream.getResourceAsString(JoinDataSources.class, "sql/event.sql", "cp1250"));
    dataSource1.setCountSql(SQLDataSource.SELECT_1);
    dataSource1.loadData();

    DbDataSource dataSource2 = new DbDataSource();
    dataSource2.setName("dataSource2");
    EventFilterSearch eventFilterSearch2 = new EventFilterSearch(new HashMap<Field, DbDataSource.SqlParameter<Object>>(), null, null, 172, null, true, null);
    List<Object> parameters2 = new ArrayList<Object>();
    parameters2.add(eventFilterSearch2);
    dataSource2.setParameters(parameters2);
    dataSource2.setSelectSql(ReadInputStream.getResourceAsString(JoinDataSources.class, "sql/event.sql", "cp1250"));
    dataSource2.setCountSql(SQLDataSource.SELECT_1);
    dataSource2.loadData();



    List<DbDataSource> dataSources = new ArrayList<DbDataSource>();
    dataSources.add(dataSource1);
    dataSources.add(dataSource2);
    DbDataSource joinSecondaryDataSources = SqlUtilities.getInstance().joinSecondaryDataSources(dataSources);
    joinSecondaryDataSources.reload();

    dataSource1.beforeFirst();
    while (dataSource1.next()) {
      String string = dataSource1.getString("EventId");
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(string);
    }

    dataSource2.beforeFirst();
    while (dataSource2.next()) {
      String string = dataSource2.getString("EventId");
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(string);
    }

    DbDataSource dataSource3 = new DbDataSource();
    dataSource3.setName("dataSource3");
    EventFilterSearch eventFilterSearch3 = new EventFilterSearch(new HashMap<Field, DbDataSource.SqlParameter<Object>>(), null, null, 270, null, true, null);
    List<Object> parameters3 = new ArrayList<Object>();
    parameters3.add(eventFilterSearch3);
    dataSource3.setParameters(parameters3);
    dataSource3.setSelectSql(ReadInputStream.getResourceAsString(JoinDataSources.class, "sql/event.sql", "cp1250"));
    dataSource3.setCountSql(SQLDataSource.SELECT_1);
    dataSource3.loadData();

    dataSources = new ArrayList<DbDataSource>();
    dataSources.add(dataSource1);
    dataSources.add(dataSource2);
    dataSources.add(dataSource3);
    dataSources.add(joinSecondaryDataSources);
    DbDataSource joinSecondaryDataSources2 = SqlUtilities.getInstance().joinSecondaryDataSources(dataSources);
    joinSecondaryDataSources2.reload();

    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("dataSource1");
    dataSource1.beforeFirst();
    while (dataSource1.next()) {
      String string = dataSource1.getString("EventId");
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(string);
    }

    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("dataSource2");

    dataSource2.beforeFirst();
    while (dataSource2.next()) {
      String string = dataSource2.getString("EventId");
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(string);
    }
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("dataSource3");

    dataSource3.beforeFirst();
    while (dataSource3.next()) {
      String string = dataSource3.getString("EventId");
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(string);
    }
  }

  public void testTrijedataSourciBrezNext() throws SQLException {
    DbDataSource.DUMP_SQL = true;
    DbConnection.register();

    DbDataSource dataSource1 = new DbDataSource();
    dataSource1.setName("dataSource1");
    EventFilterSearch eventFilterSearch = new EventFilterSearch(new HashMap<Field, DbDataSource.SqlParameter<Object>>(), null, null, 297, null, true, null);
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(eventFilterSearch);
    dataSource1.setParameters(parameters);
    dataSource1.setSelectSql(ReadInputStream.getResourceAsString(JoinDataSources.class, "sql/event.sql", "cp1250"));
    dataSource1.setCountSql(SQLDataSource.SELECT_1);
    dataSource1.loadData();

    DbDataSource dataSource2 = new DbDataSource();
    dataSource2.setName("dataSource2");
    EventFilterSearch eventFilterSearch2 = new EventFilterSearch(new HashMap<Field, DbDataSource.SqlParameter<Object>>(), null, null, 172, null, true, null);
    List<Object> parameters2 = new ArrayList<Object>();
    parameters2.add(eventFilterSearch2);
    dataSource2.setParameters(parameters2);
    dataSource2.setSelectSql(ReadInputStream.getResourceAsString(JoinDataSources.class, "sql/event.sql", "cp1250"));
    dataSource2.setCountSql(SQLDataSource.SELECT_1);
    dataSource2.loadData();



    List<DbDataSource> dataSources = new ArrayList<DbDataSource>();
    dataSources.add(dataSource1);
    dataSources.add(dataSource2);
    DbDataSource joinSecondaryDataSources = SqlUtilities.getInstance().joinSecondaryDataSources(dataSources);
    joinSecondaryDataSources.reload();

    dataSource1.beforeFirst();
    while (dataSource1.next()) {
      String string = dataSource1.getString("EventId");
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(string);
    }

    dataSource2.beforeFirst();
    while (dataSource2.next()) {
      String string = dataSource2.getString("EventId");
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(string);
    }

    DbDataSource dataSource3 = new DbDataSource();
    dataSource3.setName("dataSource3");
    EventFilterSearch eventFilterSearch3 = new EventFilterSearch(new HashMap<Field, DbDataSource.SqlParameter<Object>>(), null, null, 270, null, true, null);
    List<Object> parameters3 = new ArrayList<Object>();
    parameters3.add(eventFilterSearch3);
    dataSource3.setParameters(parameters3);
    dataSource3.setSelectSql(ReadInputStream.getResourceAsString(JoinDataSources.class, "sql/event.sql", "cp1250"));
    dataSource3.setCountSql(SQLDataSource.SELECT_1);
    dataSource3.loadData();

    dataSources = new ArrayList<DbDataSource>();
    dataSources.add(dataSource1);
    dataSources.add(dataSource2);
    dataSources.add(dataSource3);
    dataSources.add(joinSecondaryDataSources);
    DbDataSource joinSecondaryDataSources2 = SqlUtilities.getInstance().joinSecondaryDataSources(dataSources);
    joinSecondaryDataSources2.reload();

    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("dataSource1");
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(dataSource1.getString("EventId"));

    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("dataSource2");
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(dataSource2.getString("EventId"));

    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("dataSource3");
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(dataSource3.getString("EventId"));

  }
}
