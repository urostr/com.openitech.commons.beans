/*
 * OwnerFrame.java
 *
 * Created on Èetrtek, 26 oktober 2006, 19:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.util;

import java.awt.Frame;

/**
 *
 * @author uros
 */
public class OwnerFrame {
  private static OwnerFrame instance;
  
  private Frame owner = null;
  
  /** Creates a new instance of OwnerFrame */
  private OwnerFrame() {
  }
  
  public static OwnerFrame getInstance() {
    if (instance==null)
      instance = new OwnerFrame();
    return instance;
  }

  public Frame getOwner() {
    return owner;
  }

  public void setOwner(Frame owner) {
    this.owner = owner;
  }
}
