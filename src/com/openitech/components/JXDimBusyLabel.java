/*
 * JXDimBusyLabel.java
 *
 * Created on Sobota, 7 april 2007, 19:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.BusyPainter;
import org.jdesktop.swingx.painter.PainterIcon;


/**
 *
 * @author uros
 */
public class JXDimBusyLabel extends JXLabel {
  private BusyPainter busyPainter;
  private Timer busy;

  /** Creates a new instance of JXDimBusyLabel */
  public JXDimBusyLabel() {
        busyPainter = new BusyPainter();
        busyPainter.setBaseColor(Color.LIGHT_GRAY);
        busyPainter.setHighlightColor(getForeground());
        Dimension dim = new Dimension(getBusySize(),getBusySize());
        PainterIcon icon = new PainterIcon(dim);
        icon.setPainter(busyPainter);
        this.setIcon(icon);
  }
    
    /**
     * <p>Gets whether this <code>JXBusyLabel</code> is busy. If busy, then
     * the <code>JXBusyLabel</code> instance will indicate that it is busy,
     * generally by animating some state.</p>
     * 
     * @return true if this instance is busy
     */
    public boolean isBusy() {
        return busy != null;
    }

    /**
     * <p>Sets whether this <code>JXBusyLabel</code> instance should consider
     * itself busy. A busy component may indicate that it is busy via animation,
     * or some other means.</p>
     *
     * @param busy whether this <code>JXBusyLabel</code> instance should
     *        consider itself busy
     */
    public void setBusy(boolean busy) {
        boolean old = isBusy();
        if (!old && busy) {
            startAnimation();
            firePropertyChange("busy", old, isBusy());
        } else if (old && !busy) {
            stopAnimation();
            firePropertyChange("busy", old, isBusy());
        }
    }
    
    private void startAnimation() {
        if(busy != null) {
            stopAnimation();
        }
        
        busy = new Timer(100, new ActionListener() {
            int frame = 8;
            public void actionPerformed(ActionEvent e) {
                frame = (frame+1)%8;
                busyPainter.setFrame(frame);
                repaint();
            }
        });
        busy.start();
    }
    
    private void stopAnimation() {
        busy.stop();
        busyPainter.setFrame(-1);
        repaint();
        busy = null;
    }

  /**
   * Holds value of property busySize.
   */
  private int busySize = 26;

  /**
   * Getter for property busySize.
   * @return Value of property busySize.
   */
  public int getBusySize() {
    return this.busySize;
  }

  /**
   * Setter for property busySize.
   * @param busySize New value of property busySize.
   */
  public void setBusySize(int busySize) {
    if (this.busySize!=busySize) {
      int oldValue = this.busySize;
      this.busySize = busySize;
      
      busyPainter.setBarLength(busySize*8/26);
      busyPainter.setBarWidth(busySize*4/26);
      busyPainter.setTrailLength(busySize*4/26);
      busyPainter.setCenterDistance(busySize*5/26);
      
      Dimension dim = new Dimension(getBusySize(),getBusySize());
      PainterIcon icon = new PainterIcon(dim);
      icon.setPainter(busyPainter);
      this.setIcon(icon);
      firePropertyChange("busySize", oldValue, busySize);
    }
  }
  
}
