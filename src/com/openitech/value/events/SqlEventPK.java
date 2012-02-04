/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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

