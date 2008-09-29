package com.openitech.util;

import java.lang.reflect.Method;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Odprte Informacijske Tehnologije Uroš Trojar s.p.</p>
 *
 * @author Uroš Trojar
 * @version $Revision: 1.2 $
 */
public class PdfViewer {
  private static PdfViewer instance = null;

  private boolean canViewPDFs;
  private boolean jdic;
  private Object manager;
  private Method open;
  private String acroread = System.getProperty("acroread.bin","");

  private PdfViewer() {
    try {
      final Class[] parameters = new Class[] {
          File.class
      };

      Class desktop;
      
      try {
        desktop = Class.forName("org.jdesktop.jdic.desktop.Desktop");
      } catch (java.lang.ClassNotFoundException ex) {
        desktop = Class.forName("java.awt.Desktop");
        if ((Boolean) (desktop.getMethod("isDesktopSupported", new Class[] {}).invoke(null, new Object[] {}))) {
          manager = desktop.getMethod("getDesktop", new Class[] {}).invoke(null, new Object[] {});
          desktop = null;
        }
      }
      
      if (desktop!=null) {
        open = desktop.getMethod("open", parameters);
      }
      jdic = (open!=null);
    }
    catch (Exception ex) {
      System.out.println("org.jdesktop.jdic.desktop.Desktop : "+ex.getMessage());
      open = null;
      jdic = false;
    }
    canViewPDFs = (acroread.length()>0) ||
                  ((System.getProperty("view.pdfs","false").equals("true")) && jdic);
  }

  public static void clear() {
    instance = null;
  }

  public static PdfViewer getInstance() {
    if (instance==null)
      instance = new PdfViewer();
    return instance;
  }

  public boolean canViewPDFs() {
    return canViewPDFs;
  }

  public void openPDF(File fPDF) throws InvocationTargetException,
      IllegalArgumentException, IllegalAccessException, IOException {
    if (fPDF.exists() && canViewPDFs) {
        if (jdic&&(acroread.length()==0)) {
          open.invoke(manager, new Object[] { fPDF });
        } else {
          Runtime.getRuntime().exec(new String[] { acroread, fPDF.getAbsolutePath() });
        }
    }
  }


}
