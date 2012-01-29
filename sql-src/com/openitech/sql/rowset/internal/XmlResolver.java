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
 * @(#)XmlResolver.java	1.5 10/03/23 
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.openitech.sql.rowset.internal;

import org.xml.sax.*;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * An implementation of the <code>EntityResolver</code> interface, which
 * reads and parses an XML formatted <code>WebRowSet</code> object.
 * This is an implementation of org.xml.sax
 * 
 */
public class XmlResolver implements EntityResolver {
	
	public InputSource resolveEntity(String publicId, String systemId) {
           String schemaName = systemId.substring(systemId.lastIndexOf("/"));

	   if(systemId.startsWith("http://java.sun.com/xml/ns/jdbc")) { 
	       return new InputSource(this.getClass().getResourceAsStream(schemaName));

	   } else {
	      // use the default behaviour
	      return null;
	   }
	   
	  
	   
	
       }
}
