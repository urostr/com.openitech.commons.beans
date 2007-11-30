package com.openitech.icons;

import java.awt.Image;
import javax.swing.ImageIcon;


/**
 * $Revision: 1.1.1.1 $
 */

public class LoadImage {
  public static ImageIcon getImage(String filename) {
    try {
      return new ImageIcon(com.openitech.icons.LoadImage.class.getResource(filename));
    }
    catch (Exception ex) {
      return new ImageIcon();
    }
  }
}