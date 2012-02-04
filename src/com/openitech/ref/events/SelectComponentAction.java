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


package com.openitech.ref.events;

import javax.swing.JTabbedPane;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.awt.event.ActionListener;
import com.openitech.ref.WeakMethodReference;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uroš Trojar s.p.</p>
 * @author Uroš Trojar
 * @version $Revision: 1.1.1.1 $
 */

public class SelectComponentAction extends WeakMethodReference<JTabbedPane> implements ActionListener {
  WeakReference component;

  public SelectComponentAction(JTabbedPane owner, Component component) throws
      NoSuchMethodException  {
    super(owner);
    this.component = new WeakReference<Component>(component);
  }
  public void actionPerformed(ActionEvent e) {
    if (isValid() && component.get()!=null && this.isEnabled())
      ((JTabbedPane) this.get()).setSelectedComponent((Component) component.get());
  }
}
