package com.openitech.util;

import com.openitech.Settings;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * <p>Title: J2EE Common components</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Prosoft-Consulting d.o.o.</p>
 * @author Uroš Trojar
 * $Revision: 1.7 $
 */
public class ReadInputStream {

  public static String getResourceAsString(Class clazz, String resourceName) {
    return getResourceAsString(clazz, resourceName, "UTF-8");
  }

  public static String getResourceAsString(Class clazz, String resourceName, String charsetName) {
    String[] result = getResourceAsString(clazz, resourceName, false, charsetName);
    return result.length > 0 ? result[0] : null;
  }

  public static String[] getResourceAsString(Class clazz, String resourceName, boolean batch) {
    return getResourceAsString(clazz, resourceName, batch, "UTF-8");
  }

  public static String[] getResourceAsString(Class clazz, String resourceName, boolean batch, String charsetName) {
    return getResourceAsString(clazz.getResourceAsStream(resourceName), batch, charsetName);
  }

  public static String getResourceAsString(InputStream is, String charsetName) {
    String[] result = getResourceAsString(is, false, charsetName);
    return result.length > 0 ? result[0] : null;
  }

  public static String[] getResourceAsString(InputStream is, boolean batch, String charsetName) {
    BufferedReader bis = null;
    if (charsetName != null) {
      try {
        bis = new BufferedReader(new InputStreamReader(is, charsetName));
      } catch (UnsupportedEncodingException ex) {
        Logger.getLogger(ReadInputStream.class.getName()).log(Level.WARNING, null, ex);
      }
    }
    if (bis == null) {
      bis = new BufferedReader(new InputStreamReader(is));
    }
    StreamTokenizer st = new StreamTokenizer(bis);

    st.resetSyntax();
    st.eolIsSignificant(true);
    st.wordChars('!', '~');
    st.whitespaceChars(' ', ' ');
    st.whitespaceChars('\t', '\t');
    if (!batch) {
      st.whitespaceChars(';', ';');
    }


    StringBuilder sb = new StringBuilder(108);
    String ls = System.getProperty("line.separator");

    int token;


    try {
      while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
        if (token == StreamTokenizer.TT_EOL) {
          sb.append(ls);
        } else if (st.sval != null) {
          sb.append(st.sval).append(" ");
        } else {
          sb.append(" ");
        }
      }

      bis.close();

      String[] sqls = sb.toString().split(";");
      List<String> result = new ArrayList<String>(sqls.length);

      for (String sql : sqls) {
        if (sql.trim().length() > 0 && !sql.startsWith("--")) {
          result.add(sql.trim());
        }
      }
      return result.toArray(new String[result.size()]);
    } catch (IOException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error reading the file.", ex);
      return null;
    }
  }
}
