/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.db.components.JDbTable;
import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.model.DataSourceObserver;
import com.openitech.db.filters.DataSourceFiltersMap;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbTableModel;
import com.openitech.db.model.xml.config.DataModel;
import com.openitech.db.model.xml.config.DataModel.TableColumns.TableColumnDefinition;
import com.openitech.db.model.xml.config.DataSourceFilter;
import com.openitech.db.model.xml.config.DataSourceParametersFactory;
import com.openitech.db.model.xml.config.Factory;
import com.openitech.db.model.xml.config.SeekParameters;
import com.openitech.db.model.xml.config.SeekParameters.SifrantSeekType.LookupDefinition.Lookup;
import com.openitech.swing.framework.context.AssociatedFilter;
import com.openitech.swing.framework.context.AssociatedTasks;
import groovy.lang.GroovyClassLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  /**
   * Get the value of parameters
   *
   * @return the value of parameters
   */
  public abstract List<? extends Object> getParameters();
  protected List<JMenu> viewMenuItems = new ArrayList<JMenu>();

  /**
   * Get the value of viewMenuItems
   *
   * @return the value of viewMenuItems
   */
  public List<JMenu> getViewMenuItems() {
    return viewMenuItems;
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
            filter.addDataSource(dataSource);
          }
        }
        if (dataSourceFilter.getParameters() != null) {
          for (SeekParameters seekParameter : dataSourceFilter.getParameters().getSeekParameters()) {
            if (seekParameter.getSeekType() != null) {
              final SeekParameters.SeekType parameter = seekParameter.getSeekType();

              String field = parameter.getField();
              com.openitech.db.model.xml.config.SeekType type = parameter.getType();
              Integer minumumLength = parameter.getMinumumLength();
              Integer parameterCount = parameter.getParameterCount();

              DataSourceFilters.SeekType seekType;
              if (type == null) {
                seekType = new DataSourceFilters.SeekType(field);
              } else if (minumumLength == null) {
                seekType = new DataSourceFilters.SeekType(field, getSeekType(type));
              } else if (parameterCount == null) {
                seekType = new DataSourceFilters.SeekType(field, getSeekType(type), minumumLength);
              } else {
                seekType = new DataSourceFilters.SeekType(field, getSeekType(type), minumumLength, parameterCount);
              }
              seekType.setName(parameter.getName());

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
              integerSeekType.setName(parameter.getName());

              filtersMap.put(filter, integerSeekType);
            } else if (seekParameter.getBetweenDateSeekType() != null) {
              final SeekParameters.BetweenDateSeekType parameter = seekParameter.getBetweenDateSeekType();

              String field = parameter.getField();

              DataSourceFilters.BetweenDateSeekType betweenDateSeekType = new DataSourceFilters.BetweenDateSeekType(field);
              betweenDateSeekType.setName(parameter.getName());

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
                    if (factory.getGroovy() != null) {
                      GroovyClassLoader gcl = new GroovyClassLoader(DataSourceFactory.class.getClassLoader());
                      Class gcls = gcl.parseClass(factory.getGroovy(), "waSifrantSeekType_" + System.currentTimeMillis());
                      Constructor constructor = gcls.getConstructor(String.class);
                      newInstance = constructor.newInstance(field);
                    } else if (factory.getClassName() != null) {
                      @SuppressWarnings(value = "static-access")
                      Class jcls = AbstractDataSourceParametersFactory.class.forName(factory.getClassName());
                      Constructor constructor = jcls.getConstructor(String.class);
                      newInstance = constructor.newInstance(field);
                    }
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
                } else {
                  Lookup lookup = parameter.getLookupDefinition().getLookup();
                  String sifrantSkupina = lookup.getSifrantSkupina();
                  String sifrantOpis = lookup.getSifrantOpis();
                  String textNotDefined = lookup.getTextNotDefined();
                  List<String> allowedValues = lookup.getAllowedValues();
                  List<String> excludedValues = lookup.getExcludedValues();

                  if ((allowedValues.size() > 0) || (excludedValues.size() > 0)) {
                    sifrantSeekType = new DataSourceFilters.SifrantSeekType(
                            new DataSourceFilters.SeekType(field, DataSourceFilters.SeekType.EQUALS, 1),
                            sifrantSkupina,
                            sifrantOpis,
                            textNotDefined,
                            (allowedValues.isEmpty() ? null : allowedValues), excludedValues.isEmpty() ? null : excludedValues);
                  } else if (textNotDefined == null) {
                    sifrantSeekType = new DataSourceFilters.SifrantSeekType(field, sifrantSkupina, sifrantOpis);
                  } else {
                    sifrantSeekType = new DataSourceFilters.SifrantSeekType(field, sifrantSkupina, sifrantOpis, textNotDefined);
                  }
                }

                if (sifrantSeekType != null) {
                  sifrantSeekType.setName(parameter.getName());

                  filtersMap.put(filter, sifrantSeekType);
                }
              }
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
      if (dataSourceParametersFactory.getExportMenuModels().getName()==null) {
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

      if (jmiExport.getMenuComponentCount()>0) {
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
