/*
 * JMnemonicButton.java
 *
 * Created on Sobota, 15 julij 2006, 10:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.swing;

/**
 *
 * @author uros
 */
import java.awt.*;
import javax.swing.border.Border;
import javax.swing.JButton;
import javax.swing.Icon;
import java.awt.event.*;
import javax.swing.KeyStroke;

public class JMnemonicButton extends JButton implements ActionListener {
  private int mnemonic = 0;
  private int condition = WHEN_IN_FOCUSED_WINDOW;
  private int mask = java.awt.Event.ALT_MASK;
  private javax.swing.KeyStroke keyStroke = null;
  private boolean hideButtonBorder;
  private final static javax.swing.KeyStroke KS_ENTER = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0, true);

  /**
   * Creates a button with no set text or icon.
   */
  public JMnemonicButton() {
      super(null, null);
      registerKeyboardAction(this, KS_ENTER, WHEN_FOCUSED);
  }

  /**
   * Creates a button with an icon.
   *
   * @param icon  the Icon image to display on the button
   */
  public JMnemonicButton(Icon icon) {
      super(null, icon);
      registerKeyboardAction(this, KS_ENTER, WHEN_FOCUSED);
  }

  /**
   * Creates a button with text.
   *
   * @param text  the text of the button
   */
  public JMnemonicButton(String text) {
      super(text, null);
      registerKeyboardAction(this, KS_ENTER, WHEN_FOCUSED);
  }

  /**
   * Creates a button with initial text and an icon.
   *
   * @param text  the text of the button.
   * @param icon  the Icon image to display on the button
   */
  public JMnemonicButton(String text, Icon icon) {
      super(text, icon);
      registerKeyboardAction(this, KS_ENTER, WHEN_FOCUSED);
  }

  public int getMnemonic() {
    return mnemonic;
  }

  /**
   * Specifies the mnemonic value.
   *
   * @param mnemonic  a char specifying the mnemonic value
   */
  public void setMnemonic(char newMnemonic) {
    setKeyStroke(KeyStroke.getKeyStroke(newMnemonic, mask));
  }

  public void setMnemonic(int newMnemonic) {
    setKeyStroke(KeyStroke.getKeyStroke(newMnemonic, mask));
  }

  public void addMnemonic(char newMnemonic) {
    registerKeyboardAction(this, KeyStroke.getKeyStroke(newMnemonic, mask), condition);
  }

  public void addMnemonic(int newMnemonic) {
    registerKeyboardAction(this, KeyStroke.getKeyStroke(newMnemonic, mask), condition);
  }

  public void actionPerformed(ActionEvent e) {
    this.requestFocus(true);
    doClick(1);
  }

  public void setKeyStroke(javax.swing.KeyStroke newKeyStroke) {
    if (keyStroke != null) {
      unregisterKeyboardAction(keyStroke);
    }
    keyStroke = newKeyStroke;
    if (keyStroke != null) {
      mnemonic = getKeyStroke().getKeyCode();
      registerKeyboardAction(this, keyStroke, condition);
    }
    else
      mnemonic = 0;
  }

  public void setCondition(int condition) {
    this.condition = condition;
    if (keyStroke!=null) {
      setKeyStroke(keyStroke);
    }
  }

  public int getCondition() {
    return condition;
  }

  public void setMask(int mask) {
    this.mask = mask;
    if (keyStroke!=null) {
      setMnemonic(getKeyStroke().getKeyCode());
    }
  }

  public int getMask() {
    return mask;
  }
  
  public javax.swing.KeyStroke getKeyStroke() {
    return keyStroke;
  }
  public boolean isHideButtonBorder() {
    return hideButtonBorder;
  }
  public void setHideButtonBorder(boolean hideButtonBorder) {
    this.hideButtonBorder = hideButtonBorder;
    if (hideButtonBorder)
      setBorder(null);
    else {
      final JButton button = new JButton();
      setBorder(button.getBorder());
      button.setBorder(null);
    }
  }
}

