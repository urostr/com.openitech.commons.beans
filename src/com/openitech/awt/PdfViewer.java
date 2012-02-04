/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.openitech.awt;

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
 * @version $Revision: 1.5 $
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
