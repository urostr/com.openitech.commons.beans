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
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


public class FilterDocumentCaretListener extends DataSourceFilterScheduler implements CaretListener {
  protected Document document;
  protected DataSourceFilters filter;
  protected DataSourceFilters.SeekType seek_type;

  public FilterDocumentCaretListener(Document document, DataSourceFilters filter, DataSourceFilters.SeekType seek_type) {
    this(document, filter, seek_type, DataSourceFilterScheduler.DELAY);
  }
  
  public FilterDocumentCaretListener(Document document, DataSourceFilters filter, DataSourceFilters.SeekType seek_type, long delay) {
    this.document = document;
    this.filter = filter;
    this.seek_type = seek_type;
    this.delay = delay;
  }
  
  private String getText(CaretEvent e) {
    String txt;
    try {
      int min = Math.min(e.getDot(), e.getMark());
      //int max = Math.max(e.getDot(), e.getMark());
      //txt = document.getText(min==max?0:min, max);
      txt = document.getText(0, min==0?document.getLength():min);
    }  catch (BadLocationException err) {
      txt = null;
    }
    return txt;
  }  

  public void caretUpdate(CaretEvent e) {
    caretUpdate(e, delay);
  }
  
  protected void caretUpdate(CaretEvent e, long delay) {
    schedule(new SeekValueUpdateRunnable(filter, seek_type, getText(e)), delay);
  }
}