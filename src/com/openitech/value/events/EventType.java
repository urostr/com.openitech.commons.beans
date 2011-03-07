package com.openitech.value.events;

public class EventType {

  public EventType(Event event) {
    this.sifrant = event.getSifrant();
    this.sifra = event.getSifra();
  }

  public EventType(int sifrant, String sifra) {
    this.sifrant = sifrant;
    this.sifra = sifra;
  }
  protected final int sifrant;

  /**
   * Get the value of sifrant
   *
   * @return the value of sifrant
   */
  public int getSifrant() {
    return sifrant;
  }
  protected final String sifra;

  /**
   * Get the value of sifra
   *
   * @return the value of sifra
   */
  public String getSifra() {
    return sifra;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final EventType other = (EventType) obj;
    if (this.sifrant != other.sifrant) {
      return false;
    }
    if ((this.sifra == null) ? (other.sifra != null) : !this.sifra.equals(other.sifra)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 53 * hash + this.sifrant;
    hash = 53 * hash + (this.sifra != null ? this.sifra.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    return "EventType:" + sifrant + "-" + sifra;
  }
}
