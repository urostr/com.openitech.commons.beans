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


package com.openitech.io;

import com.openitech.Settings;
import com.openitech.sql.util.SqlUtilities;
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
 * @author Uros Trojar
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
    st.wordChars(' ', ' ');
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
        sql = sql.trim();
        if (sql.trim().length() > 0 && !sql.startsWith("--")) {
          result.add(getReplacedSql(sql));
        }
      }
      return result.toArray(new String[result.size()]);
    } catch (IOException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Error reading the file.", ex);
      return null;
    }
  }

  public static String getReplacedSql(String sql) {
    if (sql != null) {
      sql = sql.replaceAll("<%ChangeLog%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB));
      sql = sql.replaceAll("<%RPP%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPP_DB, SqlUtilities.RPP_DB));
      sql = sql.replaceAll("<%RPE%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPE_DB, SqlUtilities.RPE_DB));
      sql = sql.replaceAll("<%MVIEWCACHE%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.MVIEW_CACHE_DB, SqlUtilities.MVIEW_CACHE_DB));
    }

    return sql;
  }

  public static String[] getReplacedSqls(String... sqls) {
    if (sqls != null) {
      for (int i = 0; i < sqls.length; i++) {
        sqls[i] = getReplacedSql(sqls[i]);
      }
    }

    return sqls;
  }
}
