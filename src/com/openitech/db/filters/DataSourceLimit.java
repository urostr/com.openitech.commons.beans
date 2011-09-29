/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.filters;

import com.openitech.db.model.DbDataSource;

/**
 *
 * @author uros
 */
public class DataSourceLimit extends DbDataSource.SubstSqlParameter {

  private Limit limitValue = Limit.DEFAULT_LIMIT;

  public void setValue(Limit limit) {
    limitValue = limit;
    super.setValue(limit.getValue());
  }

  public enum Limit {

    L10 {

      @Override
      public String getValue() {
        return " TOP 10 ";
      }
    },
    L50 {

      @Override
      public String getValue() {
        return " TOP 50 ";
      }
    },
    L100 {

      @Override
      public String getValue() {
        return " TOP 100 ";
      }
    },
    LALL {

      @Override
      public String getValue() {
        return "";
      }
    },
    DEFAULT_LIMIT {

      @Override
      public String getValue() {
        return L10.getValue();
      }
    };

    public abstract String getValue();
  }

  public DataSourceLimit(String replace) {
    super(replace);
  }
}
