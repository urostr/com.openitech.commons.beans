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
