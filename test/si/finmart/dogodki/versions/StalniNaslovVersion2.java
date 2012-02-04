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
package si.finmart.dogodki.versions;

import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.events.Event;
import com.openitech.value.fields.FieldValue;
import java.sql.SQLException;

/**
 *
 * @author domenbasic
 */
public class StalniNaslovVersion2 {

  public static void main(String[] args) throws SQLException {
    new StalniNaslovVersion2().test();
  }

  public void test() throws SQLException {
    DbConnection.register();

    FieldValue idPPFieldValue = new FieldValue("ID_PP", java.sql.Types.INTEGER, 1358333);
//
    //prvi dogodek
    Event e175 = new Event(175, "RPP01");
    e175.setEventSource(18);
    e175.addValue(idPPFieldValue);
    e175.addValue(new FieldValue("RPP_STATUS_PP", java.sql.Types.VARCHAR, "A"));
    e175.setPrimaryKey(idPPFieldValue);

    FieldValue idOsebeFieldValue = new FieldValue("ID_RPP_OSEBE", java.sql.Types.VARCHAR, "AAA000002955");

    //oseba
    Event e266 = new Event(266, "RPPO01");
    e266.addValue(idPPFieldValue);
    e266.addValue(idOsebeFieldValue);
    e266.addValue(new FieldValue("RPP_NAZIVI_TIP", java.sql.Types.VARCHAR, "RPPTIP01"));
    e266.setPrimaryKey(idPPFieldValue, idOsebeFieldValue);
    e175.getChildren().add(e266);

    //tip osebe, fizicna oseba
    Event e225 = new Event(225, "RPPTIP01");
    e225.addValue(idPPFieldValue);
    e225.addValue(idOsebeFieldValue);
    e225.setPrimaryKey(idPPFieldValue, idOsebeFieldValue);
    e266.getChildren().add(e225);

    //naziv
    Event e226 = new Event(226, "RPPNF01");
    e226.addValue(idPPFieldValue);
    e226.addValue(idOsebeFieldValue);
    e226.addValue(new FieldValue("RPP_NAZIV_IME", java.sql.Types.VARCHAR, "Dosso23"));
    e226.addValue(new FieldValue("RPP_NAZIV_PRIIMEK", java.sql.Types.VARCHAR, "Bašaaè3"));
    FieldValue ppNazivIDFieldValue = new FieldValue("PPNAZIVID", java.sql.Types.INTEGER, 1971013);
    e226.addValue(ppNazivIDFieldValue);
    e226.setPrimaryKey(ppNazivIDFieldValue);
    e225.getChildren().add(e226);

    Event e214 = new Event(214, "RPPBLR01");
    FieldValue idBlokadeFieldValue = new FieldValue("ID_BLOKADE", java.sql.Types.VARCHAR, "AAA000000025");
    e214.addValue(idBlokadeFieldValue);
    e214.addValue(new FieldValue("RPP_NAZIV_PRIIMEK", java.sql.Types.VARCHAR, "aaaaa"));
    e214.setPrimaryKey(idBlokadeFieldValue);

    e225.getChildren().add(e214);

    setVersioned(e175);
    
    SqlUtilities.getInstance().updateEvent(e175);


  }

  private void setVersioned(Event event) {
    event.setVersioned(true);
    event.setVersionParent(new Event(175, "RPP01"));
    for (Event child : event.getChildren()) {
      setVersioned(child);
    }
  }
}
