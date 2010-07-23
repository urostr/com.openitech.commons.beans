/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.swing;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

/**
 *
 * @author uros
 */
public class WindowsActions {

  private WindowsActions() {
  }

  public static void addActions(final JTextComponent component) {
    Action copyAction = new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        component.copy();
        System.out.println("Copy Pressed");
      }
    };
    Action pasteAction = new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        component.paste();
        System.out.println("Paste Pressed");
      }
    };
    Action cutAction = new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        component.cut();
        System.out.println("Cut Pressed");
      }
    };
    KeyStroke ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK);
    KeyStroke ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK);
    KeyStroke ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK);
    component.getInputMap().put(ctrlC, copyAction);
    component.getInputMap().put(ctrlV, pasteAction);
    component.getInputMap().put(ctrlX, cutAction);
  }
}
