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
import java.util.Iterator;

/**
 *
 * @author uros
 */
public class Equals {
  
  /** Creates a new instance of Equals */
  private Equals() {
  }
  
  public static boolean equals(Object a, Object b) {
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
      else if (a instanceof Number && b instanceof Boolean) {
        return ((Comparable) a).compareTo(((Boolean) b).booleanValue()?1:0)==0;
      }
      else if (a instanceof java.util.Map && b instanceof java.util.Map) {
        return compareMap((java.util.Map) a,(java.util.Map) b);
      }      else if (a instanceof java.util.List && b instanceof java.util.List) {
        return compareList((java.util.List) a,(java.util.List) b);
      }
      else if (a instanceof java.util.Collection && b instanceof java.util.Collection) {
        return compareCollection((java.util.Collection) a,(java.util.Collection) b);
      }
      else if (a instanceof Comparable && b instanceof Comparable)
        return ((Comparable) a).compareTo(b)==0;
      else 
        return a.equals(b);
    } else
      return false;
  }

  private static boolean compareMap(java.util.Map a, java.util.Map b) {
    if (a==null && b==null) {
      return true;
    } else if (a!=null && b!=null) {
      if (compareCollection(a.keySet(), b.keySet())) {
        boolean result = true;
        Iterator<java.util.Map.Entry> aiterator = a.entrySet().iterator();
        Iterator<java.util.Map.Entry> biterator = b.entrySet().iterator();

        for (int i=0; i<a.size() && result; i++) {
          result = equals(aiterator.next().getValue(), biterator.next().getValue());
        }

        return result;
      } else
        return false;
    } else
      return false;
  }
  
  private static boolean compareCollection(java.util.Collection a, java.util.Collection b) {
    if (a==null && b==null) {
      return true;
    } else if (a!=null && b!=null) {
      if (a.size()==b.size()) {
        boolean result = true;
        Iterator aiterator = a.iterator();
        Iterator biterator = b.iterator();

        for (int i=0; i<a.size() && result; i++) {
          result = equals(aiterator.next(), biterator.next());
        }

        return result;
      } else
        return false;
    } else
      return false;
  }

  private static boolean compareList(java.util.List a, java.util.List b) {
    if (a==null && b==null) {
      return true;
    } else if (a!=null && b!=null) {
      if (a.size()==b.size()) {
        boolean result = true;
        for (int i=0; i<a.size() && result; i++) {
          result = equals(a.get(i), b.get(i));
        }

        return result;
      } else
        return false;
    } else
      return false;
  }
}
