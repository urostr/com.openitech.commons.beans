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
package com.openitech.db.model.factory;

import com.openitech.db.components.JDbTable;
import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.model.DataSourceObserver;
import com.openitech.db.filters.DataSourceFiltersMap;
import com.openitech.db.filters.JWorkAreaFilter;
import com.openitech.db.model.AutoInsertValue;
import com.openitech.db.model.DbComboBoxModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbTableModel;
import com.openitech.db.model.xml.config.DataModel;
import com.openitech.db.model.xml.config.DataModel.TableColumns.TableColumnDefinition;
import com.openitech.db.model.xml.config.DataSourceFilter;
import com.openitech.db.model.xml.config.DataSourceFilter.AutoInsertColumns;
import com.openitech.db.model.xml.config.DataSourceFilter.FilterParameter;
import com.openitech.db.model.xml.config.DataSourceParametersFactory;
import com.openitech.db.model.xml.config.Factory;
import com.openitech.db.model.xml.config.RezultatKlicaValues;
import com.openitech.db.model.xml.config.SeekParameters;
import com.openitech.db.model.xml.config.SeekParameters.CheckBoxSeekType;
import com.openitech.db.model.xml.config.SeekParameters.ConfigureFilterSeekType;
import com.openitech.db.model.xml.config.SeekParameters.RezultatExistsSeekType;
import com.openitech.db.model.xml.config.SeekParameters.RezultatKlicaSeekType;
import com.openitech.db.model.xml.config.SeekParameters.RezultatKlicaSeekType.Rezultati;
import com.openitech.db.model.xml.config.SeekParameters.SifrantSeekType.LookupDefinition.ComboBoxModel;
import com.openitech.db.model.xml.config.SeekParameters.SifrantSeekType.LookupDefinition.ComboBoxModel.Display;
import com.openitech.db.model.xml.config.SeekParameters.SifrantSeekType.LookupDefinition.Lookup;
import com.openitech.swing.framework.context.AssociatedFilter;
import com.openitech.swing.framework.context.AssociatedTasks;
import com.openitech.value.fields.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;

/**
 *
 * @author uros
 */
public abstract class AbstractDataSourceParametersFactory implements DataSourceObserver, DataSourceFiltersMap.MapReader, AssociatedFilter, AssociatedTasks {

  public AbstractDataSourceParametersFactory(DbDataSource dataSource) {
    this.dataSource = dataSource;
  }
  protected DbDataSource dataSource;

  /**
   * Get the value of dataSource
   *
   * @return the value of dataSource
   */
  @Override
  public DbDataSource getDataSource() {
    return dataSource;
  }

  /**
   * Set the value of dataSource
   *
   * @param dataSource new value of dataSource
   */
  @Override
  public void setDataSource(DbDataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Configures the data source parameters factory
   */
  public abstract void configure() throws SQLException;
  protected boolean canExportData = true;

  /**
   * Get the value of canExportData
   *
   * @return the value of canExportData
   */
  public boolean isCanExportData() {
    return canExportData;
  }

  /**
   * Set the value of canExportData
   *
   * @param canExportData new value of canExportData
   */
  public void setCanExportData(boolean canExportData) {
    this.canExportData = canExportData;
  }

  /**
   * Get the value of parameters
   *
   * @return the value of parameters
   */
  public abstract List<? extends Object> getParameters();
  protected List<JMenu> viewMenuItems = new ArrayList<JMenu>();
  protected List<JMenu> exportMenuItems = new ArrayList<JMenu>();

  /**
   * Get the value of viewMenuItems
   *
   * @return the value of viewMenuItems
   */
  public List<JMenu> getViewMenuItems() {
    if (isCanExportData()) {
      return viewMenuItems;
    } else {
      viewMenuItems.removeAll(exportMenuItems);
      return viewMenuItems;
    }
  }
  protected DataSourceFiltersMap filtersMap = new DataSourceFiltersMap();

  /**
   * Get the value of filtersMap
   *
   * @return the value of filtersMap
   */
  @Override
  public DataSourceFiltersMap getFiltersMap() {
    return filtersMap;
  }
  protected DataSourceParametersFactory dataSourceParametersFactory;

  /**
   * Get the value of dataSourceParametersFactory
   *
   * @return the value of dataSourceParametersFactory
   */
  public DataSourceParametersFactory getDataSourceParametersFactory() {
    return dataSourceParametersFactory;
  }
  private List<JWorkAreaFilter> workAreaFilters = new ArrayList<JWorkAreaFilter>();

  public List<JWorkAreaFilter> getWorkAreaFilters() {
    return workAreaFilters;
  }
  private List<AutoInsertValue> autoInsertValues = new ArrayList<AutoInsertValue>();

  public List<AutoInsertValue> getAutoInsertValue() {
    return autoInsertValues;
  }

  private Set<Field> dataEntryValues = new HashSet<Field>();

  public Set<Field> getDataEntryValues() {
    return dataEntryValues;
  }
  /**
   * Set the value of dataSourceParametersFactory
   *
   * @param dataSourceParametersFactory new value of dataSourceParametersFactory
   */
  public void setDataSourceParametersFactory(DataSourceParametersFactory dataSourceParametersFactory) {
    this.dataSourceParametersFactory = dataSourceParametersFactory;
  }

  protected void configure(java.util.Map<String, DataSourceFilters> filters) {
    if ((dataSourceParametersFactory != null)
            && (dataSourceParametersFactory.getFilters() != null)) {
      for (DataSourceFilter dataSourceFilter : dataSourceParametersFactory.getFilters().getDataSourceFilter()) {
        final String replace = dataSourceFilter.getReplace();
        DataSourceFilters filter = filters.get(replace);
        if (filter == null) {
          filters.put(replace, filter = new DataSourceFilters(replace));
          if (dataSourceFilter.getOperator() != null) {
            filter.setOperator(dataSourceFilter.getOperator());
          }
        }
        if (!filter.getDataSources().contains(dataSource)) {
          filter.addDataSource(dataSource);
        }

        if (dataSourceFilter.isFilterRequred() != null) {
          filter.setFilterRequired(dataSourceFilter.isFilterRequred());
        }

        if (dataSourceFilter.isFilterRequired() != null) {
          filter.setFilterRequired(dataSourceFilter.isFilterRequired());
        }
        
        if (dataSourceFilter.getFilterSequence() != null) {
          filter.setSequence(dataSourceFilter.getFilterSequence());
        }

        if (dataSourceFilter.getFilterParameter() != null) {
          FilterParameter filterParameter = dataSourceFilter.getFilterParameter();
          for (FilterParameter.WorkAreaParameter workAreaParameter : filterParameter.getWorkAreaParameter()) {
            Integer workAreaId = workAreaParameter.getWorkAreaId();
            String workSpaceId = workAreaParameter.getWorkSpaceId();
            List<Integer> workSpaceIds = new ArrayList<Integer>();
            if(workSpaceId != null){
              String[] split = workSpaceId.split(",");
              for (String string : split) {
                workSpaceIds.add(Integer.parseInt(string.trim()));
              }
            }

            String otherColumName = workAreaParameter.getOtherColumName();
            SeekParameters seekParameter = workAreaParameter.getSeekParameter();
            Boolean convertToVarchar = null;
            String convertToVarcharS = seekParameter.getConvertToVarchar();
            if (convertToVarcharS != null) {
              convertToVarchar = Boolean.valueOf(convertToVarcharS);
            }
            if (seekParameter.getSeekType() != null) {
              final SeekParameters.SeekType parameter = seekParameter.getSeekType();

              String field = parameter.getField();
              com.openitech.db.model.xml.config.SeekType type = parameter.getType();
              Integer minumumLength = parameter.getMinumumLength();
              Integer parameterCount = parameter.getParameterCount();

              DataSourceFilters.SeekType seekType;
              seekType = new DataSourceFilters.SeekType(field);

              if (type != null) {
                seekType.setSeekType(getSeekType(type));
              }
              if (parameterCount != null) {
                seekType.setParameter_count(parameterCount);
              }
              if (minumumLength != null) {
                seekType.setMin_length(minumumLength);
              }
              if (convertToVarchar != null) {
                seekType.setConvertToVarchar(convertToVarchar);
              }
              seekType.setName(parameter.getName());
              seekType.setLayout(parameter.getLayout());
              
              if (parameter.getDefaultValue()!=null) {
                Object value = null;
                if (parameter.getDefaultValue().getDefaultValueFactory()!=null) {
                  try {
                    value = ClassInstanceFactory.getInstance(parameter.getDefaultValue().getDefaultValueFactory(), new Class[] {}).newInstance(new Object[] {});
                  } catch (Exception ex) {
                    Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                    value = null;
                  }
                } else {
                  value = parameter.getDefaultValue().getDefaultValue();
                }
                
                if (value != null) {
                  seekType.setValue(value.toString());
                }
              }

              if (seekParameter.isRequired() != null && seekParameter.isRequired()) {
                filter.addRequired(seekType);
              }

              workAreaFilters.add(new JWorkAreaFilter(workAreaId, workSpaceIds.toArray(new Integer[workSpaceIds.size()]), filter, seekType, otherColumName));
            } else if (seekParameter.getIntegerSeekType() != null) {
              final SeekParameters.IntegerSeekType parameter = seekParameter.getIntegerSeekType();

              String field = parameter.getField();
              com.openitech.db.model.xml.config.SeekType type = parameter.getType();

              DataSourceFilters.IntegerSeekType integerSeekType;
              if (type == null) {
                integerSeekType = new DataSourceFilters.IntegerSeekType(field);
              } else {
                integerSeekType = new DataSourceFilters.IntegerSeekType(field, getSeekType(type));
              }
              if (parameter.getMinumumLength() != null) {
                integerSeekType.setMinLength(parameter.getMinumumLength());
              }
              if (parameter.getParameterCount() != null) {
                integerSeekType.setParameter_count(parameter.getParameterCount());
              }
              if (convertToVarchar != null) {
                integerSeekType.setConvertToVarchar(convertToVarchar);
              }
              integerSeekType.setName(parameter.getName());
              integerSeekType.setLayout(parameter.getLayout());
              
              if (parameter.getDefaultValue()!=null) {
                Object value = null;
                if (parameter.getDefaultValue().getDefaultValueFactory()!=null) {
                  try {
                    value = ClassInstanceFactory.getInstance(parameter.getDefaultValue().getDefaultValueFactory(), new Class[] {}).newInstance(new Object[] {});
                  } catch (Exception ex) {
                    Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                    value = null;
                  }
                } else {
                  value = parameter.getDefaultValue().getDefaultValue();
                }
                
                if (value != null) {
                  integerSeekType.setValue(Integer.valueOf(value.toString()));
                }
              }

              if (seekParameter.isRequired() != null && seekParameter.isRequired()) {
                filter.addRequired(integerSeekType);
              }

              workAreaFilters.add(new JWorkAreaFilter(workAreaId, workSpaceIds.toArray(new Integer[workSpaceIds.size()]), filter, integerSeekType, otherColumName));
            }
          }

        }

        if (dataSourceFilter.getAutoInsertColumns() != null) {
          AutoInsertColumns autoInsertColumns = dataSourceFilter.getAutoInsertColumns();
          for (AutoInsertColumns.Column column : autoInsertColumns.getColumn()) {
            Integer workAreaId = column.getWorkAreaId();
            String workSpaceId = column.getWorkSpaceId();
            List<Integer> workSpaceIds = new ArrayList<Integer>();
            if(workSpaceId != null){
              String[] split = workSpaceId.split(",");
              for (String string : split) {
                workSpaceIds.add(Integer.parseInt(string.trim()));
              }
            }
            String columName = column.getColumName();
            String otherColumName = column.getOtherColumName();
            autoInsertValues.add(new AutoInsertValue(workAreaId, workSpaceIds.toArray(new Integer[workSpaceIds.size()]), dataSource, columName, otherColumName));
          }
        }

        if (dataSourceFilter.getParameters() != null) {
          for (SeekParameters seekParameter : dataSourceFilter.getParameters().getSeekParameters()) {
            Boolean convertToVarchar = null;
            String convertToVarcharS = seekParameter.getConvertToVarchar();
            if (convertToVarcharS != null) {
              convertToVarchar = Boolean.valueOf(convertToVarcharS);
            }
//            seekParameter.getRezultatKlicaSeekType().getRezultati().getRezultatiValues().get(0).isChecked();
            if (seekParameter.getSeekType() != null) {
              final SeekParameters.SeekType parameter = seekParameter.getSeekType();

              String field = parameter.getField();
              com.openitech.db.model.xml.config.SeekType type = parameter.getType();
              Integer minumumLength = parameter.getMinumumLength();
              Integer parameterCount = parameter.getParameterCount();

              DataSourceFilters.SeekType seekType;
              seekType = new DataSourceFilters.SeekType(field);

              if (type != null) {
                seekType.setSeekType(getSeekType(type));
              }
              if (parameterCount != null) {
                seekType.setParameter_count(parameterCount);
              }
              if (minumumLength != null) {
                seekType.setMin_length(minumumLength);
              }
              if (convertToVarchar != null) {
                seekType.setConvertToVarchar(convertToVarchar);
              }
              seekType.setName(parameter.getName());
              seekType.setLayout(parameter.getLayout());
              
              if (parameter.getDefaultValue()!=null) {
                Object value = null;
                if (parameter.getDefaultValue().getDefaultValueFactory()!=null) {
                  try {
                    value = ClassInstanceFactory.getInstance(parameter.getDefaultValue().getDefaultValueFactory(), new Class[] {}).newInstance(new Object[] {});
                  } catch (Exception ex) {
                    Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                    value = null;
                  }
                } else {
                  value = parameter.getDefaultValue().getDefaultValue();
                }
                
                if (value != null) {
                  seekType.setValue(value.toString());
                }
              }

              if (seekParameter.isRequired() != null && seekParameter.isRequired()) {
                filter.addRequired(seekType);
              }

              filtersMap.put(filter, seekType);
            } else if (seekParameter.getIntegerSeekType() != null) {
              final SeekParameters.IntegerSeekType parameter = seekParameter.getIntegerSeekType();

              String field = parameter.getField();
              com.openitech.db.model.xml.config.SeekType type = parameter.getType();

              DataSourceFilters.IntegerSeekType integerSeekType;
              if (type == null) {
                integerSeekType = new DataSourceFilters.IntegerSeekType(field);
              } else {
                integerSeekType = new DataSourceFilters.IntegerSeekType(field, getSeekType(type));
              }
              if (parameter.getMinumumLength() != null) {
                integerSeekType.setMinLength(parameter.getMinumumLength());
              }
              if (parameter.getParameterCount() != null) {
                integerSeekType.setParameter_count(parameter.getParameterCount());
              }
              if (convertToVarchar != null) {
                integerSeekType.setConvertToVarchar(convertToVarchar);
              }
              integerSeekType.setName(parameter.getName());
              integerSeekType.setLayout(parameter.getLayout());
              
              if (parameter.getDefaultValue()!=null) {
                Object value = null;
                if (parameter.getDefaultValue().getDefaultValueFactory()!=null) {
                  try {
                    value = ClassInstanceFactory.getInstance(parameter.getDefaultValue().getDefaultValueFactory(), new Class[] {}).newInstance(new Object[] {});
                  } catch (Exception ex) {
                    Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                    value = null;
                  }
                } else {
                  value = parameter.getDefaultValue().getDefaultValue();
                }
                
                if (value != null) {
                  integerSeekType.setValue(Integer.valueOf(value.toString()));
                }
              }

              if (seekParameter.isRequired() != null && seekParameter.isRequired()) {
                filter.addRequired(integerSeekType);
              }

              filtersMap.put(filter, integerSeekType);
            } else if (seekParameter.getBetweenDateSeekType() != null) {
              final SeekParameters.BetweenDateSeekType parameter = seekParameter.getBetweenDateSeekType();


              String field = parameter.getField();

              DataSourceFilters.BetweenDateSeekType betweenDateSeekType;
              if (parameter.isPreformated() != null) {
                betweenDateSeekType = new DataSourceFilters.BetweenDateSeekType(field, parameter.isPreformated());
              } else {
                betweenDateSeekType = new DataSourceFilters.BetweenDateSeekType(field);
              }
              if (convertToVarchar != null) {
                betweenDateSeekType.setConvertToVarchar(convertToVarchar);
              }
              betweenDateSeekType.setName(parameter.getName());
              betweenDateSeekType.setLayout(parameter.getLayout());
              if (parameter.getDefaultValueFrom()!=null || 
                  parameter.getDefaultValueTo()!=null) {
              java.util.Date[] values = new java.util.Date[] {new java.util.Date(0L),new java.util.Date()};
              
              if (parameter.getDefaultValueFrom() != null) {
                  Object value = null;
                  if (parameter.getDefaultValueFrom().getDefaultValueFactory() != null) {
                    try {
                      value = ClassInstanceFactory.getInstance(parameter.getDefaultValueFrom().getDefaultValueFactory(), new Class[]{}).newInstance(new Object[]{});
                    } catch (Exception ex) {
                      Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                      value = null;
                    }
                  } else {
                    value = parameter.getDefaultValueFrom().getDefaultValue();
                  }
                  
                  if (value != null) {
                    try {
                      value = com.openitech.text.FormatFactory.DATETIME_FORMAT.parse(value.toString());
                      values[0] = (java.util.Date) value;
                    } catch (ParseException ex) {
                      Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                  }
                }
              
              if (parameter.getDefaultValueTo() != null) {
                  Object value = null;
                  if (parameter.getDefaultValueTo().getDefaultValueFactory() != null) {
                    try {
                      value = ClassInstanceFactory.getInstance(parameter.getDefaultValueTo().getDefaultValueFactory(), new Class[]{}).newInstance(new Object[]{});
                    } catch (Exception ex) {
                      Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                      value = null;
                    }
                  } else {
                    value = parameter.getDefaultValueTo().getDefaultValue();
                  }
                  
                  if (value != null) {
                    try {
                      value = com.openitech.text.FormatFactory.DATETIME_FORMAT.parse(value.toString());
                      values[1] = (java.util.Date) value;
                    } catch (ParseException ex) {
                      Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                  }
                }
              
                betweenDateSeekType.setValue(Arrays.asList(values));
              }

              if (seekParameter.isRequired() != null && seekParameter.isRequired()) {
                filter.addRequired(betweenDateSeekType);
              }

              filtersMap.put(filter, betweenDateSeekType);
            } else if (seekParameter.getSifrantSeekType() != null) {
              final SeekParameters.SifrantSeekType parameter = seekParameter.getSifrantSeekType();

              String field = parameter.getField();

              DataSourceFilters.SifrantSeekType sifrantSeekType = null;
              if (parameter.getLookupDefinition() != null) {
                final Factory factory = parameter.getLookupDefinition().getFactory();
                if (factory != null) {
                  Object newInstance = null;
                  try {
                    newInstance = ClassInstanceFactory.getInstance("waSifrantSeekType_" + System.currentTimeMillis(), factory.getGroovy(), factory.getClassName(), String.class).newInstance(field);

                    sifrantSeekType = (DataSourceFilters.SifrantSeekType) newInstance;
                  } catch (ClassNotFoundException ex) {
                    Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                  } catch (InstantiationException ex) {
                    Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                  } catch (IllegalAccessException ex) {
                    Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                  } catch (IllegalArgumentException ex) {
                    Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                  } catch (InvocationTargetException ex) {
                    Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                  } catch (NoSuchMethodException ex) {
                    Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                  } catch (SecurityException ex) {
                    Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                  }
                } else if (parameter.getLookupDefinition().getComboBoxModel() != null) {
                  final ComboBoxModel comboBoxModel = parameter.getLookupDefinition().getComboBoxModel();
                  Callable<DbComboBoxModel> callComboBoxModel = new Callable<DbComboBoxModel>() {

                    @Override
                    public DbComboBoxModel call() {
                      DbComboBoxModel cmSifrant = new DbComboBoxModel();
                      try {
                        DbDataSource dsSifrant = new DbDataSource();
                        dsSifrant.setShareResults(comboBoxModel.isShareResults());
                        if (comboBoxModel.getCountSQL() != null) {
                          dsSifrant.setCountSql(comboBoxModel.getCountSQL());
                        }
                        dsSifrant.setSelectSql(comboBoxModel.getSelectSQL());
                        dsSifrant.setName(comboBoxModel.getName());
                        cmSifrant.setKeyColumnName(comboBoxModel.getKeyColumnName());

                        final Display display = comboBoxModel.getDisplay();
                        final List<String> valueColumnNames = display.getValueColumnNames();
                        cmSifrant.setValueColumnNames(valueColumnNames.toArray(new String[valueColumnNames.size()]));
                        if (display.getExtendedValueColumnNames().size() > 0) {
                          cmSifrant.setExtendedValueColumnNames(display.getExtendedValueColumnNames().toArray(new String[display.getExtendedValueColumnNames().size()]));
                        }
                        if (display.getSeparators().size() > 0) {
                          cmSifrant.setSeparator(display.getSeparators().toArray(new String[display.getSeparators().size()]));
                        }
                        cmSifrant.setDataSource(dsSifrant);
                      } catch (SQLException ex) {
                        Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                      }
                      return cmSifrant;
                    }
                  };

                  sifrantSeekType = new DataSourceFilters.SifrantSeekType(field, callComboBoxModel);
                } else {
                  Lookup lookup = parameter.getLookupDefinition().getLookup();
                  String sifrantSkupina = lookup.getSifrantSkupina();
                  String sifrantOpis = lookup.getSifrantOpis();
                  String textNotDefined = lookup.getTextNotDefined();
                  String dataBase = lookup.getDataBase();
                  List<String> allowedValues = lookup.getAllowedValues();
                  List<String> excludedValues = lookup.getExcludedValues();

                  sifrantSeekType = new DataSourceFilters.SifrantSeekType(
                          new DataSourceFilters.SeekType(field, DataSourceFilters.SeekType.EQUALS, 1),
                          sifrantSkupina,
                          sifrantOpis,
                          textNotDefined,
                          dataBase,
                          (allowedValues.isEmpty() ? null : allowedValues), excludedValues.isEmpty() ? null : excludedValues);
                }

                if (sifrantSeekType != null) {
                  if (parameter.getDefaultValue() != null) {
                    Object value = null;
                    if (parameter.getDefaultValue().getDefaultValueFactory() != null) {
                      try {
                        value = ClassInstanceFactory.getInstance(parameter.getDefaultValue().getDefaultValueFactory(), new Class[]{}).newInstance(new Object[]{});
                      } catch (Exception ex) {
                        Logger.getLogger(AbstractDataSourceParametersFactory.class.getName()).log(Level.SEVERE, null, ex);
                        value = null;
                      }
                    } else {
                      value = parameter.getDefaultValue().getDefaultValue();
                    }

                    if (value != null) {
                      sifrantSeekType.setValue(value.toString());
                    }
                  }
                  if (convertToVarchar != null) {
                    sifrantSeekType.setConvertToVarchar(convertToVarchar);
                  }
                  sifrantSeekType.setName(parameter.getName());
                  sifrantSeekType.setLayout(parameter.getLayout());

                  if (seekParameter.isRequired() != null && seekParameter.isRequired()) {
                    filter.addRequired(sifrantSeekType);
                  }
                  filtersMap.put(filter, sifrantSeekType);
                }
              }
            } else if (seekParameter.getConfigureFilterSeekType() != null) {
              final ConfigureFilterSeekType parameter = seekParameter.getConfigureFilterSeekType();


              DataSourceFilters.ValueSeekType preformatedSeekType;

              preformatedSeekType = new DataSourceFilters.ValueSeekType();

              if (convertToVarchar != null) {
                preformatedSeekType.setConvertToVarchar(convertToVarchar);
              }
              preformatedSeekType.setName(parameter.getName());
              preformatedSeekType.setLayout(parameter.getLayout());

              if (seekParameter.isRequired() != null && seekParameter.isRequired()) {
                filter.addRequired(preformatedSeekType);
              }

              filtersMap.put(filter, preformatedSeekType);
            } else if (seekParameter.getRezultatKlicaSeekType() != null) {
              final RezultatKlicaSeekType parameter = seekParameter.getRezultatKlicaSeekType();

              DataSourceFilters.RezultatiKlicaSeekType rezultatiSeekType;

              rezultatiSeekType = new DataSourceFilters.RezultatiKlicaSeekType(parameter.getField(), filter);

              if (convertToVarchar != null) {
                rezultatiSeekType.setConvertToVarchar(convertToVarchar);
              }
              rezultatiSeekType.setName(parameter.getName());
              rezultatiSeekType.setLayout(parameter.getLayout());
              Rezultati rezultati = parameter.getRezultati();
              if (rezultati != null) {
                List<RezultatKlicaValues> rezultatiValues = rezultati.getRezultatiValues();
                for (RezultatKlicaValues rezultat : rezultatiValues) {
                  rezultatiSeekType.addRezultat(new DataSourceFilters.RezultatiKlicaSeekType.RezultatKlica(rezultat.getOpis(), rezultat.getSifra(), rezultat.isChecked()));
                }
              }

              filtersMap.put(filter, rezultatiSeekType);
            } else if (seekParameter.getRezultatExistsSeekType() != null) {
              RezultatExistsSeekType parameter = seekParameter.getRezultatExistsSeekType();

              DataSourceFilters.RezultatExistsSeekType existsSeekType;

              existsSeekType = new DataSourceFilters.RezultatExistsSeekType(parameter.getSelect(), parameter.getReplace(), parameter.getField(), filter);

              if (convertToVarchar != null) {
                existsSeekType.setConvertToVarchar(convertToVarchar);
              }
              existsSeekType.setName(parameter.getName());
              existsSeekType.setLayout(parameter.getLayout());
              RezultatExistsSeekType.Rezultati rezultati = parameter.getRezultati();
              if (rezultati != null) {
                List<RezultatKlicaValues> rezultatiValues = rezultati.getRezultatiValues();
                for (RezultatKlicaValues rezultat : rezultatiValues) {
                  existsSeekType.addRezultat(new DataSourceFilters.RezultatExistsSeekType.RezultatExistsValue(rezultat.getOpis(), rezultat.getSifra(), rezultat.isChecked(), rezultat.isAsString()));
                }
              }

              filtersMap.put(filter, existsSeekType);
            } else if (seekParameter.getCheckBoxSeekType() != null) {
              CheckBoxSeekType parameter = seekParameter.getCheckBoxSeekType();

              DataSourceFilters.CheckBoxSeekType checkBoxSeekType;

              if (parameter.getField()!=null) {
                checkBoxSeekType = new DataSourceFilters.CheckBoxSeekType(parameter.getField(), parameter.getOperator().value(), filter);
              } else if (parameter.getField()==null && 
                         parameter.getOperator()!=null) {
                checkBoxSeekType = new DataSourceFilters.CheckBoxSeekType("", parameter.getOperator().value(), filter);
              } else {
                checkBoxSeekType = new DataSourceFilters.CheckBoxSeekType(filter);
              }

              if (convertToVarchar != null) {
                checkBoxSeekType.setConvertToVarchar(convertToVarchar);
              }
              checkBoxSeekType.setName(parameter.getName());
              checkBoxSeekType.setLayout(parameter.getLayout());
              checkBoxSeekType.setGrouped(parameter.isButtonGroup());
              
              CheckBoxSeekType.Rezultati rezultati = parameter.getRezultati();
              if (rezultati != null) {
                List<RezultatKlicaValues> rezultatiValues = rezultati.getRezultatiValues();
                for (RezultatKlicaValues rezultat : rezultatiValues) {
                  checkBoxSeekType.addRezultat(new DataSourceFilters.CheckBoxSeekType.CheckBoxValue(rezultat.getOpis(), rezultat.getSifra(), rezultat.isChecked()));
                }
              }

              filtersMap.put(filter, checkBoxSeekType);
            }
          }
        }
      }
    }
  }

  protected void configure(List<JMenu> viewMenuItems) {
    if ((dataSourceParametersFactory != null)
            && (dataSourceParametersFactory.getExportMenuModels() != null)) {

      javax.swing.JMenu jmiExport = new javax.swing.JMenu();
      if (dataSourceParametersFactory.getExportMenuModels().getName() == null) {
        jmiExport.setText("Izvozi");
      } else {
        jmiExport.setText(dataSourceParametersFactory.getExportMenuModels().getName());
      }
      for (DataSourceParametersFactory.ExportMenuModels.Model model : dataSourceParametersFactory.getExportMenuModels().getModel()) {
        final DbTableModel tmExport = createTableModel(model.getDataModel());
        final JDbTable jtExport = new JDbTable();
        jtExport.setModel(tmExport);

        javax.swing.JMenuItem jmiExportTM = new javax.swing.JMenuItem();
        jmiExportTM.setText(model.getName());
        jmiExportTM.addActionListener(new java.awt.event.ActionListener() {

          @Override
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            java.awt.EventQueue.invokeLater(new Runnable() {

              @Override
              public void run() {
                com.openitech.util.HSSFWrapper.openWorkbook(jtExport);
              }
            });
          }
        });

        jmiExport.add(jmiExportTM);
      }

      if (jmiExport.getMenuComponentCount() > 0) {
        exportMenuItems.add(jmiExport);
        viewMenuItems.add(jmiExport);
      }
    }
  }

  protected int getSeekType(com.openitech.db.model.xml.config.SeekType type) {
    return Arrays.asList(com.openitech.db.model.xml.config.SeekType.values()).indexOf(type);
  }

  private DbTableModel createTableModel(DataModel dataModel) {
    com.openitech.db.model.DbTableModel tableModel = new com.openitech.db.model.DbTableModel();
    List<String[]> tableColumns = new ArrayList<String[]>();
    for (TableColumnDefinition tableColumnDefinition : dataModel.getTableColumns().getTableColumnDefinition()) {
      tableColumns.add(tableColumnDefinition.getTableColumnEntry().toArray(new String[tableColumnDefinition.getTableColumnEntry().size()]));
    }
    tableModel.setColumns(tableColumns.toArray(new String[tableColumns.size()][]));
    if (dataModel.getSeparator() != null) {
      tableModel.setSeparator(dataModel.getSeparator());
    }
    tableModel.setDataSource(getDataSource());
    return tableModel;
  }
}
