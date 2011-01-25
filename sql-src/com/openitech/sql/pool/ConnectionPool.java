/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.sql.pool;

import com.openitech.jdbc.proxy.ConnectionProxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;

/**
 *
 * @author domenbasic
 */
public class ConnectionPool {

  private final List<PooledConnectionProxy> connections = Collections.synchronizedList(new ArrayList<PooledConnectionProxy>());
  private final DataSource dataSource;

  public ConnectionPool(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Connection getConnection() throws SQLException {
    PooledConnectionProxy result = null;
    for (PooledConnectionProxy connectionProxy : connections) {
      if (connectionProxy.isClosed()) {
        result = connectionProxy;
        result.open();
        break;
      }
    }
    if (result==null) {
      result = new PooledConnectionProxy(dataSource);
      connections.add(result);
    }

    return result;
  }
}
