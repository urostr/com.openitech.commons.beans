/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.cache;

import com.openitech.db.model.factory.DataSourceConfig;
import com.openitech.db.model.factory.DataSourceParametersFactory;
import com.openitech.db.model.factory.JaxbUnmarshaller;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.db.model.xml.config.TemporaryTable;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

/**
 *
 * @author uros
 */
public class CachedTemporaryTablesManager extends DataSourceParametersFactory<DataSourceConfig> {

  private static CachedTemporaryTablesManager instance;

  private CachedTemporaryTablesManager() {
//    cachedTemporaryTables = SqlUtilities.getInstance().getCachedTemporaryTables();
  }

  public static CachedTemporaryTablesManager getInstance() {
    if (instance == null) {
      instance = new CachedTemporaryTablesManager();
    }

    return instance;
  }

  public TemporarySubselectSqlParameter getCachedTemporarySubselectSqlParameter(TemporaryTable tt) {
    return createTemporaryTable(tt);
  }

  public TemporarySubselectSqlParameter getCachedTemporarySubselectSqlParameter(Class clazz, String resource) {
    Reader is;
    try {
      is = new LineNumberReader(new InputStreamReader(clazz.getResourceAsStream(resource), "UTF-8"));
    } catch (UnsupportedEncodingException ex) {
      is = new LineNumberReader(new InputStreamReader(clazz.getResourceAsStream(resource)));
    }
    try {
      com.openitech.db.model.xml.config.CachedTemporaryTable ctt = (com.openitech.db.model.xml.config.CachedTemporaryTable) JaxbUnmarshaller.getInstance().unmarshall(com.openitech.db.model.xml.config.CachedTemporaryTable.class, is);
      return getCachedTemporarySubselectSqlParameter(ctt.getTemporaryTable());
    } catch (JAXBException ex) {
      Logger.getLogger(CachedTemporaryTablesManager.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }

  }
}
