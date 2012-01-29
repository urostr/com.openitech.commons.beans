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
 * %W% %E% 
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.openitech.sql.rowset.internal;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import com.openitech.sql.rowset.*;
import javax.sql.rowset.*;


/**
 * An implementation of the <code>DefaultHandler</code> interface, which
 * handles all the errors, fatalerrors and warnings while reading the xml file.
 * This is the ErrorHandler which helps <code>WebRowSetXmlReader</code>
 * to handle any errors while reading the xml data.
 */


public class XmlErrorHandler extends DefaultHandler {
       public int errorCounter = 0;

       public void error(SAXParseException e) throws SAXException {
           errorCounter++;
       
       }
       
       public void fatalError(SAXParseException e) throws SAXException {
	   errorCounter++;
       
       }
       
       public void warning(SAXParseException exception) throws SAXException {
       
       }	
}  
    