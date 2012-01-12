/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.components.dogodki;

import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.factory.DataSourceFactory;
import com.openitech.db.model.factory.JaxbUnmarshaller;
import com.openitech.db.model.xml.config.Workarea;
import com.openitech.db.model.xml.config.Workarea.DataSource;
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

  public DbReport(String name, byte[] fileBytes, Blob serializedBlob, String xmlParameters) throws Exception {
    this.name = name;
    this.fileBytes = fileBytes;
    this.serializedBlob = serializedBlob;
    this.xmlParameters = xmlParameters;

    ObjectInputStream oin = new ObjectInputStream((serializedBlob).getBinaryStream());
    jasperReport = (JasperReport) oin.readObject();

    new DataSourceFactory(new DbDataModel() {

      @Override
      public Map<String, Document> getDocuments() {
        return new HashMap<String, Document>();
      }
    }).configure("Report", xmlParameters);
    workArea = (Workarea) JaxbUnmarshaller.getInstance().unmarshall(Workarea.class, xmlParameters);
  }

  public JasperReport getJasperReport() {
    return jasperReport;
  }

  public String getName() {
    return name;
  }

  public ResultSet getResultSet(DbDataSource sourceDataSource) throws SQLException {
    ResultSet result = null;

    DataSource waDataSource = workArea.getDataSource();
    if (waDataSource != null) {
      String sql = waDataSource.getSQL();
      if (sql != null) {
        List parameters = new ArrayList();
        List<String> eventColumns = waDataSource.getEventColumns();
        for (String eventCoulmn : eventColumns) {
          parameters.add(sourceDataSource.getObject(eventCoulmn));
        }

        result = sqlWorker.executeQuery(sql, parameters);
      }
    }

    return result;
  }
}
