/*
 * Equals.java
 *
 * Created on Torek, 2 maj 2006, 14:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.util;

/**
 *
 * @author uros
 */
public class Equals {
  
  /** Creates a new instance of Equals */
  private Equals() {
  }
  
  public static final boolean equals(Object a, Object b) {
    if (a==null && b==null)
      return true;
    else if (a!=null && b!=null)
      return a.equals(b);
    else
      return false;
  }
}
