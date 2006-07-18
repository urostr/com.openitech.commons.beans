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
 * @version $Revision: 1.1 $
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
