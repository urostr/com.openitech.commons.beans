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


/*
 * Document.java
 *
 * Created on Sreda, 27 februar 2008, 20:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.text;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

/**
 *
 * @author uros
 */
public class Document {

  /** Creates a new instance of Document */
  private Document() {
  }

  public static void setText(javax.swing.text.Document doc, String t) throws BadLocationException {
    if (doc instanceof AbstractDocument) {
      ((AbstractDocument) doc).replace(0, doc.getLength(), t, null);
    } else {
      doc.remove(0, doc.getLength());
      doc.insertString(0, t, null);
    }
  }

  public static String getText(javax.swing.text.Document doc) throws BadLocationException {
    String result;
    try {
      result = doc.getText(0, doc.getLength());
    } catch (BadLocationException err) {
      result = "";
    }

    return result;
  }

  public static String identText(CharSequence text, int ident) {
    try {
      LineNumberReader sr = new LineNumberReader(new StringReader(text.toString()));
      StringBuilder sb = new StringBuilder(text.length());
      for (int i = 0; i < ident; i++) {
        sb.append(" ");
      }
      String ids = sb.toString();
      sb.setLength(0);
      String line;
      while ((line = sr.readLine()) != null) {
        sb.append(ids).append(line).append('\n');
      }
      return sb.toString();
    } catch (IOException ex) {
      return text.toString();
    }
  }
}
