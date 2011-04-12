/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.sql;

import com.openitech.db.model.DbDataSourceParametersPreprocessor;
import com.openitech.db.model.Preprocessor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 *
 * @author uros
 */
public class TemporaryParameterPreprocessor implements Preprocessor<List<?>, Connection> {

  static {
    DbDataSourceParametersPreprocessor.getInstance().getPreprocessors(false).add(new TemporaryParameterPreprocessor());
  }

  private TemporaryParameterPreprocessor() {
  }

  @Override
  public List<?> preprocess(List<?> parameters, Connection connection) throws SQLException {
    List<Object> result = null;
//    synchronized (connection) {
    List<Object> queryParameters = new java.util.ArrayList(parameters.size());
    Set<TemporarySubselectSqlParameter.TemporaryTableGroup> temporarySubselectSqlParameters = new java.util.LinkedHashSet<TemporarySubselectSqlParameter.TemporaryTableGroup>(parameters.size());
    for (Object parameter : parameters) {
      if (parameter instanceof TemporarySubselectSqlParameter) {
        TemporarySubselectSqlParameter tt = (TemporarySubselectSqlParameter) parameter;
        temporarySubselectSqlParameters.add(tt.getGroup());
        queryParameters.add(tt.getSubstSqlParameter());
      } else {
        queryParameters.add(parameter);
      }
    }

    if (!temporarySubselectSqlParameters.isEmpty()) {
      try {
        for (TemporarySubselectSqlParameter.TemporaryTableGroup tempSubselect : temporarySubselectSqlParameters) {
          tempSubselect.executeQuery(connection, queryParameters);
        }
      } catch (InterruptedException ex) {
        throw new SQLException("Can't prepare the temporary tables", ex);
      }
    }
    result = queryParameters;
//    }
    return result;
  }
}
