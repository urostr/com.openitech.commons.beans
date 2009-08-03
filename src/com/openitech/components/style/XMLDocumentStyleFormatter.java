/*
 * To change this template, choose Tools | Templates
 * and read the template in the editor.
 */
package com.openitech.components.style;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

/**
 *
 * @author domenbasic
 */
public class XMLDocumentStyleFormatter implements JTextPaneStyleFormatter {

  private JTextPane tpXML;
  private StyledDocument srcdoc;
  private Style styleElement;
  private Style styleNomAttribut;
  private Style styleValeurAttribut;
  private Style styleTexte;
  private Style styleEntite;
  private Style styleCommentaire;

  /**
   * Buffer de caractères du document
   */
  class Buffer {

    int maxSize = 200;
    String StringBuilder;
    int goal, end; // does not end the last character of the buffer

    public Buffer() {
      read(0);
    }

    public void read(final int ind) {
      int lg = maxSize;
      if (ind + lg > srcdoc.getLength()) {
        lg = srcdoc.getLength() - ind;
      }
      try {
        StringBuilder = srcdoc.getText(ind, lg);
      } catch (final BadLocationException ex) {
        ex.printStackTrace();
      }
      if (StringBuilder.length() != lg) {
        System.out.println("Buffer.read: error: " + StringBuilder.length() + " != " + lg);
      }
      goal = ind;
      end = ind + lg;
    }

    public char getChar(final int p) {
      if (p >= srcdoc.getLength()) {
        return (' ');
      }
      if (p >= end) {
        read(p);
      } else if (p < goal) {
        int p2 = p - maxSize + 1;
        if (p2 < 0) {
          p2 = 0;
        }
        read(p2);
      }
      return (StringBuilder.charAt(p - goal));
    }

    public boolean subEquals(final String s, final int ind) {
      if (ind >= srcdoc.getLength()) {
        System.out.println("Error in Buffer.subEquals: ind >= srcdoc.getLength() : " + ind + " >= " + srcdoc.getLength());
      }
      final int lg = s.length();
      if (ind + lg >= srcdoc.getLength()) {
        return (false);
      }
      if (lg > maxSize) {
        System.out.println("Error in Buffer.subEquals: " + lg + " >  max size(" + maxSize + ")");
      }
      if (ind < goal || ind + lg > end) {
        read(ind);
      }
      for (int i = 0, j = ind - goal; i < lg; i++, j++) {
        if (s.charAt(i) != StringBuilder.charAt(j)) {
          return (false);
        }
      }
      return (true);
    }
  }

  /**
   * Specifies the size of tabs, equivalent characters (using the size of the 'w' as a reference)
   */
  public void setTabs(final int charactersPerTab) {
    final FontMetrics fm = tpXML.getFontMetrics(tpXML.getFont());
    final int charWidth = fm.charWidth('w');
    final int tabWidth = charWidth * charactersPerTab;

    final TabStop[] tabs = new TabStop[10];

    for (int j = 0; j < tabs.length; j++) {
      final int tab = j + 1;
      tabs[j] = new TabStop(tab * tabWidth);
    }

    final TabSet tabSet = new TabSet(tabs);
    final SimpleAttributeSet attributes = new SimpleAttributeSet();
    StyleConstants.setTabSet(attributes, tabSet);
    final int length = srcdoc.getLength();
    srcdoc.setParagraphAttributes(0, length, attributes, false);
  }

  public void colorAll() {
    coloring(srcdoc, 0, srcdoc.getLength());
  }

  public void coloring(StyledDocument srcdoc, final int goal, final int end) {
    final Buffer buff = new Buffer();
    if (buff.subEquals("\n", goal)) {
      if (end - goal > 1) {
        srcdoc.setCharacterAttributes(goal + 1, end - goal - 1, styleTexte, false);
      }
    } else {
      srcdoc.setCharacterAttributes(goal, end - goal, styleTexte, false);
    }

    boolean inElementName = false;
    boolean inAttributeName = false;
    boolean attributeValueBefore = false;
    char valueForField = '"';
    boolean valueInField = false;
    boolean inProject = false;
    boolean inComentary = false;
    int goalArea = goal;
    for (int ic = goal; ic < end; ic++) {
      if (inComentary) {
        if (buff.subEquals("-->", ic)) {
          inComentary = false;
          ic += 2;
          srcdoc.setCharacterAttributes(goalArea, ic - goalArea + 1, styleCommentaire, false);
        }
      } else if (inElementName) {
        final char c = buff.getChar(ic);
        if (c == ' ' || c == '\n') {
          inElementName = false;
          srcdoc.setCharacterAttributes(goalArea, ic - goalArea, styleElement, false);
          inAttributeName = true;
          goalArea = ic + 1;
        } else if (c == '>' || ic == end - 1) {
          inElementName = false;
          srcdoc.setCharacterAttributes(goalArea, ic - goalArea + 1, styleElement, false);
        }
      } else if (inAttributeName) {
        final char c = buff.getChar(ic);
        if (c == '>' || ic == end - 1) {
          inAttributeName = false;
          srcdoc.setCharacterAttributes(goalArea, ic - goalArea + 1, styleElement, false);
        } else if (c == '=') {
          inAttributeName = false;
          srcdoc.setCharacterAttributes(goalArea, ic - goalArea, styleNomAttribut, false);
          attributeValueBefore = true;
        }
      } else if (attributeValueBefore) {
        final char c = buff.getChar(ic);
        if (c == '"' || c == '\'') {
          attributeValueBefore = false;
          valueInField = true;
          valueForField = c;
          goalArea = ic;
        }
      } else if (valueInField) {
        final char c = buff.getChar(ic);
        if (c == valueForField || ic == end - 1) {
          valueInField = false;
          srcdoc.setCharacterAttributes(goalArea, ic - goalArea + 1, styleValeurAttribut, false);
          inAttributeName = true;
          goalArea = ic + 1;
        }
      } else if (inProject) {
        final char c = buff.getChar(ic);
        if (c == ';' || c == ' ' || c == '\n' || ic == end - 1) {
          inProject = false;
          srcdoc.setCharacterAttributes(goalArea, ic - goalArea + 1, styleEntite, false);
        }
      } else {
        final char c = buff.getChar(ic);
        if (c == '<') {
          if (buff.subEquals("<!--", ic)) {
            inComentary = true;
          } else {
            inElementName = true;
          }
          goalArea = ic;
        } else if (c == '>') {
          srcdoc.setCharacterAttributes(ic, 1, styleElement, false);
        } else if (c == '&' || c == '%') {
          inProject = true;
          goalArea = ic;
        }

      }
    }

    // if one is always in a comment at the end of the zone, continues to coloring in Delrez
    if (inComentary) {
      for (int ic = end; ic < srcdoc.getLength() && inComentary; ic++) {
        if (buff.subEquals("-->", ic)) {
          inComentary = false;
          ic += 2;
          srcdoc.setCharacterAttributes(goalArea, ic - goalArea + 1, styleCommentaire, false);
        }
      }
      if (inComentary) {
        srcdoc.setCharacterAttributes(goalArea, srcdoc.getLength() - goalArea, styleCommentaire, false);
      }
    }
  }

  @Override
  public void applyStyle(JTextPane component) {
    this.tpXML = component;
    this.srcdoc = (StyledDocument) component.getDocument();

    setTabs(4);
    // Monaco font looks much better than the default Courier on MacOS X with Java 1.4.1
    final String[] fontnames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    boolean found = false;
    for (final String element : fontnames) {
      if ("Monaco".equals(element)) {
        found = true;
        break;
      }
    }
    if (found) {
      final Style defaultStyle = tpXML.getStyle(StyleContext.DEFAULT_STYLE);
      StyleConstants.setFontFamily(defaultStyle, "Monaco");
      StyleConstants.setFontSize(defaultStyle, 12);
    }

    final Style defaultStyle = tpXML.getStyle(StyleContext.DEFAULT_STYLE);
    styleElement = tpXML.addStyle(null, defaultStyle);
    StyleConstants.setForeground(styleElement, new Color(150, 0, 0)); // dark red
    styleNomAttribut = tpXML.addStyle(null, defaultStyle);
    StyleConstants.setForeground(styleNomAttribut, new Color(0, 0, 150)); // dark blue
    styleValeurAttribut = tpXML.addStyle(null, defaultStyle);
    StyleConstants.setForeground(styleValeurAttribut, new Color(0, 100, 0)); // dark green
    styleEntite = tpXML.addStyle(null, defaultStyle);
    StyleConstants.setForeground(styleEntite, new Color(0, 100, 100)); // dark cyan
    styleCommentaire = tpXML.addStyle(null, defaultStyle);
    StyleConstants.setForeground(styleCommentaire, Color.gray); // gray
    styleTexte = tpXML.addStyle(null, defaultStyle);
    StyleConstants.setForeground(styleTexte, Color.black); // black

    colorAll();

  }
}
