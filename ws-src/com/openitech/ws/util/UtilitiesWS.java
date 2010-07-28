/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.ws.util;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.web.DbWebRowSetImpl;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.fields.Field;
import com.openitech.value.events.ActivityEvent;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
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

    public synchronized CachedRowSet getGeneratedFields(int idSifranta, String idSifre, boolean visibleOnly, ActivityEvent activityEvent) throws SQLException {
        if (client == null) {
            client = new com.sun.jersey.api.client.Client();

        }
        webResource = client.resource(ConnectionManager.getInstance().getProperty(ConnectionManager.DB_GENERATEDFIELDS_WS));
        webResource = webResource.queryParam("idsifranta", Integer.toString(idSifranta));
        if (idSifre != null) {
            webResource = webResource.queryParam("idsifre", idSifre);
        }
        webResource = webResource.queryParam("visibleonly", Boolean.toString(visibleOnly));
        if (activityEvent != null) {
            webResource = webResource.queryParam("activityid", Long.toString(activityEvent.getActivityId())).queryParam("activityidsifranta", Integer.toString(activityEvent.getIdSifranta())).queryParam("activityidsifre", activityEvent.getIdSifre());
        }

        Logger.getAnonymousLogger().info("getGeneratedFields:idsifranta=" + idSifranta + " idSifre=" + idSifre + " visibleOnly=" + visibleOnly + " activityEvent=" + (activityEvent != null ? activityEvent.toString() : "null"));


        com.openitech.xml.wrs.WebRowSet rowSet = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(com.openitech.xml.wrs.WebRowSet.class);
        Logger.getAnonymousLogger().info("Received getGeneratedFields");

        StringWriter stringWriter = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(com.openitech.xml.wrs.WebRowSet.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(rowSet, stringWriter);

        } catch (Exception ex) {
        }
        System.out.println(stringWriter.toString());

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
                System.err.println("Generated field ne dela dobro!");
            }
        }
        wrs.beforeFirst();
        return wrs;
    }

    public synchronized boolean hasFields(int idSifranta, String idSifre, Set<Field> defaultValues, boolean visibleOnly, ActivityEvent activityEvent) {

        return false;
    }
}
