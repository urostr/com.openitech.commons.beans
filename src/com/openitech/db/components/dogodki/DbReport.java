/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.components.dogodki;

import com.openitech.db.filters.DataSourceFiltersMap;
import com.openitech.db.filters.DataSourceLimit;
import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.factory.DataSourceConfig;
import com.openitech.db.model.factory.DataSourceFactory;
import com.openitech.db.model.factory.JaxbUnmarshaller;
import com.openitech.db.model.xml.config.QueryParameter;
import com.openitech.db.model.xml.config.Workarea;
import com.openitech.db.model.xml.config.Workarea.DataSource;
import com.openitech.events.concurrent.DataSourceEvent;
import com.openitech.sql.SQLWorker;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.Document;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author domenbasic
 */
public class DbReport {

  protected JasperReport jasperReport;
  protected String name;
  protected byte[] fileBytes;
  protected Blob serializedBlob;
  protected String xmlParameters;
  protected Workarea workArea;
  protected SQLWorker sqlWorker = new SQLWorker();
  private DataSourceFactory factory;
  DataSourceConfig config;

  public DbReport(DataSourceConfig config, String name, byte[] fileBytes, Blob serializedBlob, String xmlParameters) throws Exception {
    this.name = name;
    this.fileBytes = fileBytes;
    this.serializedBlob = serializedBlob;
    this.xmlParameters = xmlParameters;

    ObjectInputStream oin = new ObjectInputStream((serializedBlob).getBinaryStream());
    jasperReport = (JasperReport) oin.readObject();

    if(xmlParameters != null && xmlParameters.length() > 0){
      workArea = (Workarea) JaxbUnmarshaller.getInstance().unmarshall(Workarea.class, xmlParameters);
    }
    if (workArea != null) {
      final DbDataModel dbDataModel = config != null ? config.getDataModel() : new DbDataModel() {

        @Override
        public Map<String, Document> getDocuments() {
          return new HashMap<String, Document>();
        }
      };
      factory = new DataSourceFactory(dbDataModel);
      factory.configure("Report", workArea, config != null ? config : new DataSourceConfig(dbDataModel));
    }
  }

  public JasperReport getJasperReport() {
    return jasperReport;
  }

  public String getName() {
    return name;
  }

  public ResultSet getResultSet(DbDataSource sourceDataSource) throws SQLException {
    ResultSet result = null;
    if (workArea != null) {
      DataSource waDataSource = workArea.getDataSource();
      if (waDataSource != null) {
        String sql = waDataSource.getSQL();
        if (sql != null) {
          List parameters = new ArrayList();
          List<String> eventColumns = waDataSource.getEventColumns();
          for (String eventColumn : eventColumns) {
            parameters.add(sourceDataSource.getObject(eventColumn));
          }
          List<QueryParameter> queryParameters = waDataSource.getParameters();
          if(queryParameters != null){
            for (QueryParameter queryParameter : queryParameters) {
              parameters.add(sourceDataSource.getObject(queryParameter.getNamedParameter()));
            }
          }

          result = sqlWorker.executeQuery(sql, parameters);
        }
      }
    }
    if (result == null) {
      for (Object param : sourceDataSource.getParameters()) {
        if (param instanceof DataSourceLimit) {
          final DataSourceLimit limitParam = (DataSourceLimit) param;
          if (!limitParam.getValue().equals(DataSourceLimit.Limit.LALL)) {
            DataSourceEvent.suspend(sourceDataSource);
            limitParam.setValue(DataSourceLimit.Limit.LALL);
            DataSourceEvent.cancel(sourceDataSource);
            sourceDataSource.reload(false);
            DataSourceEvent.resume(sourceDataSource);
          }
        }
      }

      result = sourceDataSource.getResultSet();
    }

    return result;
  }

  public boolean hasParameters() {
    if (factory != null) {
      DataSourceFiltersMap filtersMap = factory.getFiltersMap();
      if (filtersMap != null) {
        return filtersMap.size() > 0;
      }
    }
    return false;
  }

  public DataSourceFactory getFactory() {
    return factory;
  }
}
