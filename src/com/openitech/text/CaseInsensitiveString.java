/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.text;

/**
 *
 * @author uros
 */
public class CaseInsensitiveString implements CharSequence, Comparable<CharSequence> {

  final String value;

  public CaseInsensitiveString(CharSequence value) {
    this(value == null ? "" : value.toString());
  }

  public CaseInsensitiveString(String value) {
    this.value = value == null ? "" : value;
  }

  @Override
  public int length() {
    return value.length();
  }

  @Override
  public char charAt(int index) {
    return value.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return value.subSequence(start, end);
  }

  @Override
  public int compareTo(CharSequence o) {
    return value.compareToIgnoreCase(o == null ? "" : o.toString());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CaseInsensitiveString other = (CaseInsensitiveString) obj;
    if ((this.value == null) ? (other.value != null) : !this.value.equalsIgnoreCase(other.value)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 61 * hash + (this.value != null ? this.value.toUpperCase().hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  public static CaseInsensitiveString valueOf(Object value) {
    return new CaseInsensitiveString(value==null?"":value.toString());
  }
}
