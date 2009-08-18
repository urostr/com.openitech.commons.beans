/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.filters;

import com.openitech.db.model.DbDataSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;

/**
 *
 * @author uros
 */
public class DataSourceLimit extends DbDataSource.SubstSqlParameter implements ActionListener {

  @Override
  public void actionPerformed(ActionEvent e) {
    String result = null;

    for (int i = 0; (i < Limit.values().length) && result == null; i++) {
      result = Limit.values()[i].getValue();
    }

    super.setValue(result == null ? "" : result);
    super.reloadDataSources();
  }

  public enum Limit {

    L10 {

      @Override
      public String getValue() {
        return model.isSelected() ? "TOP 10" : null;
      }
    },
    L50 {

      @Override
      public String getValue() {
        return model.isSelected() ? "TOP 50" : null;
      }
    },
    L100 {

      @Override
      public String getValue() {
        return model.isSelected() ? "TOP 100" : null;
      }
    },
    LALL {

      @Override
      public String getValue() {
        return model.isSelected() ? "" : null;
      }
    },
    L10P {

      @Override
      public String getValue() {
        return model.isSelected() ? "TOP 10 PERCENT" : null;
      }
    },
    L25P {

      @Override
      public String getValue() {
        return model.isSelected() ? "TOP 25 PERCENT" : null;
      }
    },
    L50P {

      @Override
      public String getValue() {
        return model.isSelected() ? "TOP 50 PERCENT" : null;
      }
    },
    L100P {

      @Override
      public String getValue() {
        return model.isSelected() ? "TOP 100 PERCENT" : null;
      }
    };

    public abstract String getValue();
    javax.swing.JToggleButton.ToggleButtonModel model = new javax.swing.JToggleButton.ToggleButtonModel();

    public javax.swing.JToggleButton.ToggleButtonModel getModel() {
      return model;
    }
    
    private final static ButtonGroup bg = new ButtonGroup();

    static {
      for (Limit l : Limit.values()) {
        l.getModel().setGroup(bg);
      }

    }
  }

  public DataSourceLimit(String replace) {
    super(replace);

    for (Limit l : Limit.values()) {
      l.getModel().addActionListener(this);
    }
  }
}
