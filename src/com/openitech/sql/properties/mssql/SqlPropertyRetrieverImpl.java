/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.properties.mssql;

import com.openitech.db.ConnectionManager;
import com.openitech.spring.beans.factory.config.AbstractPropertyRetriever;
import com.openitech.spring.beans.factory.config.PropertyType;
import com.openitech.sql.FieldValue;
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

      if (count>0) {
        param = 1;
        getProperty.setInt(param++, type.getContentType());
        getProperty.setString(param++, properyName);

        rs = getProperty.executeQuery();
        try {
          if (rs.next()) {
            rs.getObject("PropertyValue");
            if (!rs.wasNull()) {
              int fieldType = rs.getInt("FieldType");
              if (fieldType==FieldValue.ValueType.IntValue.getTypeIndex()) {
                result = rs.getInt("PropertyValue");
              } else if (fieldType==FieldValue.ValueType.RealValue.getTypeIndex()) {
                result = rs.getDouble("PropertyValue");
              } else if (fieldType==FieldValue.ValueType.StringValue.getTypeIndex()) {
                result = rs.getString("PropertyValue");
              } else if (fieldType==FieldValue.ValueType.DateValue.getTypeIndex()) {
                result = rs.getDate("PropertyValue");
              } else if (fieldType==FieldValue.ValueType.ObjectValue.getTypeIndex()) {
                result = rs.getObject("PropertyValue");
              } else if (fieldType==FieldValue.ValueType.ClobValue.getTypeIndex()) {
                result = rs.getClob("PropertyValue");
                result = ((java.sql.Clob) result).getSubString(1L, (int) ((java.sql.Clob) result).length());
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
