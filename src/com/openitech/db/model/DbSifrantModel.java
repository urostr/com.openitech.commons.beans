/*
 * DbSifrantModel.java
 *
 * Created on Ponedeljek, 11 avgust 2008, 10:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.model;

import java.sql.SQLException;

/**
 *
 * @author uros
 */
public class DbSifrantModel extends DbComboBoxModel<String> {
  private final DbSifrantModel.SifrantiDataSourceDescriptionFilter fNotDefined = new DbSifrantModel.SifrantiDataSourceDescriptionFilter();
  private final DbSifrantModel.SifrantiDataSourceFilters           fGroup      = new DbSifrantModel.SifrantiDataSourceFilters();
  private final DbDataSource dsSifrant = new DbDataSource();
  
  /** Creates a new instance of DbSifrantModel */
  public DbSifrantModel() throws SQLException {
    this("Ni doloèen");
  }
  
  public DbSifrantModel(String textNotDefined) throws SQLException {
    super(null, "IdSifre", new String[] {"Opis"});
    init();
  }
  
  private void init() throws SQLException  {
    dsSifrant.setCanAddRows(false);
    dsSifrant.setCanDeleteRows(false);
    dsSifrant.setReadOnly(true);
    
    java.util.List parameters = new  java.util.ArrayList();

    parameters.add(fNotDefined);
    parameters.add(fGroup);
    
    dsSifrant.setParameters(parameters);
    dsSifrant.setCountSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sifrant_c.sql", "cp1250"));
    dsSifrant.setSelectSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sifrant.sql", "cp1250"));
    dsSifrant.setQueuedDelay(0);
    
    fNotDefined.addDataSource(dsSifrant);
    fGroup.addDataSource(dsSifrant);
    
    super.setDataSource(dsSifrant);
  }
  
  private static class SifrantiDataSourceDescriptionFilter extends com.openitech.db.model.DataSourceFilters {
    public final com.openitech.db.model.DataSourceFilters.SeekType I_TYPE_NOT_DEFINED = new com.openitech.db.model.DataSourceFilters.SeekType("cast(? as varchar)", com.openitech.db.model.DataSourceFilters.SeekType.PREFORMATTED, 1);
    
    public SifrantiDataSourceDescriptionFilter() {
      super("<%DbSifrantModelDescription%>");
      setOperator("");
      setFilterRequired(true);
      addRequired(I_TYPE_NOT_DEFINED);

      I_TYPE_NOT_DEFINED.setCaseSensitive(true);
      setSeekValue(I_TYPE_NOT_DEFINED, "Ni doloèen");
    }
  }
  
  
  private static class SifrantiDataSourceFilters extends com.openitech.db.model.DataSourceFilters {
    public final com.openitech.db.model.DataSourceFilters.SeekType I_TYPE_SKUPINA_SIFRANTA = new com.openitech.db.model.DataSourceFilters.SeekType("SeznamSifrantov.Skupina", com.openitech.db.model.DataSourceFilters.SeekType.UPPER_EQUALS, 1);
    public final com.openitech.db.model.DataSourceFilters.SeekType I_TYPE_OPIS_SIFRANTA    = new com.openitech.db.model.DataSourceFilters.SeekType("SeznamSifrantov.Opis", com.openitech.db.model.DataSourceFilters.SeekType.UPPER_EQUALS, 1);
    
    public SifrantiDataSourceFilters() {
      super("<%DbSifrantModelFilter%>");
      setOperator("AND");
      setFilterRequired(true);
      addRequired(I_TYPE_SKUPINA_SIFRANTA,2);
      addRequired(I_TYPE_OPIS_SIFRANTA,2);
    }
  }
  
  /**
   * Holds value of property textNotDefined.
   */
  private String textNotDefined = "Ni doloèen";
  
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
    this.textNotDefined = textNotDefined==null||textNotDefined.length()<=1?"Ni doloèen":textNotDefined;
    fNotDefined.setSeekValue(fNotDefined.I_TYPE_NOT_DEFINED, textNotDefined);
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
    fGroup.setSeekValue(fGroup.I_TYPE_SKUPINA_SIFRANTA, this.sifrantSkupina);
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
    fGroup.setSeekValue(fGroup.I_TYPE_OPIS_SIFRANTA, this.sifrantOpis);
  }
}
