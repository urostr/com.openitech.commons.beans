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

import java.io.Serializable;

/**
 *
 * @author uros
 */
public class ActivityEventPolja implements Serializable {

    private long activityId;
    private int activityIdSifranta;
    private String activityIdSifre;
    private int idSifranta;
    private String idSifre;
    private int idPolja;
    private int fieldValueIndex;

    public ActivityEventPolja() {
    }

    public ActivityEventPolja(long activityId, int activityIdSifranta, String activityIdSifre, int idSifranta, String idSifre, int idPolja, int fieldValueIndex) {
        this.activityId = activityId;
        this.activityIdSifranta = activityIdSifranta;
        this.activityIdSifre = activityIdSifre;
        this.idSifranta = idSifranta;
        this.idSifre = idSifre;
        this.idPolja = idPolja;
        this.fieldValueIndex = fieldValueIndex;
    }

    public String getActivityIdSifre() {
        return activityIdSifre;
    }

    public void setActivityIdSifre(String activityIdSifre) {
        this.activityIdSifre = activityIdSifre;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public int getActivityIdSifranta() {
        return activityIdSifranta;
    }

    public void setActivityIdSifranta(int activityIdSifranta) {
        this.activityIdSifranta = activityIdSifranta;
    }

    public int getFieldValueIndex() {
        return fieldValueIndex;
    }

    public void setFieldValueIndex(int fieldValueIndex) {
        this.fieldValueIndex = fieldValueIndex;
    }

    public int getIdPolja() {
        return idPolja;
    }

    public void setIdPolja(int idPolja) {
        this.idPolja = idPolja;
    }

    public int getIdSifranta() {
        return idSifranta;
    }

    public void setIdSifranta(int idSifranta) {
        this.idSifranta = idSifranta;
    }

    public String getIdSifre() {
        return idSifre;
    }

    public void setIdSifre(String idSifre) {
        this.idSifre = idSifre;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) activityId;
        hash += (int) activityIdSifranta;
        hash += (activityIdSifre != null ? activityIdSifre.hashCode() : 0);
        hash += (idSifre != null ? idSifre.hashCode() : 0);
        hash += (int) idSifranta;
        hash += (int) idPolja;
        hash += (int) fieldValueIndex;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ActivityEventPolja)) {
            return false;
        }
        ActivityEventPolja other = (ActivityEventPolja) object;
        if (this.activityId != other.activityId) {
            return false;
        }
        if (this.activityIdSifranta != other.activityIdSifranta) {
            return false;
        }
        if ((this.activityIdSifre == null && other.activityIdSifre != null) || (this.activityIdSifre != null && !this.activityIdSifre.equals(other.activityIdSifre))) {
            return false;
        }
        if (this.idSifranta != other.idSifranta) {
            return false;
        }
        if ((this.idSifre == null && other.idSifre != null) || (this.idSifre != null && !this.idSifre.equals(other.idSifre))) {
            return false;
        }
        if (this.idPolja != other.idPolja) {
            return false;
        }
        if (this.fieldValueIndex != other.fieldValueIndex) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ActivityEventsPolja[activityId=" + activityId + ", activityIdSifranta=" + activityIdSifranta + ", activityIdSifre=" + activityIdSifre + "IdSifranta=" + idSifranta + ", idSifre=" + idSifre + ", IdPolja=" + idPolja + ", fieldValueIndex=" + fieldValueIndex + "]";
    }
}
