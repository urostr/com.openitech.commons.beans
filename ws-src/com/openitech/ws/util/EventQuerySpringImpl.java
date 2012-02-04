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

package com.openitech.ws.util;

import com.openitech.db.model.DbDataSource.SqlParameter;
import com.openitech.value.fields.Field;
import com.openitech.value.events.EventQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author domenbasic
 */
public class EventQuerySpringImpl implements EventQuery{
    private int sifrant;
    private String[] sifra;
    private Map<Field, SqlParameter<Object>> namedParameters;

    @Override
    public List<Object> getParameters() {
        return new ArrayList<Object>();
    }

    @Override
    public String getQuery() {
        return "";
    }

    @Override
    public Map<Field, SqlParameter<Object>> getNamedParameters() {
        return this.namedParameters;
    }

    @Override
    public int getSifrant() {
        return this.sifrant;
    }

    @Override
    public String[] getSifra() {
        return this.sifra;
    }

    public void setNamedParameters(Map<Field, SqlParameter<Object>> namedParameters) {
        this.namedParameters = namedParameters;
    }

    public void setSifra(String[] sifra) {
        this.sifra = sifra;
    }

    public void setSifrant(int sifrant) {
        this.sifrant = sifrant;
    }

  @Override
  public int getValuesSet() {
    return 0;
  }

  @Override
  public Set<Field> getResultFields() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Set<Field> getSearchFields() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isSearchByEventPK() {
    throw new UnsupportedOperationException("Not supported yet.");
  }



}
