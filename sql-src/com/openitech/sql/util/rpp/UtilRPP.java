/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.util.rpp;

import com.openitech.db.components.DbNaslovDataModel.Naslov;
import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.io.ReadInputStream;
import com.openitech.sql.cache.CachedTemporaryTablesManager;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.fields.Field;
import com.openitech.value.fields.FieldValue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author domenbasic
 */
public class UtilRPP {

  private final String findKontakt = ReadInputStream.getResourceAsString(UtilRPP.class, "findKontakt.sql", "cp1250");
  private final String findEmail = ReadInputStream.getResourceAsString(UtilRPP.class, "findEmail.sql", "cp1250");
  private final String findRPPGsm = ReadInputStream.getResourceAsString(UtilRPP.class, "findRPPGsm.sql", "cp1250");
  private final String findRPPTelefon = ReadInputStream.getResourceAsString(UtilRPP.class, "findRPPTelefon.sql", "cp1250");
  private final String findRPPEmail = ReadInputStream.getResourceAsString(UtilRPP.class, "findRPPEmail.sql", "cp1250");
  public static final boolean cachePreparedStatments = true;
  private Map<String, PreparedStatement> psCache = new HashMap<String, PreparedStatement>();
  private static UtilRPP instance;
  private boolean isNew = true;

  public static UtilRPP getInstance() {
    if (instance == null) {
      instance = new UtilRPP();
    }
    return instance;
  }

  private UtilRPP() {
    try {
      TemporarySubselectSqlParameter ttKontakti = CachedTemporaryTablesManager.getInstance().getCachedTemporarySubselectSqlParameter(UtilRPP.class, "kontakti.xml");
      ttKontakti.executeQuery(ConnectionManager.getInstance().getTxConnection(), new ArrayList<Object>());
      TemporarySubselectSqlParameter ttEmail = CachedTemporaryTablesManager.getInstance().getCachedTemporarySubselectSqlParameter(UtilRPP.class, "email.xml");
      ttEmail.executeQuery(ConnectionManager.getInstance().getTxConnection(), new ArrayList<Object>());
    } catch (SQLException ex) {
      Logger.getLogger(UtilRPP.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(UtilRPP.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public String getTelefonIDKontakta(Integer omrezna, Integer telefonska) throws SQLException {
    return getIDKontakta(omrezna, telefonska, "KTIP01");
  }

  public String getGSMIDKontakta(Integer omrezna, Integer telefonska) throws SQLException {
    return getIDKontakta(omrezna, telefonska, "KTIP02");
  }

  private synchronized String getIDKontakta(Integer omrezna, Integer telefonska, String tip) throws SQLException {
    String idKontakta = null;
    PreparedStatement ps_findIdKontakta = null;


    if (cachePreparedStatments) {
      if (psCache.containsKey(findKontakt)) {
        ps_findIdKontakta = psCache.get(findKontakt);
      } else {
        ps_findIdKontakta = ConnectionManager.getInstance().getTxConnection().prepareStatement(findKontakt);
        psCache.put(findKontakt, ps_findIdKontakta);
      }
    } else {
      ps_findIdKontakta = ConnectionManager.getInstance().getTxConnection().prepareStatement(findKontakt);
    }
    try {
      if (omrezna != null && telefonska != null) {
        int param = 1;
        ps_findIdKontakta.clearParameters();
        ps_findIdKontakta.setInt(param++, omrezna);
        ps_findIdKontakta.setInt(param++, telefonska);
        ps_findIdKontakta.setString(param++, tip);
        ResultSet rs_findIdKontakta = ps_findIdKontakta.executeQuery();
        try {
          if (rs_findIdKontakta.next()) {
            idKontakta = rs_findIdKontakta.getString("ID_KONTAKTA");
          }
        } finally {
          rs_findIdKontakta.close();
        }
      }
    } finally {
      if (!cachePreparedStatments) {
        ps_findIdKontakta.close();
      }
    }
    if (idKontakta == null) {
      isNew = true;
      idKontakta = getNextIdKontakta();
    } else {
      isNew = false;
    }
    return idKontakta;
  }

  private String getNextIdKontakta() throws SQLException {
    String idKontakta;
    FieldValue nextIdentity = SqlUtilities.getInstance().getNextIdentity(new Field("ID_KONTAKTA", java.sql.Types.VARCHAR));
    idKontakta = (String) nextIdentity.getValue();
    return idKontakta;
  }

  public synchronized String getEmailIDKontakta(String email) throws SQLException {
    String idKontakta = null;
    PreparedStatement ps_findIdKontakta = null;
    if (cachePreparedStatments) {
      if (psCache.containsKey(findEmail)) {
        ps_findIdKontakta = psCache.get(findEmail);
      } else {
        ps_findIdKontakta = ConnectionManager.getInstance().getTxConnection().prepareStatement(findEmail);
        psCache.put(findEmail, ps_findIdKontakta);
      }
    } else {
      ps_findIdKontakta = ConnectionManager.getInstance().getTxConnection().prepareStatement(findEmail);
    }

    int param = 1;
    ps_findIdKontakta.clearParameters();
    ps_findIdKontakta.setString(param++, email);
    ResultSet rs_findIdKontakta = ps_findIdKontakta.executeQuery();
    if (rs_findIdKontakta.next()) {
      idKontakta = rs_findIdKontakta.getString("ID_KONTAKTA");
    }
    if (idKontakta == null) {
      isNew = true;
      idKontakta = getNextIdKontakta();
    } else {
      isNew = false;
    }
    return idKontakta;
  }

  public synchronized boolean isNewIdKontakta() {
    return isNew;
  }

  public synchronized GSMKontakt getRppGsm(int ppID) throws SQLException {
    GSMKontakt result = null;

    PreparedStatement ps_findRPPGsm = null;

    if (cachePreparedStatments) {
      if (psCache.containsKey(findRPPGsm)) {
        ps_findRPPGsm = psCache.get(findRPPGsm);
      } else {
        ps_findRPPGsm = ConnectionManager.getInstance().getTxConnection().prepareStatement(findRPPGsm);
        psCache.put(findRPPGsm, ps_findRPPGsm);
      }
    } else {
      ps_findRPPGsm = ConnectionManager.getInstance().getTxConnection().prepareStatement(findRPPGsm);
    }

    try {
      int param = 1;
      ps_findRPPGsm.setInt(param++, ppID);
      ResultSet rs_findRPPGsm = ps_findRPPGsm.executeQuery();
      try {
        if (rs_findRPPGsm.next()) {
          Integer ppKontaktID = rs_findRPPGsm.getInt("PPKontaktID");
          if (rs_findRPPGsm.wasNull()) {
            ppKontaktID = null;
          }
          Integer os = rs_findRPPGsm.getInt("PPOmreznaGSM");
          if (rs_findRPPGsm.wasNull()) {
            os = null;
          }
          Integer stevilka = rs_findRPPGsm.getInt("PPStevilkaGSM");
          if (rs_findRPPGsm.wasNull()) {
            stevilka = null;
          }

          result = new GSMKontakt(ppID, ppKontaktID, os, stevilka);

        }
      } finally {
        rs_findRPPGsm.close();
      }
    } finally {
      if (!cachePreparedStatments) {
        ps_findRPPGsm.close();
      }
    }
    return result;
  }

  public synchronized TelefonKontakt getRppTelefon(int ppID) throws SQLException {
    TelefonKontakt result = null;

    PreparedStatement ps_findRPPTelefon = null;

    if (cachePreparedStatments) {
      if (psCache.containsKey(findRPPTelefon)) {
        ps_findRPPTelefon = psCache.get(findRPPTelefon);
      } else {
        ps_findRPPTelefon = ConnectionManager.getInstance().getTxConnection().prepareStatement(findRPPTelefon);
        psCache.put(findRPPTelefon, ps_findRPPTelefon);
      }
    } else {
      ps_findRPPTelefon = ConnectionManager.getInstance().getTxConnection().prepareStatement(findRPPTelefon);
    }

    try {
      int param = 1;
      ps_findRPPTelefon.setInt(param++, ppID);
      ResultSet rs_findRPPTelefon = ps_findRPPTelefon.executeQuery();
      try {
        if (rs_findRPPTelefon.next()) {
          Integer ppKontaktID = rs_findRPPTelefon.getInt("PPKontaktID");
          if (rs_findRPPTelefon.wasNull()) {
            ppKontaktID = null;
          }
          Integer os = rs_findRPPTelefon.getInt("PPOmreznaTelefon");
          if (rs_findRPPTelefon.wasNull()) {
            os = null;
          }
          Integer stevilka = rs_findRPPTelefon.getInt("PPStevilkaTelefon");
          if (rs_findRPPTelefon.wasNull()) {
            stevilka = null;
          }

          result = new TelefonKontakt(ppID, ppKontaktID, os, stevilka);

        }
      } finally {
        rs_findRPPTelefon.close();
      }
    } finally {
      if (!cachePreparedStatments) {
        ps_findRPPTelefon.close();
      }
    }
    return result;
  }

  public synchronized EmailKontakt getRppEmail(int ppID) throws SQLException {
    EmailKontakt result = null;

    PreparedStatement ps_findRPPEmail = null;

    if (cachePreparedStatments) {
      if (psCache.containsKey(findRPPEmail)) {
        ps_findRPPEmail = psCache.get(findRPPEmail);
      } else {
        ps_findRPPEmail = ConnectionManager.getInstance().getTxConnection().prepareStatement(findRPPEmail);
        psCache.put(findRPPEmail, ps_findRPPEmail);
      }
    } else {
      ps_findRPPEmail = ConnectionManager.getInstance().getTxConnection().prepareStatement(findRPPEmail);
    }

    try {
      int param = 1;
      ps_findRPPEmail.setInt(param++, ppID);
      ResultSet rs_findRPPEmail = ps_findRPPEmail.executeQuery();
      try {
        if (rs_findRPPEmail.next()) {
          Integer ppKontaktID = rs_findRPPEmail.getInt("PPKontaktID");
          if (rs_findRPPEmail.wasNull()) {
            ppKontaktID = null;
          }
          String email = rs_findRPPEmail.getString("PPEMAIL");

          result = new EmailKontakt(ppID, ppKontaktID, email);

        }
      } finally {
        rs_findRPPEmail.close();
      }
    } finally {
      if (!cachePreparedStatments) {
        ps_findRPPEmail.close();
      }
    }
    return result;
  }

  public static class GSMKontakt extends Kontakt {

    private final int ppTipontaktaID = 2;

    public GSMKontakt(int ppID, Integer ppKontaktID, Integer os, Integer stevilka) {
      super(ppID, ppKontaktID, os, stevilka);
    }
  }

  public static class TelefonKontakt extends Kontakt {

    private final int ppTipontaktaID = 1;

    public TelefonKontakt(int ppID, Integer ppKontaktID, Integer os, Integer stevilka) {
      super(ppID, ppKontaktID, os, stevilka);
    }
  }

  public static class EmailKontakt extends Kontakt {

    private final int ppTipontaktaID = 3;

    public EmailKontakt(int ppID, Integer ppKontaktID, String email) {
      super(ppID, ppKontaktID, email);
    }

    @Override
    public String getEmail() {
      return super.getEmail();
    }
  }

  private static class Kontakt {

    private int ppID;
    private Integer ppKontaktID = null;
    private Integer os = null;
    private Integer stevilka = null;
    private String email = null;

    protected Kontakt(int ppID, Integer ppKontaktID, Integer os, Integer stevilka) {
      this.ppID = ppID;
      this.ppKontaktID = ppKontaktID;
      this.os = os;
      this.stevilka = stevilka;
    }

    protected Kontakt(int ppID, Integer ppKontaktID, String email) {
      this.ppID = ppID;
      this.ppKontaktID = ppKontaktID;
      this.email = email;
    }

    public int getPPID() {
      return ppID;
    }

    public Integer getPPKontaktID() {
      return ppKontaktID;
    }

    public Integer getOs() {
      return os;
    }

    public Integer getStevilka() {
      return stevilka;
    }

    protected String getEmail() {
      return email;
    }
  }
  private PreparedStatement findHS_MID_3 = null;

  public Integer getHS_mid(String ul_ime, String hs_hd, String pt_id, String na_ime) throws SQLException {
    Integer result = null;
    ul_ime = ul_ime == null ? null : ul_ime.toUpperCase();
    na_ime = na_ime == null ? null : na_ime.toUpperCase();
    hs_hd = hs_hd == null ? null : hs_hd.replaceAll(" ", "");
    pt_id = pt_id == null || pt_id.length() == 0 ? null : pt_id;
    long timer;

    if (findHS_MID_3 == null) {
      final Connection connection = ConnectionManager.getInstance().getConnection();
      findHS_MID_3 = connection.prepareStatement(ReadInputStream.getResourceAsString(getClass(), "find_hs_mid_3.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    int param = 1;

    param = 1;
    findHS_MID_3.clearParameters();
    findHS_MID_3.setString(param++, hs_hd);
    findHS_MID_3.setString(param++, ul_ime);
    findHS_MID_3.setObject(param++, pt_id, Types.INTEGER);
    timer = System.currentTimeMillis();
    ResultSet rsHS_MID_3 = findHS_MID_3.executeQuery();
    try {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "findHS_MID_3: {0}ms", (System.currentTimeMillis() - timer));
      if (rsHS_MID_3.next() && rsHS_MID_3.isLast()) {
        result = rsHS_MID_3.getInt(1);
      }
    } finally {
      rsHS_MID_3.close();
    }
    return result;
  }
  private final String insertPP = ReadInputStream.getResourceAsString(getClass(), "insertPP.sql", "cp1250");

  public int insertPP() throws SQLException {
    int result = -1;
    SqlUtilities sqlUtilities = SqlUtilities.getInstance();
    PreparedStatement ps_insertPP = ConnectionManager.getInstance().getTxConnection().prepareStatement(insertPP);
    try {
      ps_insertPP.execute();
      result = (int) sqlUtilities.getLastIdentity();
    } catch (SQLException ex) {
      throw ex;
    } finally {
      ps_insertPP.close();
    }
    return result;
  }
}
