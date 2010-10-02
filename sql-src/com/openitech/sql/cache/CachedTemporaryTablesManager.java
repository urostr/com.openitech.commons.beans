/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.cache;

import com.openitech.db.model.factory.DataSourceConfig;
import com.openitech.db.model.factory.DataSourceParametersFactory;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.db.model.xml.config.TemporaryTable;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.sql.util.mssql.SqlUtilitesImpl;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author uros
 */
public class CachedTemporaryTablesManager extends DataSourceParametersFactory<DataSourceConfig> {

  private static CachedTemporaryTablesManager instance;

  private CachedTemporaryTablesManager() {
    cachedTemporaryTables = SqlUtilities.getInstance().getCachedTemporaryTables();
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

  public TemporarySubselectSqlParameter getCachedTemporarySubselectSqlParameter(Class clazz, String resource) throws JAXBException {
    Reader is;
    try {
      is = new LineNumberReader(new InputStreamReader(clazz.getResourceAsStream(resource), "UTF-8"));
    } catch (UnsupportedEncodingException ex) {
      is = new LineNumberReader(new InputStreamReader(clazz.getResourceAsStream(resource)));
    }
    JAXBContext ctx = JAXBContext.newInstance(com.openitech.db.model.xml.config.CachedTemporaryTable.class);
    Unmarshaller um = ctx.createUnmarshaller();

    com.openitech.db.model.xml.config.CachedTemporaryTable ctt = (com.openitech.db.model.xml.config.CachedTemporaryTable) um.unmarshal(is);

    return getCachedTemporarySubselectSqlParameter(ctt.getTemporaryTable());

  }
}
