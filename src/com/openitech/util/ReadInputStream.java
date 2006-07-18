package com.openitech.util;

import com.openitech.Settings;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * <p>Title: J2EE Common components</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Prosoft-Consulting d.o.o.</p>
 * @author Uroš Trojar
 * $Revision: 1.1 $
 */
public class ReadInputStream {
  public static String getResourceAsString(Class clazz, String resourceName) {
    String[] result = getResourceAsString(clazz, resourceName, false);
    return result.length>0?result[0]:null;
  }
  public static String[] getResourceAsString(Class clazz, String resourceName, boolean batch) {
    BufferedReader bis = new BufferedReader(new InputStreamReader(clazz.getResourceAsStream(resourceName)));
    StreamTokenizer st = new StreamTokenizer(bis);

    st.resetSyntax();
    st.eolIsSignificant(true);
    st.wordChars('!', '~');
    st.whitespaceChars(' ', ' ');
    st.whitespaceChars('\t', '\t');
    if (!batch)
      st.whitespaceChars(';',';');
    else
      st.commentChar('-');


    StringBuffer sb = new StringBuffer(108);
    String ls = System.getProperty("line.separator");

    int token;


    try {
      while ( (token=st.nextToken()) != StreamTokenizer.TT_EOF)
        if (token == StreamTokenizer.TT_EOL)
          sb.append(ls);
        else if (st.sval!=null)
          sb.append(st.sval).append(" ");
        else
          sb.append(" ");
      
      bis.close();
      
      return sb.toString().split(";");
    }
    catch (IOException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error reading the file.", ex);
      return null;
    }
  }

}
