/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.util.rpp;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.io.ReadInputStream;
import com.openitech.sql.cache.CachedTemporaryTablesManager;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.fields.Field;
import com.openitech.value.fields.FieldValue;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
  public static final boolean cachePreparedStatments = true;
  private Map<String, PreparedStatement> psCache = new HashMap<String, PreparedStatement>();
  private static UtilRPP instance;

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

  private String getIDKontakta(Integer omrezna, Integer telefonska, String tip) throws SQLException {
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
      idKontakta = getNextIdKontakta();
    }
    return idKontakta;
  }

  private String getNextIdKontakta() throws SQLException {
    String idKontakta;
    FieldValue nextIdentity = SqlUtilities.getInstance().getNextIdentity(new Field("ID_KONTAKTA", java.sql.Types.VARCHAR));
    idKontakta = (String) nextIdentity.getValue();
    return idKontakta;
  }

  public String getEmailIDKontakta(String email) throws SQLException {
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
      idKontakta = getNextIdKontakta();
    }
    return idKontakta;
  }
}
