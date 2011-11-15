/*
 * AssociatedFilter.java
 *
 * Created on Ponedeljek, 4 februar 2008, 10:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.swing.framework.context;

import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author uros
 */
public interface AssociatedInformationPanel {
  /**
   * Getter for property filter.
   * @return Value of property filter.
   */
  public List<JPanel> getInformationPanels();

  /**
   * Setter for property filter.
   * @param filter New value of property filter.
   */
  public void setInformationPanels(List<JPanel> informationPanels);
  
}
