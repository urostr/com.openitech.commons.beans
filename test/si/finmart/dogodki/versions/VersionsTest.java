/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package si.finmart.dogodki.versions;

import junit.framework.TestCase;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.events.Event;
import com.openitech.value.fields.FieldValue;
import java.sql.SQLException;
import java.util.Calendar;

/**
 *
 * @author domenbasic
 */
public class VersionsTest extends TestCase {

  public VersionsTest(String testName) {
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
  public void testHello() throws SQLException {
    DbConnection.register();

    FieldValue idPPFieldValue = new FieldValue("ID_PP", java.sql.Types.INTEGER, 1358333);
    FieldValue idOsebeFieldValue = new FieldValue("ID_RPP_OSEBE", java.sql.Types.VARCHAR, "AAA000002955");



    FieldValue idBlokadeFieldValue = new FieldValue("ID_BLOKADE", java.sql.Types.VARCHAR, "AAA000000025");

    Event e269 = new Event(269, "BL01");
    e269.addValue(idBlokadeFieldValue);
    e269.addValue(new FieldValue("BLOKADA_DATUM", java.sql.Types.DATE, new java.sql.Date(Calendar.getInstance().getTimeInMillis())));



    Event e214 = new Event(214, "RPPBLR02");
    e214.addValue(idBlokadeFieldValue);
    e214.setPrimaryKey(idBlokadeFieldValue);

    e269.getChildren().add(e214);

    //naziv
    Event e226 = new Event(226, "RPPNF01");
    e226.addValue(idPPFieldValue);
    e226.addValue(idOsebeFieldValue);
    e226.addValue(new FieldValue("RPP_NAZIV_IME", java.sql.Types.VARCHAR, "Dom"));
    e226.addValue(new FieldValue("RPP_NAZIV_PRIIMEK", java.sql.Types.VARCHAR, "Basss"));
    FieldValue ppNazivIDFieldValue = new FieldValue("PPNAZIVID", java.sql.Types.INTEGER, 1971013);
    e226.addValue(ppNazivIDFieldValue);
    e226.setPrimaryKey(ppNazivIDFieldValue);
    e269.getChildren().add(e226);


    setVersioned(e269);

    SqlUtilities.getInstance().updateEvent(e269);


  }

  private void setVersioned(Event event) {
    event.setVersioned(true);
    event.setVersionParent(new Event(269, "BL01"));
    for (Event child : event.getChildren()) {
      setVersioned(child);
    }
  }
}
