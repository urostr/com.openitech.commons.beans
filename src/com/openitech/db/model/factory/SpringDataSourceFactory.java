/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSourceFactory.DbDataSourceImpl;
import com.openitech.db.model.spring.SpringDataSource;
import com.openitech.db.model.xml.config.Workarea.DataSource.CreationParameters;
import java.sql.SQLException;

/**
 *
 * @author domenbasic
 */
public class SpringDataSourceFactory extends AbstractDataSourceFactory {

  public SpringDataSourceFactory() {
    super(null);
  }

  @Override
  public void configure() throws SQLException, ClassNotFoundException {
    CreationParameters creationParameters = dataSourceXML.getDataSource().getCreationParameters();
    String className = null;
    String provider = null;
    if (creationParameters != null) {
      className = creationParameters.getClassName();
      provider = creationParameters.getProviderClassName();
    }
    this.dataSource = className == null ? new DbDataSource("", "", SpringDataSource.class) : new DbDataSource("", "", (Class<? extends DbDataSourceImpl>) Class.forName(className));
    if (provider != null) {
      dataSource.setProvider(provider);
    }

    dataSource.lock();
    try {

      if (dataSourceXML.getDataSource().getCOUNTSQL() != null) {
        dataSource.setCountSql(getReplacedSql(dataSourceXML.getDataSource().getCOUNTSQL()));
      }
      dataSource.setSelectSql(getReplacedSql(dataSourceXML.getDataSource().getSQL()));
      dataSource.setName("DS:FACTORY:" + this.getOpis());

    } finally {
      dataSource.unlock();
    }
  }
}
