package com.openitech.db.components;

import javax.swing.text.JTextComponent;


class Selector implements Runnable  {
  private final JTextComponent owner;
  
  public Selector(JTextComponent owner) {
    this.owner = owner;
  }
  public void run() {
    owner.selectAll();
  }
}