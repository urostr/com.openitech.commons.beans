/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.primarkey;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.sql.SQLPrimaryKeyException;
import com.openitech.io.ReadInputStream;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.VariousValue;
import com.openitech.value.events.EventPK;
import com.openitech.value.fields.Field;
import com.openitech.value.fields.FieldValue;
import com.sun.rowset.CachedRowSetImpl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import junit.framework.TestCase;
import net.sourceforge.jtds.jdbc.CachedResultSet;

/**
 *
 * @author domenbasic
 */
public class ImportPrimaryKey extends TestCase {

  public ImportPrimaryKey(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  // TODO add test methods here. The name must begin with 'test'. For example:
  public void testImportPrimaryKeys() {
    List<Integer> podvojeniEventId = new ArrayList<Integer>();
    List<Integer> deletaniEventId = new ArrayList<Integer>();
    boolean commit = false;
    try {
      DbConnection.register();
      int param = 1;

      //prepare
      SqlUtilities.getInstance().beginTransaction();
      Connection connection = ConnectionManager.getInstance().getTxConnection();
      PreparedStatement allEvents = connection.prepareStatement(ReadInputStream.getResourceAsString(ImportPrimaryKey.class, "sql/events.sql", "cp1250"));
      PreparedStatement eventValues = connection.prepareStatement(ReadInputStream.getResourceAsString(ImportPrimaryKey.class, "sql/primaryKey.sql", "cp1250"));

      //najdi vse evente
      ResultSet rs_allEvents = allEvents.executeQuery();
      while (rs_allEvents.next()) {
        int eventId = rs_allEvents.getInt("Id");
        int idSIfranta = rs_allEvents.getInt("idSIfranta");
        String idSifre = rs_allEvents.getString("IdSifre");

        //ustvari nov primarykey za shranjevanje
        EventPK eventPK = new EventPK();
        eventPK.setEventId(eventId);
        eventPK.setIdSifranta(idSIfranta);
        eventPK.setIdSifre(idSifre);

        //najdi eventValues za dolo�en eventId
        param = 1;
        eventValues.clearParameters();
        eventValues.setInt(param++, 1);
        eventValues.setInt(param++, eventId);
        ResultSet rs_primaryKeys = eventValues.executeQuery();
        while (rs_primaryKeys.next()) {
          //za vsak PK dodaj polje v eventPK
          int idPolja = rs_primaryKeys.getInt("idPolja");
          int tipPolja = rs_primaryKeys.getInt("TipPolja");
          int fieldValueIndex = rs_primaryKeys.getInt("fieldValueIndex");
          long valueId = rs_primaryKeys.getLong("valueId");
          String imePolja = rs_primaryKeys.getString("ImePolja");
          if (fieldValueIndex > 1) {
            imePolja = imePolja + fieldValueIndex;
          }
          eventPK.addField(new FieldValue(idPolja, imePolja, tipPolja, fieldValueIndex, new VariousValue(valueId, tipPolja, "")));
        }
        try {
          //shrani PK
          SqlUtilities.getInstance().storePrimaryKey(eventPK);
        } catch (SQLPrimaryKeyException ex) {

          
          //uredi podvojene zapise
          //ce so ostale vrednosti enake, zbrisi stare eventId (valid= false)
          //drugace podvojene pa izpisi za rocni pregled

          //preveri ostale vrednosti
          param = 1;
          eventValues.clearParameters();
          eventValues.setInt(param++, 0);
          eventValues.setInt(param++, eventId);
          CachedRowSet rs_allEventValues = new CachedRowSetImpl();
          rs_allEventValues.populate(eventValues.executeQuery());

          PreparedStatement oldEevntPK = connection.prepareStatement(ReadInputStream.getResourceAsString(ImportPrimaryKey.class, "sql/findOldPK.sql", "cp1250"));
          param = 1;
          oldEevntPK.clearParameters();
          oldEevntPK.setString(param++, eventPK.toHexString());
          ResultSet rs_oldEevntPK = oldEevntPK.executeQuery();
          int podvojenPK_eventId = -1;
          if (rs_oldEevntPK.next()) {
            podvojenPK_eventId = rs_oldEevntPK.getInt("EventId");
          }
          param = 1;
          eventValues.clearParameters();
          eventValues.setInt(param++, 0);
          eventValues.setInt(param++, podvojenPK_eventId);
          CachedRowSet rs_old_allEventValues = new CachedRowSetImpl();
          rs_old_allEventValues.populate(eventValues.executeQuery());
         
          boolean enaka = true;
          while (rs_allEventValues.next()) {
            if (rs_old_allEventValues.next()) {
              int idPolja = rs_allEventValues.getInt("idPolja");
              int idPoljaOld = rs_old_allEventValues.getInt("idPolja");

              int fieldValueIndex = rs_allEventValues.getInt("fieldValueIndex");
              int fieldValueIndexOld = rs_old_allEventValues.getInt("fieldValueIndex");

              long valueId = rs_allEventValues.getLong("valueId");
              long valueIdOld = rs_old_allEventValues.getLong("valueId");

              if (idPolja != idPoljaOld
                      || fieldValueIndex != fieldValueIndexOld
                      || valueId != valueIdOld) {
                enaka = false;
                break;
              }
            } else {
              enaka = false;
            }
          }
          if (rs_old_allEventValues.next()) {
            enaka = false;
          }

          //ce sta enaka zbrisem manjsi eventId
          if (enaka) {
            int deleteEventId = eventId > podvojenPK_eventId ? podvojenPK_eventId : eventId;
            SqlUtilities.getInstance().deleteEvent(deleteEventId);
            deletaniEventId.add(deleteEventId);
          }else{
            podvojeniEventId.add(eventId);
          }
        }
      }
      commit = true;
    } catch (SQLException ex) {
      Logger.getLogger(ImportPrimaryKey.class.getName()).log(Level.SEVERE, null, ex);

    } finally {
      try {
        SqlUtilities.getInstance().endTransaction(commit);
      } catch (SQLException ex1) {
        Logger.getLogger(ImportPrimaryKey.class.getName()).log(Level.SEVERE, null, ex1);
      }
      izposiEventIdje(podvojeniEventId, deletaniEventId);
    }
  }

  private void izposiEventIdje(List<Integer> podvojeniEventId, List<Integer> deletaniEventId) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Podvojeni zapisi:\n");
    for (Integer integer : podvojeniEventId) {
      stringBuilder.append(integer);
      stringBuilder.append("\n");
    }
    stringBuilder.append("\ndeletani zapisi:\n");
    for (Integer integer : deletaniEventId) {
      stringBuilder.append(integer);
      stringBuilder.append("\n");
    }
    System.out.println(stringBuilder.toString());
  }
}