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
package com.openitech.sql.logger;

import com.openitech.auth.LoginContextManager;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.util.Equals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * @author uros
 */
public class SQLHandler extends Handler {

  private final List<LogRecord> queue = Collections.synchronizedList(new ArrayList<LogRecord>(100));
  private final Timer timer = new Timer("SQL-Handler-Log", true);
  private SqlUtilities sqlUtilites;


  public SQLHandler() {
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        flush();
      }
    }, 1000L, 100L);
  }

  @Override
  public boolean isLoggable(LogRecord record) {
    return super.isLoggable(record)&&!Equals.equals(record.getLoggerName(),SQLLogger.class.getName());
  }

  @Override
  public void publish(LogRecord record) {
    if (isLoggable(record)) {
      queue.add(record);
    }
  }

  @Override
  public void flush() {
    if (LoginContextManager.getInstance().isLoggedOn()) {
      if (sqlUtilites==null) {
        sqlUtilites = SqlUtilities.getInstance();
      }
      sqlUtilites.logActions(nextLogQueue());
    }
  }

  private List<LogRecord> nextLogQueue() {
    List<LogRecord> log = Arrays.asList(queue.toArray(new LogRecord[queue.size()]));
    queue.clear();
    return log;
  } 

  @Override
  public void close() throws SecurityException {
    flush();
    timer.cancel();
  }
}
