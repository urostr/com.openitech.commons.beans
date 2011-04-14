/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.util.rpp;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.io.ReadInputStream;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.StringValue;
import com.openitech.value.fields.Field;
import com.openitech.value.fields.FieldValue;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author domenbasic
 */
public class UtilRPP {

  private static final String findKontakt = ReadInputStream.getResourceAsString(UtilRPP.class, "findKontakt.sql", "cp1250");
  private static final String findEmail = ReadInputStream.getResourceAsString(UtilRPP.class, "findEmail.sql", "cp1250");
  public static boolean cachePreparedStatments = true;
  private static Map<String, PreparedStatement> psCache = new HashMap<String, PreparedStatement>();

  public static String getTelefonIDKontakta(Integer omrezna, Integer telefonska) throws SQLException {
    return getIDKontakta(omrezna, telefonska, "KTIP01");
  }

  public static String getGSMIDKontakta(Integer omrezna, Integer telefonska) throws SQLException {
    return getIDKontakta(omrezna, telefonska, "KTIP02");
  }

  private static String getIDKontakta(Integer omrezna, Integer telefonska, String tip) throws SQLException {
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

    if (omrezna != null && telefonska != null) {
      int param = 1;
      ps_findIdKontakta.clearParameters();
      ps_findIdKontakta.setInt(param++, omrezna);
      ps_findIdKontakta.setInt(param++, telefonska);
      ps_findIdKontakta.setString(param++, tip);
      ResultSet rs_findIdKontakta = ps_findIdKontakta.executeQuery();
      if (rs_findIdKontakta.next()) {
        idKontakta = rs_findIdKontakta.getString("ID_KONTAKTA");
      }
    }
    if (idKontakta == null) {
      idKontakta = getNextIdKontakta();
    }
    return idKontakta;
  }

  private static String getNextIdKontakta() throws SQLException {
    String idKontakta;
    FieldValue nextIdentity = SqlUtilities.getInstance().getNextIdentity(new Field("ID_KONTAKTA", java.sql.Types.VARCHAR));
    idKontakta = (String) nextIdentity.getValue();
    return idKontakta;
  }

  public static String getEmailIDKontakta(String email) throws SQLException {
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
