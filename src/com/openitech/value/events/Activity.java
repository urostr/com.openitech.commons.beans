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
