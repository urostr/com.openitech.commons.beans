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
