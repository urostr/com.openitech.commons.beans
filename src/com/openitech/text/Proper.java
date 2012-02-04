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
 * Proper.java
 *
 * Created on Nedelja, 2 september 2007, 13:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.text;

/**
 *
 * @author uros
 */
public class Proper {
  private static final java.util.regex.Pattern formatter = java.util.regex.Pattern.compile("(([\\s\\p{Punct}]*([^\\s^\\p{Punct}]))([^\\s^\\p{Punct}]*))");
  
  /** Creates a new instance of Proper */
  private Proper() {
  }
  
  public static <T extends CharSequence> String format(T text) {
    if (text==null)
      return null;
    else if (text.toString().equals(text.toString().toUpperCase())) {        
      StringBuilder result = new StringBuilder(((CharSequence) text).length());
      String veznik;
      
      java.util.regex.Matcher matcher = formatter.matcher(text);
      
      while (matcher.find()) {
        veznik=matcher.group(3).toLowerCase()+matcher.group(4).toLowerCase();
        if (result.length()>0&&(veznik.equals("za")||veznik.equals("v")||veznik.equals("pri")||veznik.equals("na")||veznik.equals("nad")||veznik.equals("pod")||veznik.equals("vas")||veznik.equals("in")))
          result.append(matcher.group(2).toLowerCase()).append(matcher.group(4).toLowerCase());
        else
          result.append(matcher.group(2).toUpperCase()).append(matcher.group(4).toLowerCase());
      }
      
      return result.toString();
    } else
      return text.toString();
  }
  
}
