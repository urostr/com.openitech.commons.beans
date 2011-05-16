/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.logger;

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
  private final SqlUtilities sqlUtilites = SqlUtilities.getInstance();


  public SQLHandler() {
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        flush();
      }
    }, 1000L);
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
    sqlUtilites.logActions(nextLogQueue());
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
