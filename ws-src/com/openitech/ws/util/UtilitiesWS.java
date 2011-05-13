/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.ws.util;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.spring.Beans;
import com.openitech.db.model.web.DbWebRowSetImpl;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.fields.Field;
import com.openitech.value.events.ActivityEvent;
import com.openitech.xml.wrs.WSParameters;
import com.openitech.xml.wrs.WebRowSet;
import com.openitech.xml.ws.entry.Command;
import com.openitech.xml.ws.entry.Entry;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import javax.swing.JOptionPane;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 *
 * @author uros
 */
public class UtilitiesWS {

  private static UtilitiesWS instance;
  private com.sun.jersey.api.client.WebResource webResource;
  private com.sun.jersey.api.client.Client client;

  public static UtilitiesWS getInstance() {
    if (instance == null) {
      instance = new UtilitiesWS();

    }
    return instance;
  }

  public UtilitiesWS() {
    client = new com.sun.jersey.api.client.Client();
  }

  public synchronized CachedRowSet getGeneratedFields(int idSifranta, String idSifre, boolean visibleOnly, ActivityEvent activityEvent) throws SQLException {

    WSParameters wsParameters = new WSParameters();
    int param = 1;
    WSParameters.Parameter parameter = new WSParameters.Parameter();
    parameter.setIndex(param++);
    parameter.setValue(idSifranta);
    wsParameters.getParameter().add(parameter);

    parameter = new WSParameters.Parameter();
    parameter.setIndex(param++);
    parameter.setValue(idSifre);
    wsParameters.getParameter().add(parameter);



    if (activityEvent != null) {
      parameter = new WSParameters.Parameter();
      parameter.setIndex(param++);
      parameter.setValue((int) activityEvent.getActivityId());
      wsParameters.getParameter().add(parameter);

      parameter = new WSParameters.Parameter();
      parameter.setIndex(param++);
      parameter.setValue(activityEvent.getIdSifranta());
      wsParameters.getParameter().add(parameter);

      parameter = new WSParameters.Parameter();
      parameter.setIndex(param++);
      parameter.setValue(activityEvent.getIdSifre());
      wsParameters.getParameter().add(parameter);
    } else {
      param += 3;
    }
    parameter = new WSParameters.Parameter();
    parameter.setIndex(param++);
    parameter.setValue(visibleOnly);
    wsParameters.getParameter().add(parameter);


    Logger.getAnonymousLogger().log(Level.INFO, "getGeneratedFields:idsifranta={0} idSifre={1} visibleOnly={2} activityEvent={3}", new Object[]{idSifranta, idSifre, visibleOnly, activityEvent != null ? activityEvent.toString() : "null"});


    WebRowSet rowSet = callWebServiceBean(Beans.GET_GENERATED_FIELDS, Command.ROWSET, wsParameters, WebRowSet.class);
    Logger.getAnonymousLogger().info("Received getGeneratedFields");

//        StringWriter stringWriter = new StringWriter();
//        try {
//            JAXBContext jaxbContext = JAXBContext.newInstance(com.openitech.xml.wrs.WebRowSet.class);
//            Marshaller marshaller = jaxbContext.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//
//            marshaller.marshal(rowSet, stringWriter);
//
//        } catch (Exception ex) {
//        }
//        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(stringWriter.toString());

    DbWebRowSetImpl wrs = new DbWebRowSetImpl();
    wrs.readXml(rowSet);
    CachedRowSet generatedFields = SqlUtilities.getInstance().getGeneratedFields(idSifranta, idSifre, visibleOnly, activityEvent);

    wrs.beforeFirst();
    generatedFields.beforeFirst();
    while (wrs.next()) {
      int id1 = wrs.getInt("Id");
      generatedFields.beforeFirst();
      boolean found = false;
      while (generatedFields.next()) {
        int id2 = generatedFields.getInt("Id");
        if (id1 == id2) {
          found = true;

        }
      }
      if (!found) {
        JOptionPane.showMessageDialog(null, "Generated field ne dela dobro!");
      }
    }
    wrs.beforeFirst();
    return wrs;
  }

  public synchronized boolean hasFields(int idSifranta, String idSifre, Set<Field> defaultValues, boolean visibleOnly, ActivityEvent activityEvent) {

    throw new UnsupportedOperationException();
  }

  public <T> T callWebServiceBean(String beanName, Command command, Object param, Class<T> returnType) {
    T result = null;
    StringWriter stringWriter = null;

    webResource = client.resource(ConnectionManager.getInstance().getProperty(ConnectionManager.DB_ENTRY_SERVICE));


    Entry entry = new Entry();
    entry.setBeanName(beanName);
    entry.setCommand(command);


    stringWriter = new StringWriter();
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(param.getClass());
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
      marshaller.marshal(param, stringWriter);

    } catch (Exception ex) {
    }
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(stringWriter.toString());

    entry.setJAXBXml(stringWriter.toString());

    stringWriter = new StringWriter();
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(Entry.class);
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(entry, stringWriter);

    } catch (Exception ex) {
    }
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(stringWriter.toString());

    Object post = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(returnType, entry);
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(post.toString());
    result = (T) post;


    return result;
  }
}
