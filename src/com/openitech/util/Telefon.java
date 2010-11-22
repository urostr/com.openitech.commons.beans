/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.util;

/**
 *
 * @author domenbasic
 */
public class Telefon {

  public static final int[] OMREZNE_GSM = new int[]{
    30,
    31,
    40,
    41,
    50,
    51,
    64,
    70
  };
  private boolean gsm = false;
  private boolean tujina = false;
  private String omrezna;
  private String telefonska;
  private String drzava = TelefonskeStevilke.TL_SLOVENIJA;

  public Telefon() {
    this(null, null);
  }

  public Telefon(String omrezna, String telefonska) {
    this(TelefonskeStevilke.TL_SLOVENIJA, omrezna, telefonska);
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
    tujina = !(TelefonskeStevilke.TL_SLOVENIJA.equals(drzava));
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
    return gsm && (!tujina);
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
