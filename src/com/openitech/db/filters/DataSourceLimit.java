/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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

  protected boolean hideTop10 = false;

  /**
   * Get the value of hideTop10
   *
   * @return the value of hideTop10
   */
  public boolean isHideTop10() {
    return hideTop10;
  }

  /**
   * Set the value of hideTop10
   *
   * @param hideTop10 new value of hideTop10
   */
  public void setHideTop10(boolean hideTop10) {
    this.hideTop10 = hideTop10;
  }

  protected boolean hideTop50 = false;

  /**
   * Get the value of hideTop10
   *
   * @return the value of hideTop10
   */
  public boolean isHideTop50() {
    return hideTop50;
  }

  /**
   * Set the value of hideTop10
   *
   * @param hideTop10 new value of hideTop10
   */
  public void setHideTop50(boolean hideTop50) {
    this.hideTop50 = hideTop50;
  }

  protected boolean hideTop100 = false;

  /**
   * Get the value of hideTop10
   *
   * @return the value of hideTop10
   */
  public boolean isHideTop100() {
    return hideTop100;
  }

  /**
   * Set the value of hideTop10
   *
   * @param hideTop10 new value of hideTop10
   */
  public void setHideTop100(boolean hideTop100) {
    this.hideTop100 = hideTop100;
  }

  protected boolean hideTop1000 = false;

  /**
   * Get the value of hideTop10
   *
   * @return the value of hideTop10
   */
  public boolean isHideTop1000() {
    return hideTop1000;
  }

  /**
   * Set the value of hideTop10
   *
   * @param hideTop10 new value of hideTop10
   */
  public void setHideTop1000(boolean hideTop1000) {
    this.hideTop1000 = hideTop1000;
  }

  protected boolean hideTopAll = false;

  /**
   * Get the value of hideTop10
   *
   * @return the value of hideTop10
   */
  public boolean isHideTopAll() {
    return hideTopAll;
  }

  /**
   * Set the value of hideTop10
   *
   * @param hideTop10 new value of hideTop10
   */
  public void setHideTopAll(boolean hideTopAll) {
    this.hideTopAll = hideTopAll;
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
