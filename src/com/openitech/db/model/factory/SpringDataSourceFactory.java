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
  public void configure(final AbstractDataSourceFactory factory, final String opis, com.openitech.db.model.factory.DataSourceConfig config, com.openitech.db.model.xml.config.Workarea dataSourceXML) throws SQLException, ClassNotFoundException {
    factory.opis = opis;


    CreationParameters creationParameters = dataSourceXML.getDataSource().getCreationParameters();
    String className = null;
    String provider = null;
    if (creationParameters != null) {
      className = creationParameters.getClassName();
      provider = creationParameters.getProviderClassName();
    }
    final DbDataSource dataSource = className == null ? new DbDataSource("", "", SpringDataSource.class) : new DbDataSource("", "", (Class<? extends DbDataSourceImpl>) Class.forName(className));
    if (provider != null) {
      dataSource.setProvider(provider);
    }

    dataSource.lock();
    try {

      if (dataSourceXML.getDataSource().getCOUNTSQL() != null) {
        dataSource.setCountSql(getReplacedSql(dataSourceXML.getDataSource().getCOUNTSQL()));
      }
      dataSource.setSelectSql(getReplacedSql(dataSourceXML.getDataSource().getSQL()));
      dataSource.setName("DS:FACTORY:" + factory.getOpis());


      factory.dataSource = dataSource;

    } finally {
      dataSource.unlock();
    }
  }
}
