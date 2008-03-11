/*
 * Document.java
 *
 * Created on Sreda, 27 februar 2008, 20:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.util;

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
            ((AbstractDocument)doc).replace(0, doc.getLength(), t,null);
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
}
