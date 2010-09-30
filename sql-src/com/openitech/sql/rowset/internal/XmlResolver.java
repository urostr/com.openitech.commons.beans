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
