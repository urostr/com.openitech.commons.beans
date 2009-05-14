/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    int tailleMax = 200;
    String sbuff;
    int debut, fin; // fin n'inclut pas le dernier caractère du buffer

    public Buffer() {
      lire(0);
    }

    public void lire(final int ind) {
      int lg = tailleMax;
      if (ind + lg > srcdoc.getLength()) {
        lg = srcdoc.getLength() - ind;
      }
      try {
        sbuff = srcdoc.getText(ind, lg);
      } catch (final BadLocationException ex) {
        ex.printStackTrace();
      }
      if (sbuff.length() != lg) {
        System.out.println("Buffer.lire: erreur: " + sbuff.length() + " != " + lg);
      }
      debut = ind;
      fin = ind + lg;
    }

    public char getChar(final int p) {
      if (p >= srcdoc.getLength()) {
        return (' ');
      }
      if (p >= fin) {
        lire(p);
      } else if (p < debut) {
        int p2 = p - tailleMax + 1;
        if (p2 < 0) {
          p2 = 0;
        }
        lire(p2);
      }
      return (sbuff.charAt(p - debut));
    }

    public boolean subEquals(final String s, final int ind) {
      if (ind >= srcdoc.getLength()) {
        System.out.println("erreur dans Buffer.subEquals: ind >= srcdoc.getLength() : " + ind + " >= " + srcdoc.getLength());
      }
      final int lg = s.length();
      if (ind + lg >= srcdoc.getLength()) {
        return (false);
      }
      if (lg > tailleMax) {
        System.out.println("erreur dans Buffer.subEquals: " + lg + " > taille maxi (" + tailleMax + ")");
      }
      if (ind < debut || ind + lg > fin) {
        lire(ind);
      }
      for (int i = 0, j = ind - debut; i < lg; i++, j++) {
        if (s.charAt(i) != sbuff.charAt(j)) {
          return (false);
        }
      }
      return (true);
    }
  }

  /**
   * Spécifie la taille des tabulations, en équivalent-caractères (on utilise la taille du 'w' comme référence)
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

  public void toutColorier() {
    colorier(srcdoc, 0, srcdoc.getLength());
  }

  public void colorier(StyledDocument srcdoc, final int debut, final int fin) {
    final Buffer buff = new Buffer();
    if (buff.subEquals("\n", debut)) {
      if (fin - debut > 1) {
        srcdoc.setCharacterAttributes(debut + 1, fin - debut - 1, styleTexte, false);
      }
    } else {
      srcdoc.setCharacterAttributes(debut, fin - debut, styleTexte, false);
    }

    boolean dansNomElement = false;
    boolean dansNomAttribut = false;
    boolean avantValeurAttribut = false;
    char carValeurAttribut = '"';
    boolean dansValeurAttribut = false;
    boolean dansEntite = false;
    boolean dansCommentaire = false;
    int debutzone = debut;
    for (int ic = debut; ic < fin; ic++) {
      if (dansCommentaire) {
        if (buff.subEquals("-->", ic)) {
          dansCommentaire = false;
          ic += 2;
          srcdoc.setCharacterAttributes(debutzone, ic - debutzone + 1, styleCommentaire, false);
        }
      } else if (dansNomElement) {
        final char c = buff.getChar(ic);
        if (c == ' ' || c == '\n') {
          dansNomElement = false;
          srcdoc.setCharacterAttributes(debutzone, ic - debutzone, styleElement, false);
          dansNomAttribut = true;
          debutzone = ic + 1;
        } else if (c == '>' || ic == fin - 1) {
          dansNomElement = false;
          srcdoc.setCharacterAttributes(debutzone, ic - debutzone + 1, styleElement, false);
        }
      } else if (dansNomAttribut) {
        final char c = buff.getChar(ic);
        if (c == '>' || ic == fin - 1) {
          dansNomAttribut = false;
          srcdoc.setCharacterAttributes(debutzone, ic - debutzone + 1, styleElement, false);
        } else if (c == '=') {
          dansNomAttribut = false;
          srcdoc.setCharacterAttributes(debutzone, ic - debutzone, styleNomAttribut, false);
          avantValeurAttribut = true;
        }
      } else if (avantValeurAttribut) {
        final char c = buff.getChar(ic);
        if (c == '"' || c == '\'') {
          avantValeurAttribut = false;
          dansValeurAttribut = true;
          carValeurAttribut = c;
          debutzone = ic;
        }
      } else if (dansValeurAttribut) {
        final char c = buff.getChar(ic);
        if (c == carValeurAttribut || ic == fin - 1) {
          dansValeurAttribut = false;
          srcdoc.setCharacterAttributes(debutzone, ic - debutzone + 1, styleValeurAttribut, false);
          dansNomAttribut = true;
          debutzone = ic + 1;
        }
      } else if (dansEntite) {
        final char c = buff.getChar(ic);
        if (c == ';' || c == ' ' || c == '\n' || ic == fin - 1) {
          dansEntite = false;
          srcdoc.setCharacterAttributes(debutzone, ic - debutzone + 1, styleEntite, false);
        }
      } else {
        final char c = buff.getChar(ic);
        if (c == '<') {
          if (buff.subEquals("<!--", ic)) {
            dansCommentaire = true;
          } else {
            dansNomElement = true;
          }
          debutzone = ic;
        } else if (c == '>') {
          srcdoc.setCharacterAttributes(ic, 1, styleElement, false);
        } else if (c == '&' || c == '%') {
          dansEntite = true;
          debutzone = ic;
        }

      }
    }

    // si on est toujours dans un commentaire à la fin de la zone, on continue à colorier au-delà
    if (dansCommentaire) {
      for (int ic = fin; ic < srcdoc.getLength() && dansCommentaire; ic++) {
        if (buff.subEquals("-->", ic)) {
          dansCommentaire = false;
          ic += 2;
          srcdoc.setCharacterAttributes(debutzone, ic - debutzone + 1, styleCommentaire, false);
        }
      }
      if (dansCommentaire) {
        srcdoc.setCharacterAttributes(debutzone, srcdoc.getLength() - debutzone, styleCommentaire, false);
      }
    }
  }

  public void applyStyle(JTextPane component) {
    this.tpXML = component;
    this.srcdoc = (StyledDocument) component.getDocument();
    // dsWorkSpace.updateString("Config", cl.getSubString(1L, (int) cl.length()));
    setTabs(4);
    // Monaco font looks much better than the default Courier on MacOS X with Java 1.4.1
    final String[] fontnames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    boolean trouv = false;
    for (final String element : fontnames) {
      if ("Monaco".equals(element)) {
        trouv = true;
        break;
      }
    }
    if (trouv) {
      final Style defaultStyle = tpXML.getStyle(StyleContext.DEFAULT_STYLE);
      StyleConstants.setFontFamily(defaultStyle, "Monaco");
      StyleConstants.setFontSize(defaultStyle, 12);
    }

    final Style defaultStyle = tpXML.getStyle(StyleContext.DEFAULT_STYLE);
    styleElement = tpXML.addStyle(null, defaultStyle);
    StyleConstants.setForeground(styleElement, new Color(150, 0, 0)); // rouge foncé
    styleNomAttribut = tpXML.addStyle(null, defaultStyle);
    StyleConstants.setForeground(styleNomAttribut, new Color(0, 0, 150)); // bleu foncé
    styleValeurAttribut = tpXML.addStyle(null, defaultStyle);
    StyleConstants.setForeground(styleValeurAttribut, new Color(0, 100, 0)); // vert foncé
    styleEntite = tpXML.addStyle(null, defaultStyle);
    StyleConstants.setForeground(styleEntite, new Color(0, 100, 100)); // cyan foncé
    styleCommentaire = tpXML.addStyle(null, defaultStyle);
    StyleConstants.setForeground(styleCommentaire, Color.gray); // gris
    styleTexte = tpXML.addStyle(null, defaultStyle);
    StyleConstants.setForeground(styleTexte, Color.black); // noir

    toutColorier();

  }
}
