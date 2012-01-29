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
package com.openitech.db.model.sql;

import com.openitech.value.events.EventPK;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author domenbasic
 */
public class SQLPrimaryKeyException extends SQLException {

  private String reason = "";
  private EventPK eventPK;

  public SQLPrimaryKeyException(String reason, Throwable cause, EventPK eventPK) {
    super(reason, cause);
    this.reason = reason;
    this.eventPK = eventPK;

   // showDialog();

  }

  private void showDialog() {
    JOptionPane.showMessageDialog(null, reason + "\n\n" + eventPK.getDebugString(), "Napaka!", JOptionPane.ERROR_MESSAGE, null);
  }
}
