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
public class Activity implements Serializable {

  private long activityId;
  private String opis;

  public Activity() {
  }

  public Activity(long activityId, String opis) {
    this.activityId = activityId;
    this.opis = opis;
  }

  public long getActivityId() {
    return activityId;
  }

  public void setActivityId(long activityId) {
    this.activityId = activityId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (int) activityId;
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Activity)) {
      return false;
    }
    Activity other = (Activity) object;
    if (this.activityId != other.activityId) {
      return false;
    }

    return true;
  }

  @Override
  public String toString() {
    return "ActivityEvents[activityId=" + activityId + "]";
  }
}
