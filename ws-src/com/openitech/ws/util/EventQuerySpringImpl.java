/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.ws.util;

import com.openitech.db.model.DbDataSource.SqlParameter;
import com.openitech.sql.Field;
import com.openitech.sql.events.EventQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author domenbasic
 */
public class EventQuerySpringImpl implements EventQuery{
    private int sifrant;
    private String sifra;
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
    public String getSifra() {
        return this.sifra;
    }

    public void setNamedParameters(Map<Field, SqlParameter<Object>> namedParameters) {
        this.namedParameters = namedParameters;
    }

    public void setSifra(String sifra) {
        this.sifra = sifra;
    }

    public void setSifrant(int sifrant) {
        this.sifrant = sifrant;
    }



}