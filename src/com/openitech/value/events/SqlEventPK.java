/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.value.events;

/**
 *
 * @author domenbasic
 */
public class SqlEventPK extends EventPK {

    protected String primaryKey = null;

    public SqlEventPK(long eventId, int idSifranta, String idSifre, String primaryKey) {
      super(eventId, idSifranta, idSifre);
      this.primaryKey = primaryKey;
    }

    public SqlEventPK(long eventId, int idSifranta, String idSifre, String primaryKey, Integer versionID) {
      super(eventId, idSifranta, idSifre, versionID);
      this.primaryKey = primaryKey;
    }

    /**
     * Get the value of primaryKey
     *
     * @return the value of primaryKey
     */
    @Override
    public String getPrimaryKey() {
      return primaryKey == null ? super.getPrimaryKey() : primaryKey;
    }

    /**
     * Set the value of primaryKey
     *
     * @param primaryKey new value of primaryKey
     */
    public void setPrimaryKey(String primaryKey) {
      this.primaryKey = primaryKey;
    }
  }

