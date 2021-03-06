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
 * DbSifrantModel.java
 *
 * Created on Ponedeljek, 11 avgust 2008, 10:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.model;

import com.openitech.sql.util.SqlUtilities;
import java.beans.DesignMode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.event.ListDataEvent;

/**
 *
 * @author uros
 */
public class DbSifrantModel extends DbComboBoxModel<String> {

  private final DbSifrantModel.SifrantiDataSourceDescriptionFilter fNotDefined = new DbSifrantModel.SifrantiDataSourceDescriptionFilter();
  private final DbSifrantModel.SifrantiDataSourceFilters fGroup = new DbSifrantModel.SifrantiDataSourceFilters();
  private final DbDataSource.SubstSqlParameter fValidDate = new DbDataSource.SubstSqlParameter("<%ValidDate%>");
  private final DbDataSource.SqlParameter<java.sql.Date> validDate = new DbDataSource.SqlParameter<java.sql.Date>(java.sql.Types.DATE, new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
  DbDataSource.SubstSqlParameter valuesConstraint = new DbDataSource.SubstSqlParameter("<%ValuesConstraint%>");
  private DbDataSource dsSifrant;

  /** Creates a new instance of DbSifrantModel */
  public DbSifrantModel() throws SQLException {
    this("Ni dolo�en");
  }

  public DbSifrantModel(String textNotDefined) throws SQLException {
    this(textNotDefined, "");
  }

  public DbSifrantModel(String textNotDefined, String dataBase) throws SQLException {
    this(textNotDefined, dataBase, null, null);
  }

  public DbSifrantModel(String textNotDefined, String dataBase, List<String> allowedValues, List<String> excludedValues) throws SQLException {
    super(null, "IdSifre", new String[]{"Opis"});
    if (textNotDefined == null) {
      textNotDefined = "Ni dolo�en";
    }
    this.dataBase = dataBase;
    if (allowedValues != null) {
      this.allowedValues = allowedValues.toArray(new String[allowedValues.size()]);
    }
    if (excludedValues != null) {
      this.excludedValues = excludedValues.toArray(new String[excludedValues.size()]);
    }
    init();
  }

  private void init() throws SQLException {
    fValidDate.setValue("?");
    fValidDate.addParameter(validDate);

    fGroup.setFilterRequired(true);
    fGroup.addRequired(fGroup.I_TYPE_OPIS_SIFRANTA, 2);
    fGroup.addRequired(fGroup.I_TYPE_SKUPINA_SIFRANTA, 2);

    StringBuilder values = new StringBuilder();

    if (allowedValues != null && allowedValues.length > 0) {
      StringBuilder sbAllowed = new StringBuilder();
      for (String allowedV : allowedValues) {
        sbAllowed.append(sbAllowed.length() > 0 ? " , " : " ").append("'").append(allowedV).append("' ");
      }
      values.append(" Sifranti.IdSifre IN ( ").append(sbAllowed).append(" ) AND ");
    }
    if (excludedValues != null && excludedValues.length > 0) {
      StringBuilder sbExclude = new StringBuilder();
      for (String excludedV : excludedValues) {
        sbExclude.append(sbExclude.length() > 0 ? " , " : " ").append("'").append(excludedV).append("' ");
      }
      values.append(" Sifranti.IdSifre NOT IN ( ").append(sbExclude).append(" ) AND ");
    }


    valuesConstraint.setValue(values.toString());
    java.util.List parameters = new java.util.ArrayList();

    parameters.add(fNotDefined);
    parameters.add(fGroup);
    parameters.add(valuesConstraint);
    parameters.add(fValidDate);
    parameters.add(fValidDate);
    parameters.add(fValidDate);
    parameters.add(fValidDate);

    if (SqlUtilities.getInstance() == null) {
      this.dsSifrant = new DbDataSource();
    } else {
      this.dsSifrant = SqlUtilities.getInstance().getDsSifrantModel(dataBase, parameters);
    }

    this.dsSifrant.setName("SIFRANT");

    validDate.addPropertyChangeListener("value", new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        dsSifrant.reload();
      }
    });

//    fNotDefined.addDataSource(dsSifrant);
    if (!java.beans.Beans.isDesignTime()) {
      fGroup.addDataSource(dsSifrant);
    }

    super.setDataSource(dsSifrant);
  }

  @Override
  protected void updateEntries(ListDataEvent e) {
    if (sifrantSkupina != null && sifrantOpis != null) {
      super.updateEntries(e);
    }
  }

  private static class SifrantiDataSourceDescriptionFilter extends com.openitech.db.filters.DataSourceFilters {

    public final com.openitech.db.filters.DataSourceFilters.SeekType I_TYPE_NOT_DEFINED = new com.openitech.db.filters.DataSourceFilters.SeekType("cast(? as varchar(700))", com.openitech.db.filters.DataSourceFilters.SeekType.PREFORMATTED, 1);

    public SifrantiDataSourceDescriptionFilter() {
      super("<%DbSifrantModelDescription%>");
      setOperator("");
      setFilterRequired(true);
      addRequired(I_TYPE_NOT_DEFINED);

      I_TYPE_NOT_DEFINED.setCaseSensitive(true);
      setSeekValue(I_TYPE_NOT_DEFINED, "Ni dolo�en");
    }
  }

  private static class SifrantiDataSourceFilters extends com.openitech.db.filters.DataSourceFilters {

    public final com.openitech.db.filters.DataSourceFilters.SeekType I_TYPE_SKUPINA_SIFRANTA = new com.openitech.db.filters.DataSourceFilters.SeekType("SeznamSifrantov.Skupina", com.openitech.db.filters.DataSourceFilters.SeekType.UPPER_EQUALS, 1);
    public final com.openitech.db.filters.DataSourceFilters.SeekType I_TYPE_OPIS_SIFRANTA = new com.openitech.db.filters.DataSourceFilters.SeekType("SeznamSifrantov.Opis", com.openitech.db.filters.DataSourceFilters.SeekType.UPPER_EQUALS, 1);

    public SifrantiDataSourceFilters() {
      super("<%DbSifrantModelFilter%>");
      setOperator("");
      setFilterRequired(true);
      addRequired(I_TYPE_SKUPINA_SIFRANTA, 2);
      addRequired(I_TYPE_OPIS_SIFRANTA, 2);
      I_TYPE_SKUPINA_SIFRANTA.setConvertToVarchar(false);
      I_TYPE_OPIS_SIFRANTA.setConvertToVarchar(false);
    }

    public void setSeekValue(String sifrantSkupina, String sifrantOpis) {
      if (sifrantSkupina != null && sifrantOpis != null) {
        if (!seek_types.contains(I_TYPE_SKUPINA_SIFRANTA)) {
          seek_types.add(I_TYPE_SKUPINA_SIFRANTA);
        }
        if (!seek_types.contains(I_TYPE_OPIS_SIFRANTA)) {
          seek_types.add(I_TYPE_OPIS_SIFRANTA);
        }
        boolean update = I_TYPE_SKUPINA_SIFRANTA.setValue(sifrantSkupina);
        update = I_TYPE_OPIS_SIFRANTA.setValue(sifrantOpis) || update;
        if (update) {
          setParameters(true);
        }
      }
    }
  }
  /**
   * Holds value of property textNotDefined.
   */
  private String textNotDefined = "Ni dolo�en";

  /**
   * Getter for property textNotDefined.
   * @return Value of property textNotDefined.
   */
  public String getTextNotDefined() {
    return this.textNotDefined;
  }

  /**
   * Setter for property textNotDefined.
   * @param textNotDefined New value of property textNotDefined.
   */
  public void setTextNotDefined(String textNotDefined) {
    textNotDefined = textNotDefined == null || textNotDefined.length() <= 1 ? "Ni dolo�en" : textNotDefined;
    if (!textNotDefined.equals(this.textNotDefined)) {
      this.textNotDefined = textNotDefined;
      fNotDefined.setSeekValue(fNotDefined.I_TYPE_NOT_DEFINED, textNotDefined);
      if (sifrantSkupina != null && sifrantOpis != null) {
        fGroup.reloadDataSources();
      }
    }
  }
  /**
   * Holds value of property sifrantSkupina.
   */
  private String sifrantSkupina;

  /**
   * Getter for property sifrantSkupina.
   * @return Value of property sifrantSkupina.
   */
  public String getSifrantSkupina() {
    return this.sifrantSkupina;
  }

  /**
   * Setter for property sifrantSkupina.
   * @param sifrantSkupina New value of property sifrantSkupina.
   */
  public void setSifrantSkupina(String sifrantSkupina) {
    this.sifrantSkupina = sifrantSkupina;
    this.dsSifrant.setName("SIFRANT:" + sifrantSkupina + "-" + sifrantOpis);
    fGroup.setSeekValue(sifrantSkupina, sifrantOpis);
  }
  /**
   * Holds value of property sifrantOpis.
   */
  private String sifrantOpis;

  /**
   * Getter for property sifrantOpis.
   * @return Value of property sifrantOpis.
   */
  public String getSifrantOpis() {
    return this.sifrantOpis;
  }

  /**
   * Setter for property sifrantOpis.
   * @param sifrantOpis New value of property sifrantOpis.
   */
  public void setSifrantOpis(String sifrantOpis) {
    this.sifrantOpis = sifrantOpis;
    this.dsSifrant.setName("SIFRANT:" + sifrantSkupina + "-" + sifrantOpis);
    fGroup.setSeekValue(sifrantSkupina, sifrantOpis);
  }
  private String dataBase;
  private String[] allowedValues;

  /**
   * Get the value of allowedValues
   *
   * @return the value of allowedValues
   */
  public String[] getAllowedValues() {
    return allowedValues;
  }

  /**
   * Set the value of allowedValues
   *
   * @param allowedValues new value of allowedValues
   */
  public void setAllowedValues(String... allowedValues) {
    this.allowedValues = allowedValues;
  }
  private String[] excludedValues;

  /**
   * Get the value of excludedValues
   *
   * @return the value of excludedValues
   */
  public String[] getExcludedValues() {
    return excludedValues;
  }

  /**
   * Set the value of excludedValues
   *
   * @param excludedValues new value of excludedValues
   */
  public void setExcludedValues(String... excludedValues) {
    this.excludedValues = excludedValues;
  }
  protected Date datumVeljavnosti = Calendar.getInstance().getTime();

  /**
   * Get the value of datumVeljavnosti
   *
   * @return the value of datumVeljavnosti
   */
  public Date getDatumVeljavnosti() {
    return datumVeljavnosti;
  }

  /**
   * Set the value of datumVeljavnosti
   *
   * @param datumVeljavnosti new value of datumVeljavnosti
   */
  public void setDatumVeljavnosti(Date datumVeljavnosti) {
    this.datumVeljavnosti = datumVeljavnosti;
    this.validDate.setValue(new java.sql.Date(datumVeljavnosti.getTime()));
  }
}
