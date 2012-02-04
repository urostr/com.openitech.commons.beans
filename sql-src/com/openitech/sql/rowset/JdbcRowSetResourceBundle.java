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
 * @(#)JdbcRowSetResourceBundle.java	1.5 10/03/23
 * 
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved. 
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.openitech.sql.rowset;

import java.io.*;
import java.util.*;
import java.lang.*;

/**
 * This class is used to help in localization of resources,
 * especially the exception strings.
 *
 * @author Amit Handa
 */

public class JdbcRowSetResourceBundle implements Serializable {

    /**
     * This <code>String</code> variable stores the location  
     * of the resource bundle location.
     */
    static String fileName;
    
    /**
     * This variable will hold the <code>PropertyResourceBundle</code>
     * of the text to be internationalized. 
     */
    transient PropertyResourceBundle propResBundle;
           
    /**
     * The constructor initializes to this object 
     * 
     */
    static JdbcRowSetResourceBundle jpResBundle;
    
    /**
     * The varible which will represent the properties
     * the suffix or extension of the resource bundle.
     **/
    private static final String PROPERTIES = "properties";
    
    /**
     * The varibale to represent underscore
     **/
    private static final String UNDERSCORE = "_";
    
    /**
     * The variable which will represent dot
     **/
    private static final String DOT = ".";
    
    /**
     * The variable which will represent the slash. 
     **/
    private static final String SLASH = "/";

    /**
     * The variable where the default resource bundle will
     * be placed.
     **/
    private static final String PATH = "com/sun/rowset/RowSetResourceBundle";
    
    /**
     * The constructor which initializes the resource bundle.
     * Note this is a private constructor and follows Singleton 
     * Design Pattern.
     * 
     * @throws IOException if unable to load the ResourceBundle
     * according to locale or the default one.
     */
    private JdbcRowSetResourceBundle () throws IOException {
        // Try to load the resource bundle according 
        // to the locale. Else if no bundle found according
        // to the locale load the default.
        
        // In default case the default locale resource bundle
        // should always be loaded else it 
        // will be difficult to throw appropriate 
        // exception string messages.
        Locale locale = Locale.getDefault();
        
        // Load appropriate bundle according to locale
         propResBundle = (PropertyResourceBundle) ResourceBundle.getBundle(PATH, 
	                   locale, Thread.currentThread().getContextClassLoader());    
	                   
   }	
    
    /**
     * This method is used to get a handle to the 
     * initialized instance of this class. Note that
     * at any time there is only one instance of this 
     * class initialized which will be returned.
     *
     * @throws IOException if unable to find the RowSetResourceBundle.properties
     */
    public static JdbcRowSetResourceBundle getJdbcRowSetResourceBundle() 
    throws IOException {

         if(jpResBundle == null){
	     synchronized(JdbcRowSetResourceBundle.class) {
	        if(jpResBundle == null){
	            jpResBundle = new JdbcRowSetResourceBundle();
	        } //end if   
	     } //end synchronized block    
         } //end if
         return jpResBundle;
    }

    /**
     * This method returns an enumerated handle of the keys
     * which correspond to values translated to various locales.
     * 
     * @returns an enumerated keys which have messages tranlated to
     * corresponding locales.
     */
    public Enumeration getKeys() {
       return propResBundle.getKeys();
    }
    
    
    /**
     * This method takes the key as an argument and 
     * returns the corresponding value reading it 
     * from the Resource Bundle loaded earlier.
     *
     * @returns value in locale specific language
     * according to the key passed.
     */
    public Object handleGetObject(String key) {
       return propResBundle.handleGetObject(key);
    }
    
    static final long serialVersionUID = 436199386225359954L;

}   
