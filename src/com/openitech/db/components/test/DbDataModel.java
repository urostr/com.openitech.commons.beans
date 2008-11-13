/*
 * DbDataModel.java
 *
 * Created on Petek, 21 julij 2006, 16:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.components.test;

import com.openitech.db.model.DbDataSource;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author uros
 */
public class DbDataModel {

  private static DbDataModel instance;    //correctly init the DbAuthService
  private static DbConnection dbConnection;
  /* 
   * ustvari nov DataSource,
   * da se lahko poveze na SQL
   */
  public final DbDataSource dsKadrovskaEvidenca = new DbDataSource();

  public static DbDataModel getInstance() {
    if (instance == null) {
      try {
        instance = new DbDataModel();
      } catch (SQLException ex) {
        Logger.getLogger(DbDataModel.class.getName()).log(Level.SEVERE, null, ex);
        System.exit(1);
      }
    }
    return instance;
  }

  public static com.openitech.db.DbConnection getDbConnection() {
    if (dbConnection == null) {
      dbConnection = new DbConnection();
    }
    return com.openitech.db.ConnectionManager.getInstance();
  }

  /** Creates a new instance of DbDataModel */
  private DbDataModel() throws SQLException {
    /*
     * nastavitve filtrov 
     */
    /** ------------filter KadrovskaEvidenca -----------------*/
    java.util.List parameters = new java.util.ArrayList();


    dsKadrovskaEvidenca.setParameters(parameters);
    /** povezava za datasource na SQL */
    dsKadrovskaEvidenca.setCountSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/KadrovskaEvidenca_c.sql", "cp1250"));
    dsKadrovskaEvidenca.setSelectSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/KadrovskaEvidenca.sql", "cp1250"));

    //ne shranjuje avtomatično , ker se mora izvšiti update na več tabelah v eni transakciji
    dsKadrovskaEvidenca.setUpdateRowFireOnly(true);
    /** setUpdateTableName(tabela, ki naj bi se spremenila) */
    dsKadrovskaEvidenca.setUniqueID(new String[]{"id"});
    dsKadrovskaEvidenca.setUpdateTableName("Zaposleni");
    dsKadrovskaEvidenca.addUpdateColumnName(
            "Ime",
            "Priimek",
            "MaticnaStevilka",
            "DatumRojstva",
            "KrajRojstva",
            "DrzavaRojstva",
            "Spol",
            "EMSO",
            "DavcnaStevilka",
            "Banka_IDSifranta",
            "Banka_IDSifre",
            "TRR",
            "Izobrazba_IDSifranta",
            "Izobrazba_IDSifre",
            "StopnjaIzob_IDSifranta",
            "StopnjaIzob_IDSifre",
            "Prevoz_IDSifranta",
            "Prevoz_IDSifre",
            "StOtrokDavcnaOl",
            "VarstvoPodatkov",
            "ZdravniskiPregled",
            "KolNezgZavarovanje");
  }

  /** class za nov filter */
}  
