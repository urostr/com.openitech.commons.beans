/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author domenbasic
 */
public class AutoInsertValue implements ActionListener {

  private final Integer workAreaId;
  private DbDataSource dsWorkAreaFrom;
  private final DbDataSource dsWorkAreaTo;
  private final String columName;
  private final String otherColumName;
  private final Integer[] workSpaceId;

  public AutoInsertValue(Integer workAreaId, Integer[] workSpaceId, DbDataSource dataSource, String columName, String otherColumName) {
    this.workAreaId = workAreaId;
    this.workSpaceId = workSpaceId;
    this.dsWorkAreaTo = dataSource;
    this.columName = columName;
    this.otherColumName = otherColumName == null ? columName : otherColumName;
    
    this.dsWorkAreaTo.addActionListener(this);
  }

  public Integer getWorkAreaId() {
    return workAreaId;
  }

  public Integer[] getWorkSpaceId() {
    return workSpaceId;
  }


  public void setDataSourceFrom(DbDataSource dataSource) {
    this.dsWorkAreaFrom = dataSource;

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();
    if (actionCommand.equals(DbDataSource.ROW_INSERTED)) {
      try {
        if (dsWorkAreaTo != null && dsWorkAreaFrom != null) {
          dsWorkAreaTo.updateObject(columName, dsWorkAreaFrom.getObject(otherColumName));
        }
      } catch (SQLException ex) {
        Logger.getLogger(AutoInsertValue.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

  }
}
