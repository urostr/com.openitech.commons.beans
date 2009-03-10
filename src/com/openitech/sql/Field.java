package com.openitech.sql;

public class Field {

  String name;
  int type;

  public Field(String name, int type) {
    super();
    this.name = name;
    this.type = type;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!obj.getClass().isInstance(this)) {
      return false;
    }
    final Field other = (Field) obj;
    if ((this.name == null) ? (other.name != null) : !this.name.equalsIgnoreCase(other.name)) {
      return false;
    }
    return true;
  }

  public String getName() {
    return name;
  }

  public int getType() {
    return type;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 47 * hash + (this.name != null ? this.name.toUpperCase().hashCode() : 0);
    return hash;
  }
}
