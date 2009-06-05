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
 * @version $Revision: 1.4 $
 */
public class PdfViewer {

  private static PdfViewer instance = null;
  private boolean canViewPDFs;
  private boolean jdic;
  private String acroread = System.getProperty("acroread.bin", "");

  private PdfViewer() {
    jdic = Desktop.isFileOpenSupported();
    canViewPDFs = (acroread.length() > 0) ||
            ((System.getProperty("view.pdfs", "false").equals("true")) && jdic);
  }

  public static void clear() {
    instance = null;
  }

  public static PdfViewer getInstance() {
    if (instance == null) {
      instance = new PdfViewer();
    }
    return instance;
  }

  public boolean canViewPDFs() {
    return canViewPDFs;
  }

  public void openPDF(File fPDF) throws InvocationTargetException,
          IllegalArgumentException, IllegalAccessException, IOException {
    if (jdic && (acroread.length() == 0)) {
      Desktop.open(fPDF);
    } else if (fPDF.exists() && canViewPDFs) {
      Runtime.getRuntime().exec(new String[]{acroread, fPDF.getAbsolutePath()});
    }
  }
}
