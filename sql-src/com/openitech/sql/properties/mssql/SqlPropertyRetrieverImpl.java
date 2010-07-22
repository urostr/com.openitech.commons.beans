/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.properties.mssql;

import com.openitech.db.ConnectionManager;
import com.openitech.spring.beans.factory.config.AbstractPropertyRetriever;
import com.openitech.spring.beans.factory.config.PropertyType;
import com.openitech.sql.ValueType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public final class SqlPropertyRetrieverImpl extends AbstractPropertyRetriever {

  PreparedStatement findProperty;
  PreparedStatement getProperty;

  @Override
  public Object getRemoteValue(PropertyType type, String properyName, String charsetName) {
    Object result = null;
    try {
      if (findProperty == null) {
        findProperty = ConnectionManager.getInstance().getConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(this.getClass(), "find_property.sql", "cp1250"));
      }
      if (getProperty == null) {
        getProperty = ConnectionManager.getInstance().getConnection().prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(this.getClass(), "get_property.sql", "cp1250"));
      }

      int param = 1;
      findProperty.setInt(param++, type.getContentType());
      findProperty.setString(param++, properyName);

      int count = 0;
      ResultSet rs = findProperty.executeQuery();
      try {
        if (rs.next()) {
          count = rs.getInt(1);
        }
      } finally {
        rs.close();
      }

      if (count > 0) {
        param = 1;
        getProperty.setInt(param++, type.getContentType());
        getProperty.setString(param++, properyName);

        rs = getProperty.executeQuery();
        try {
          if (rs.next()) {
            rs.getObject("PropertyValue");
            if (!rs.wasNull()) {
              ValueType fieldType = ValueType.valueOf(rs.getInt("FieldType"));

              switch (fieldType) {
                case BitValue:
                case IntValue:
                  result = rs.getInt("PropertyValue");
                  break;
                case RealValue:
                  rs.getDouble("PropertyValue");
                  break;
                case StringValue:
                  rs.getString("PropertyValue");
                  break;
                case DateTimeValue:
                case MonthValue:
                case DateValue:
                  rs.getDate("PropertyValue");
                  break;
                case TimeValue:
                  rs.getTime("PropertyValue");
                  break;
                case ClobValue:
                  rs.getClob("PropertyValue");
                  if ((result!=null)&&(((java.sql.Clob) result).length()>0)) {
                    result = ((java.sql.Clob) result).getSubString(1L, (int) ((java.sql.Clob) result).length());
                  }
                  break;
                case ObjectValue:
                  rs.getBlob("PropertyValue");
                  break;
                default:
                  rs.getObject("PropertyValue");
                  break;
              }
            }
          }
        } finally {
          rs.close();
        }
      }
    } catch (SQLException ex) {
      Logger.getLogger(SqlPropertyRetrieverImpl.class.getName()).log(Level.SEVERE, null, ex);
    }

    return result;
  }
}
