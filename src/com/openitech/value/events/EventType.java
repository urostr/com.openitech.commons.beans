package com.openitech.value.events;

public class EventType {

  public EventType(Event event) {
    this.sifrant = event.getSifrant();
    this.sifra = event.getSifra();
    this.cacheOnUpdate = event.isCacheOnUpdate();
    this.indexedAsView = event.indexedAsView;
  }

  public EventType(int sifrant, String sifra) {
    this(sifrant, sifra, false, null);
  }

  public EventType(int sifrant, String sifra, boolean cacheOnUpdate, String indexedAsView) {
    this.sifrant = sifrant;
    this.sifra = sifra;
    this.cacheOnUpdate = cacheOnUpdate;
    this.indexedAsView = indexedAsView;
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
  protected final boolean cacheOnUpdate;

  /**
   * Get the value of cacheOnUpdate
   *
   * @return the value of cacheOnUpdate
   */
  public boolean isCacheOnUpdate() {
    return cacheOnUpdate;
  }
  
  protected String indexedAsView;

  /**
   * Get the value of indexedAsView
   *
   * @return the value of indexedAsView
   */
  public String getIndexedAsView() {
    return indexedAsView;
  }

  /**
   * Set the value of indexedAsView
   *
   * @param indexedAsView new value of indexedAsView
   */
  public void setIndexedAsView(String indexedAsView) {
    this.indexedAsView = indexedAsView;
  }


  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof EventType)) {
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
