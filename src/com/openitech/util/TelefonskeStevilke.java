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
  private static final String TL_SLOVENIJA = "386";

  public static Telefon getTelefon(String telefonskaStevilka) {
    StringBuffer digits = new StringBuffer(telefonskaStevilka.length());
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

  public static class Telefon {

    public static final int[] OMREZNE_GSM = new int[]{
      030,
      031,
      040,
      041,
      050,
      051,
      064,
      070
    };
    private boolean gsm = false;
    private boolean tujina = false;
    private String omrezna;
    private String telefonska;
    private String drzava = TL_SLOVENIJA;

    public Telefon() {
      this(null, null);
    }

    public Telefon(String omrezna, String telefonska) {
      this(TL_SLOVENIJA, omrezna, telefonska);
    }

    public Telefon(String drzava, String omrezna, String telefonska) {
      setDrzava(drzava);
      setOmrezna(omrezna);
      setTelefonska(telefonska);
    }

    /**
     * Get the value of drzava
     *
     * @return the value of drzava
     */
    public String getDrzava() {
      return drzava;
    }

    /**
     * Set the value of drzava
     *
     * @param drzava new value of drzava
     */
    public void setDrzava(String drzava) {
      this.drzava = drzava;
      tujina = !(TL_SLOVENIJA.equals(drzava));
    }

    /**
     * Get the value of telefonska
     *
     * @return the value of telefonska
     */
    public String getTelefonska() {
      return telefonska;
    }

    /**
     * Set the value of telefonska
     *
     * @param telefonska new value of telefonska
     */
    public void setTelefonska(String telefonska) {
      this.telefonska = telefonska;
    }

    /**
     * Get the value of omrezna
     *
     * @return the value of omrezna
     */
    public String getOmrezna() {
      return omrezna;
    }

    /**
     * Set the value of omrezna
     *
     * @param omrezna new value of omrezna
     */
    public void setOmrezna(String omrezna) {
      this.omrezna = omrezna;
      if (omrezna != null) {
        int os = Integer.parseInt(omrezna);
        boolean is_gsm = false;
        for (int omrezne : OMREZNE_GSM) {
          if (!is_gsm) {
            is_gsm = os == omrezne;
          }
        }
        this.gsm = is_gsm;
      }
    }

    /**
     * Get the value of tujina
     *
     * @return the value of tujina
     */
    public boolean isTujina() {
      return tujina;
    }

    /**
     * Get the value of gsm
     *
     * @return the value of gsm
     */
    public boolean isGsm() {
      return gsm || (!tujina);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Telefon other = (Telefon) obj;
      if ((this.omrezna == null) ? (other.omrezna != null) : !this.omrezna.equalsIgnoreCase(other.omrezna)) {
        return false;
      }
      if ((this.telefonska == null) ? (other.telefonska != null) : !this.telefonska.equalsIgnoreCase(other.telefonska)) {
        return false;
      }
      if ((this.drzava == null) ? (other.drzava != null) : !this.drzava.equalsIgnoreCase(other.drzava)) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 5;
      hash = 97 * hash + (this.omrezna != null ? this.omrezna.toUpperCase().hashCode() : 0);
      hash = 97 * hash + (this.telefonska != null ? this.telefonska.toUpperCase().hashCode() : 0);
      hash = 97 * hash + (this.drzava != null ? this.drzava.toUpperCase().hashCode() : 0);
      return hash;
    }
  }
}
