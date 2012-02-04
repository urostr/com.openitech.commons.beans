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
package com.openitech.db.model.factory;

import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author domenbasic
 */
public class JaxbMarshaller {

  private static JaxbMarshaller instance;

  public static JaxbMarshaller getInstance() {
    if (instance == null) {
      instance = new JaxbMarshaller();
    }
    return instance;
  }

  public String marshall(Object xmlObject) throws JAXBException {
    String result = null;
    StringWriter sw = new StringWriter();
    Marshaller marshaller = JAXBContext.newInstance(xmlObject.getClass()).createMarshaller();
    try {
      marshaller.marshal(xmlObject, sw);
      result = sw.toString();
    } catch (Exception ex) {
    }
    return result;
  }
}
