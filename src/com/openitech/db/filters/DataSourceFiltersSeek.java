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


package com.openitech.db.filters;

import com.openitech.db.filters.DataSourceFilters.AbstractSeekType;

public class DataSourceFiltersSeek<T extends AbstractSeekType> {

  public DataSourceFiltersSeek(DataSourceFilters filter, T seek) {
    super();
    this.filter = filter;
    this.seek = seek;
  }
  public DataSourceFilters filter;

  /**
   * Get the value of filter
   *
   * @return the value of filter
   */
  public DataSourceFilters getFilter() {
    return filter;
  }

  /**
   * Set the value of filter
   *
   * @param filter new value of filter
   */
  public void setFilter(DataSourceFilters filter) {
    this.filter = filter;
  }
  public T seek;

  /**
   * Get the value of seek
   *
   * @return the value of seek
   */
  public T getSeek() {
    return seek;
  }

  /**
   * Set the value of seek
   *
   * @param seek new value of seek
   */
  public void setSeek(T seek) {
    this.seek = seek;
  }

  public interface Reader {
    public DataSourceFiltersSeek getDataSourceFilterSeek(String name);
  }
}
