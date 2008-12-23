/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.sql.util.mssql;

import com.openitech.db.ConnectionManager;
import com.openitech.sql.util.SqlUtilities;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author uros
 */
public class SqlUtilitesImpl extends SqlUtilities {
  @Override
  public long getLastSessionIdentity() throws SQLException {
    Statement statement = ConnectionManager.getInstance().getConnection().createStatement();

    ResultSet result = statement.executeQuery("SELECT SCOPE_IDENTITY()"); result.next();

    return result.getLong(1);
  }

}
