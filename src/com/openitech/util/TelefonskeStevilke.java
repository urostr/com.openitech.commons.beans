/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author domenbasic
 */
public class TelefonskeStevilke {

  private static final Pattern PT_TELEFONSKA = Pattern.compile("^((00|\\+)((1\\s?(340|670|787|868))|7|20|27|3[0-4]|36|39|4[0-1]|4[3-9]|5[1-8]|6[0-6]|8[1-2]|84|86|9[0-5]|98|21[2-3]|216|218|2[2-4][0-9]|25[0-8]|26[0-9]|284|29[0-1]|29[7-8]|35[0-9]|37[0-6]|378|38[0-1]|38[5-7]|389|42[0-1]|423|473|50[0-9]|59[0-9]|6[7-8][0-9]|69[0-2]|767|809|850|852|853|855|869|876|880|886|96[0-8]|97[1-7]|99[3-6]))??(((0)?([3-5][0-1]|64|[7-9]0|59[0-2]|597|599|817|[1-8]))(\\D*)(\\d+))");
  public static final String TL_SLOVENIJA = "386";

  public static Telefon getTelefon(String telefonskaStevilka) {
    StringBuilder digits = new StringBuilder(telefonskaStevilka.length());
    for (int pos = 0; pos < telefonskaStevilka.length(); pos++) {
      if (Character.isDigit(telefonskaStevilka.charAt(pos)) || (telefonskaStevilka.charAt(pos) == '+')) {
        digits.append(telefonskaStevilka.charAt(pos));
      }
    }
    Matcher m_telefonska = PT_TELEFONSKA.matcher(digits);
    if (m_telefonska.matches()) {
      Telefon result = new Telefon();
      result.setDrzava(m_telefonska.group(3) == null ? TL_SLOVENIJA : m_telefonska.group(3));
      if (result.getDrzava().equals(TL_SLOVENIJA)) {
        result.setOmrezna(m_telefonska.group(9));
        result.setTelefonska(m_telefonska.group(11));
      } else {
        result.setTelefonska(m_telefonska.group(6));
      }

      return result;
    } else {
      return null;
    }
  }
}
