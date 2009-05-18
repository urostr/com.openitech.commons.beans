package com.openitech.spring.beans.factory.config;

public enum PropertyType {

  SQL {
    @Override
    public int getContentType() {
      return 1;
    }

    @Override
    public Class getTypeClass() {
      return String.class;
    }
  },
  JRuby {
    @Override
    public int getContentType() {
      return 2;
    }

    @Override
    public Class getTypeClass() {
      return String.class;
    }
  },
  Groovy {
    @Override
    public int getContentType() {
      return 3;
    }

    @Override
    public Class getTypeClass() {
      return String.class;
    }
  },
  BeanShell {
    @Override
    public int getContentType() {
      return 4;
    }

    @Override
    public Class getTypeClass() {
      return String.class;
    }
  };

  public abstract int getContentType();

  public abstract Class getTypeClass();
}
