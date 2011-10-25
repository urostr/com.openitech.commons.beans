/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.db.model.xml.config.CachedTemporaryTable;
import com.openitech.db.model.xml.config.Workarea;
import com.openitech.xml.fieldactions.Script;
import com.openitech.xml.fieldlayout.Field;
import com.openitech.xml.fieldmodel.FieldModel;
import com.openitech.xml.sifrant.Sifrant;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author domenbasic
 */
public class JaxbUnmarshaller {

  private static JaxbUnmarshaller instance;

  public static JaxbUnmarshaller getInstance() {
    if (instance == null) {
      instance = new JaxbUnmarshaller();
    }
    return instance;
  }

  private Object unmarshall(Class clazz, String xml, boolean reMarshall) throws JAXBException {
    Object result = null;
    Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
    try {
      result = unmarshaller.unmarshal(new StringReader(xml));
    } catch (Exception ex) {
      if (reMarshall) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Xml ima napacno shemo. Class={0}", clazz);
        result = reUnmarshall(clazz, xml);
      } else {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, null, ex);
        throw new JAXBException(ex);
      }
    }
    return result;
  }

  public Object unmarshall(Class clazz, String xml) throws JAXBException {
    return unmarshall(clazz, xml, true);
  }

  public Object unmarshall(Class clazz, Clob clob) throws JAXBException, SQLException {
    Object result = null;
    Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();

    try {
      result = unmarshaller.unmarshal(clob.getCharacterStream());
    } catch (Exception ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Xml ima napacno shemo. Class={0}", clazz);
      String subString = clob.getSubString(1, (int) clob.length());

      result = reUnmarshall(clazz, subString);
    }
    return result;
  }

  public Object unmarshall(Class clazz, java.io.Reader reader) throws JAXBException {
    Object result = null;
    Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
    try {
      result = unmarshaller.unmarshal(reader);
    } catch (Exception ex) {
      try {
        final char[] buffer = new char[0x10000];
        StringBuilder out = new StringBuilder();
        int read;
        do {
          read = reader.read(buffer, 0, buffer.length);
          if (read > 0) {
            out.append(buffer, 0, read);
          }
        } while (read >= 0);

        result = reUnmarshall(clazz, out.toString());

      } catch (Exception ex2) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, null, ex);

      }
    }
    return result;
  }

  public Object unmarshall(Class clazz, InputStream inputStream) throws JAXBException {
    Object result = null;
    Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
    try {


      result = unmarshaller.unmarshal(inputStream);
    } catch (Exception ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Xml ima napacno shemo. Class={0}", clazz);
      try {
        final char[] buffer = new char[0x10000];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream);
        int read;
        do {
          read = in.read(buffer, 0, buffer.length);
          if (read > 0) {
            out.append(buffer, 0, read);
          }
        } while (read >= 0);

        result = reUnmarshall(clazz, out.toString());
      } catch (Exception ex2) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, null, ex);

      }
    }
    return result;
  }

  private Object reUnmarshall(Class clazz, String subString) throws JAXBException {
    if (clazz.isAssignableFrom(Workarea.class)) {
      subString = subString.replaceAll("http://xml.netbeans.org/schema/sifranti_custom", "http://xml.openitech.com/schema/datasource");
    } else if (clazz.isAssignableFrom(CachedTemporaryTable.class)) {
      subString = subString.replaceAll("http://xml.netbeans.org/schema/sifranti_custom", "http://xml.openitech.com/schema/datasource");
    } else if (clazz.isAssignableFrom(FieldModel.class)) {
      subString = subString.replaceAll("http://xml.netbeans.org/schema/sifranti_custom", "http://xml.openitech.com/schema/fieldmodel");
    } else if (clazz.isAssignableFrom(Sifrant.class)) {
      subString = subString.replaceAll("http://xml.netbeans.org/schema/sifranti_custom", "http://xml.openitech.com/schema/sifrantmodel");
    } else if (clazz.isAssignableFrom(Script.class)) {
      subString = subString.replaceAll("http://xml.netbeans.org/schema/field_actions", "http://xml.openitech.com/schema/fieldactions");
    } else if (clazz.isAssignableFrom(Field.class)) {//fieldlayout
      subString = subString.replaceAll("http://xml.netbeans.org/schema/FieldLayout", "http://xml.openitech.com/schema/fieldlayout");
    } else {
      System.out.println("Ne znam pretvorit classa: " + clazz + " string: " + subString);
    }
    return unmarshall(clazz, subString, false);
  }
}
