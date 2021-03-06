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


package com.openitech.db.filters;

import com.openitech.db.filters.DataSourceFilterScheduler.SeekValueUpdateRunnable;
import com.openitech.text.FormatFactory;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class FilterDocumentListener extends DataSourceFilterScheduler implements DocumentListener {

  protected DataSourceFilters filter;
  protected DataSourceFilters.AbstractSeekType<? extends Object> seek_type;

  public FilterDocumentListener(DataSourceFilters filter, DataSourceFilters.AbstractSeekType<? extends Object> seek_type) {
    this(filter, seek_type, DataSourceFilterScheduler.DELAY);
  }

  public FilterDocumentListener(DataSourceFilters filter, DataSourceFilters.AbstractSeekType<? extends Object> seek_type, long delay) {
    this.filter = filter;
    this.seek_type = seek_type;
    this.delay = delay;
  }

  public String getText(DocumentEvent e) {
    return getText(e.getDocument());
  }

  public String getText(Document doc) {
    String txt;
    try {
      txt = doc.getText(0, doc.getLength());
    } catch (BadLocationException err) {
      txt = null;
    }
    return txt;
  }

  public void changedUpdate(DocumentEvent e) {
    setSeekValue(e);
  }

  public void insertUpdate(DocumentEvent e) {
    setSeekValue(e);
  }

  public void removeUpdate(DocumentEvent e) {
    setSeekValue(e);
  }

  protected void setSeekValue(final DocumentEvent e) {
    if (seek_type instanceof DataSourceFilters.DateSeekType) {
      try {
        java.util.Date value = FormatFactory.DATE_FORMAT.parse(getText(e));
        schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, value));
      } catch (ParseException ex) {
        Logger.getLogger(FilterDocumentListener.class.getName()).log(Level.WARNING, ex.getMessage());
        schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, null));
      }
    } else if (seek_type instanceof DataSourceFilters.IntegerSeekType) {
      try {
        Integer value = Integer.parseInt(getText(e).trim());
        schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, value));
      } catch (NumberFormatException ex) {
        Logger.getLogger(FilterDocumentListener.class.getName()).log(Level.WARNING, ex.getMessage());
        schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, null));
      }
    } else if (seek_type instanceof DataSourceFilters.SeekTypeWrapper) {
      DataSourceFilters.AbstractSeekType<? extends Object> wrapperSeekType = ((DataSourceFilters.SeekTypeWrapper) seek_type).getWrapperFor();
      if (wrapperSeekType instanceof DataSourceFilters.DateSeekType) {
        try {
          java.util.Date value = FormatFactory.DATE_FORMAT.parse(getText(e));
          schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, value));
        } catch (ParseException ex) {
          Logger.getLogger(FilterDocumentListener.class.getName()).log(Level.WARNING, ex.getMessage());
          schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, null));
        }
      } else if (wrapperSeekType instanceof DataSourceFilters.IntegerSeekType) {
        try {
          Integer value = Integer.parseInt(getText(e).trim());
          schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, value));
        } catch (NumberFormatException ex) {
          Logger.getLogger(FilterDocumentListener.class.getName()).log(Level.WARNING, ex.getMessage());
          schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, null));
        }
      } else {
        schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, getText(e)));
      }
    } else {
      schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, getText(e)));
    }
  }
}
