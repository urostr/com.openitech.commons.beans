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
 * JDbLabel.java
 *
 * Created on April 2, 2006, 3:16 PM
 *
 * $Revision $
 */

package com.openitech.db.components;

import com.openitech.Settings;
import com.openitech.db.model.FieldObserver;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author uros
 */
public class JDbLabel extends JLabel implements FieldObserver {
  DbFieldObserver dbFieldObserver = new DbFieldObserver();
  Format format = null;
  
  private ActiveRowChangeWeakListener activeRowChangeWeakListener;
  
  
  /** Creates a new instance of JDbLabel */
  public JDbLabel() {
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_fieldValueChanged",null);
    } catch (NoSuchMethodException ex) {
      throw (RuntimeException) new IllegalStateException().initCause(ex);
    }
    dbFieldObserver.addActiveRowChangeListener(activeRowChangeWeakListener);
  }
  
  public void setDataSource(DbDataSource dataSource) {
    dbFieldObserver.setDataSource(dataSource);
  }
  
  public DbDataSource getDataSource() {
    return dbFieldObserver.getDataSource();
  }
  
  public void setColumnName(String columnName) {
    dbFieldObserver.setColumnName(columnName);
  }
  
  public String getColumnName() {
    return dbFieldObserver.getColumnName();
  }
  
  public void dataSource_fieldValueChanged(ActiveRowChangeEvent event) {
    if (format==null) {
      this.setText(dbFieldObserver.getValueAsText());
    } else {
      Object value = dbFieldObserver.getValueAsText();
      boolean wasNull = dbFieldObserver.wasNull();
      
      if (!wasNull) {
        if ((format instanceof NumberFormat)) {
          if (((String) value).length()>0) {
            if (((NumberFormat) format).getMaximumFractionDigits()==0)
              value = dbFieldObserver.getValueAsInt();
            else
              value = dbFieldObserver.getValueAsDouble();
          } else
            wasNull = true;
        } else if ((format instanceof DateFormat) && ((String) value).length()>0)  {
          if (((String) value).length()>0) {
            value = dbFieldObserver.getValueAsDate();
          } else
            wasNull = true;
        }
      }
      try {
        if (wasNull) {
          this.setText("");
        } else
          this.setText(format.format(value));
      } catch(Exception ex) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Can't display the '"+dbFieldObserver.getColumnName()+"' value. ["+ex.getMessage()+"]");
      }
    }
  }

  public void setFormat(Format format) {
    Format oldvalue = this.format;
    this.format = format;
    if (getDataSource()!=null&&getColumnName()!=null) {
      dataSource_fieldValueChanged(new ActiveRowChangeEvent(getDataSource(),getColumnName(), -1));
    } else 
      firePropertyChange("format",oldvalue, format);
  }

  public Format getFormat() {
    return format;
  }
}
