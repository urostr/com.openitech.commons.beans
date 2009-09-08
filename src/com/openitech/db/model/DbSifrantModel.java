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
import java.sql.SQLException;
import javax.swing.event.ListDataEvent;

/**
 *
 * @author uros
 */
public class DbSifrantModel extends DbComboBoxModel<String> {

  private final DbSifrantModel.SifrantiDataSourceDescriptionFilter fNotDefined = new DbSifrantModel.SifrantiDataSourceDescriptionFilter();
  private final DbSifrantModel.SifrantiDataSourceFilters fGroup = new DbSifrantModel.SifrantiDataSourceFilters();
  private DbDataSource dsSifrant;

  /** Creates a new instance of DbSifrantModel */
  public DbSifrantModel() throws SQLException {
    this("Ni doloèen");
  }

  public DbSifrantModel(String textNotDefined) throws SQLException {
    this(textNotDefined, "");
  }

  public DbSifrantModel(String textNotDefined, String dataBase) throws SQLException {
    super(null, "IdSifre", new String[]{"Opis"});
    if (textNotDefined == null) {
      textNotDefined = "Ni doloèen";
    }
    this.dataBase = dataBase;
    init();
  }

  private void init() throws SQLException {
    fGroup.setFilterRequired(true);
    fGroup.addRequired(fGroup.I_TYPE_OPIS_SIFRANTA, 2);
    fGroup.addRequired(fGroup.I_TYPE_SKUPINA_SIFRANTA, 2);

    java.util.List parameters = new java.util.ArrayList();

    parameters.add(fNotDefined);
    parameters.add(fGroup);

    if (SqlUtilities.getInstance() == null) {
      this.dsSifrant = new DbDataSource();
    } else {
      this.dsSifrant = SqlUtilities.getInstance().getDsSifrantModel(dataBase, parameters);
    }

    this.dsSifrant.setName("SIFRANT");

//    fNotDefined.addDataSource(dsSifrant);
    fGroup.addDataSource(dsSifrant);

    super.setDataSource(dsSifrant);
  }
  protected void updateEntries(ListDataEvent e) {
    if (sifrantSkupina != null && sifrantOpis != null) {
      super.updateEntries(e);
    }
  }

  private static class SifrantiDataSourceDescriptionFilter extends com.openitech.db.filters.DataSourceFilters {

    public final com.openitech.db.filters.DataSourceFilters.SeekType I_TYPE_NOT_DEFINED = new com.openitech.db.filters.DataSourceFilters.SeekType("cast(? as varchar)", com.openitech.db.filters.DataSourceFilters.SeekType.PREFORMATTED, 1);

    public SifrantiDataSourceDescriptionFilter() {
      super("<%DbSifrantModelDescription%>");
      setOperator("");
      setFilterRequired(true);
      addRequired(I_TYPE_NOT_DEFINED);

      I_TYPE_NOT_DEFINED.setCaseSensitive(true);
      setSeekValue(I_TYPE_NOT_DEFINED, "Ni doloèen");
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
    textNotDefined = textNotDefined == null || textNotDefined.length() <= 1 ? "Ni doloèen" : textNotDefined;
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
}
