/*
 * Equals.java
 *
 * Created on Torek, 2 maj 2006, 14:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.util;

import java.math.BigDecimal;

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
    else if (a!=null && b!=null) {
      if (a instanceof java.util.Date && b instanceof java.util.Date)
        return ((java.util.Date)a).getTime()==((java.util.Date)b).getTime();
      else if (a instanceof Number && b instanceof Number)
        return BigDecimal.valueOf(((Number)a).doubleValue()).equals(BigDecimal.valueOf(((Number)b).doubleValue()));
      else if (a instanceof String || b instanceof String) {
        return (a.toString()).equals(b.toString());
      }
      else if (a instanceof Comparable && b instanceof Comparable)
        return ((Comparable) a).compareTo(b)==0;
      else 
        return a.equals(b);
    } else
      return false;
  }
}
