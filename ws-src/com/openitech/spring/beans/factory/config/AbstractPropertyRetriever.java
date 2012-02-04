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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.spring.beans.factory.config;

import com.openitech.sql.properties.*;
import com.openitech.spring.beans.factory.config.PropertyType;
import com.openitech.io.ReadInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public abstract class AbstractPropertyRetriever implements PropertyRetriever {

  protected static final String ls = System.getProperty("line.separator");

  public AbstractPropertyRetriever() {
  }

  protected String getValue(PropertyType type, InputStream is, String charsetName) throws IOException {
    String result = null;
    if (type.equals(PropertyType.SQL)) {
      result = ReadInputStream.getResourceAsString(is, charsetName);
    } else {
      StringBuilder sb = new StringBuilder();
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
      String ln;
      while ((ln = bis.readLine()) != null) {
        sb.append(ln).append(ls);
      }
      result = sb.toString();
    }
    return result;
  }

  @Override
  public Object getValue(PropertyType type, String properyName) {
    return getValue(type, properyName, Charset.defaultCharset().name());
  }
  
  @Override
  public Object getValue(PropertyType type, String properyName, String charsetName) {
    Object result = null;
    File localPropertyFile = new File(properyName);
    try {
      if (localPropertyFile.exists()) {
        result = getValue(type, new FileInputStream(localPropertyFile), charsetName);
      }
      if (result == null) {
        result = getRemoteValue(type, properyName, charsetName);
      }
      if (result == null) {
        InputStream is = getClass().getResourceAsStream(properyName);
        if (is != null) {
          result = getValue(type, is, charsetName);
        }
      }
    } catch (Exception ex) {
      Logger.getLogger(SqlPropertyRetriever.class.getName()).log(Level.SEVERE, null, ex);
      result = null;
    }
    return result;
  }

  public abstract Object getRemoteValue(PropertyType type, String properyName, String charsetName);
}
