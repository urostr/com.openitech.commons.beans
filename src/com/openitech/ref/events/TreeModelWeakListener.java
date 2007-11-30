package com.openitech.ref.events;

import javax.swing.event.*;
import com.openitech.ref.WeakMethodReference;
import javax.swing.event.TreeModelEvent;
import java.lang.reflect.Method;
import java.lang.reflect.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uroš Trojar s.p.</p>
 * @author Uroš Trojar
 * @version $Revision: 1.1.1.1 $
 */

public class TreeModelWeakListener extends WeakMethodReference<Object> implements TreeModelListener {
  Method treeNodesChangedMethod;
  Method treeNodesInsertedMethod;
  Method treeNodesRemovedMethod;
  Method treeStructureChangedMethod;

  public TreeModelWeakListener(TreeModelListener owner) {
    super(owner);
    Class clazz = owner.getClass();
    try {
      this.treeNodesChangedMethod = clazz.getMethod("treeNodesChanged",
                                                    new Class[] {TreeModelEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.treeNodesChangedMethod = null;
    }
    try {
      this.treeNodesInsertedMethod = clazz.getMethod("treeNodesInserted",
          new Class[] {TreeModelEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.treeNodesInsertedMethod = null;
    }
    try {
      this.treeNodesRemovedMethod = clazz.getMethod("treeNodesRemoved",
                                                    new Class[] {TreeModelEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.treeNodesRemovedMethod = null;
    }
    try {
      this.treeStructureChangedMethod = clazz.getMethod(
          "treeStructureChanged",
          new Class[] {TreeModelEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.treeStructureChangedMethod = null;
    }
  }

  public TreeModelWeakListener(Object owner, String treeNodesChangedMethod, String treeNodesInsertedMethod, String treeNodesRemovedMethod, String treeStructureChangedMethod) throws
      NoSuchMethodException {
    super(owner);
    this.proxy = true;
    Class clazz = owner.getClass();
    this.treeNodesChangedMethod = treeNodesChangedMethod==null?null:clazz.getMethod(treeNodesChangedMethod, new Class[] { TreeModelEvent.class });
    this.treeNodesInsertedMethod = treeNodesInsertedMethod==null?null:clazz.getMethod(treeNodesInsertedMethod, new Class[] { TreeModelEvent.class });
    this.treeNodesRemovedMethod = treeNodesRemovedMethod==null?null:clazz.getMethod(treeNodesRemovedMethod, new Class[] { TreeModelEvent.class });
    this.treeStructureChangedMethod = treeStructureChangedMethod==null?null:clazz.getMethod(treeStructureChangedMethod, new Class[] { TreeModelEvent.class });
  }

  public void treeNodesChanged(TreeModelEvent e) {
    try {
      if (this.isValid() && treeNodesChangedMethod!=null)
        treeNodesChangedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }

  public void treeNodesInserted(TreeModelEvent e) {
    try {
      if (this.isValid() && treeNodesInsertedMethod!=null && this.isEnabled())
        treeNodesInsertedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }

  public void treeNodesRemoved(TreeModelEvent e) {
    try {
      if (this.isValid() && treeNodesRemovedMethod!=null && this.isEnabled())
        treeNodesRemovedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }

  public void treeStructureChanged(TreeModelEvent e) {
    try {
      if (this.isValid() && treeStructureChangedMethod!=null && this.isEnabled())
        treeStructureChangedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      ex.printStackTrace();
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }
}
