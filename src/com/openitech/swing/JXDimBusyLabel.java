/*
 * JXDimBusyLabel.java
 *
 * Created on Sobota, 7 april 2007, 19:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.swing;

import com.openitech.Settings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.color.ColorUtil;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.PainterIcon;


/**
 *
 * @author uros
 */
public class JXDimBusyLabel extends JXLabel {
  private SimpleBusyPainter busyPainter;
  private Timer busy;
  private final ActionListener l = new ActionListener() {
      int frame = 8;
      public void actionPerformed(ActionEvent e) {
        frame = (frame+1)%8;
        busyPainter.setFrame(frame);
        repaint();
      }
    };
  
  /** Creates a new instance of JXDimBusyLabel */
  public JXDimBusyLabel() {
    busyPainter = new SimpleBusyPainter();
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
  public void setBusy(final boolean busy) {
    if (EventQueue.isDispatchThread()) {
      boolean old = isBusy();
    
      if (busy)
        startAnimation();
      else
        stopAnimation();

      if (old!=busy) {
        firePropertyChange("busy", old, isBusy());
      }
    } else {
      try {
        EventQueue.invokeAndWait(new Runnable() {
          public void run() {
            setBusy(busy);
          }
        });
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.WARNING,"couldn't set the busy label.", ex);
      }
    }
  }
  
  private void startAnimation() {
    if(busy != null) {
      stopAnimation();
    }
    
    busy = new Timer(100, l);
    busy.start();
  }
  
  private void stopAnimation() {
    if (busy!=null) {
      busy.stop();
      busy.removeActionListener(l);
      busyPainter.setFrame(-1);
      repaint();
    }
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
  
  /**
   * A specific painter that paints an "infinite progress" like animation.
   *
   */
  public static class SimpleBusyPainter<T> extends AbstractPainter<T> {
    private int frame = -1;
    private boolean skewed = false;
    private int points = 8;
    private float barWidth = 4;
    private float barLength = 8;
    private float centerDistance = 5;
    
    private Color baseColor = new Color(200,200,200);
    private Color highlightColor = Color.BLACK;
    private int trailLength = 4;
    
    /**
     * @inheritDoc
     */
    @Override
    protected void doPaint(Graphics2D g, T t, int width, int height) {
      RoundRectangle2D rect = new RoundRectangle2D.Float(getCenterDistance(), -getBarWidth()/2,
              getBarLength(), getBarWidth(),
              getBarWidth(), getBarWidth());
      if(skewed) {
        rect = new RoundRectangle2D.Float(5,getBarWidth()/2,8, getBarWidth(),
                getBarWidth(), getBarWidth());
      }
      g.setColor(Color.GRAY);
      
      g.translate(width/2,height/2);
      for(int i=0; i<getPoints(); i++) {
        g.setColor(calcFrameColor(i));
        g.fill(rect);
        g.rotate(Math.PI*2.0/(double)getPoints());
      }
    }
    
    
    private Color calcFrameColor(final int i) {
      if(frame == -1) {
        return getBaseColor();
      }
      
      for(int t=0; t<getTrailLength(); t++) {
        if(i == (frame-t+getPoints())%getPoints()) {
          float terp = 1-((float)(getTrailLength()-t))/(float)getTrailLength();
          return ColorUtil.interpolate(
                  getBaseColor(),
                  getHighlightColor(), terp);
        }
      }
      return getBaseColor();
    }
    
    public int getFrame() {
      return frame;
    }
    
    public void setFrame(int frame) {
      this.frame = frame;
    }
    
    public Color getBaseColor() {
      return baseColor;
    }
    
    public void setBaseColor(Color baseColor) {
      Color old = getBaseColor();
      this.baseColor = baseColor;
      setDirty(true);
      firePropertyChange("baseColor", old, getBaseColor());
    }
    
    public Color getHighlightColor() {
      return highlightColor;
    }
    
    public void setHighlightColor(Color highlightColor) {
      Color old = getHighlightColor();
      this.highlightColor = highlightColor;
      setDirty(true);
      firePropertyChange("highlightColor", old, getHighlightColor());
    }
    
    public float getBarWidth() {
      return barWidth;
    }
    
    public void setBarWidth(float barWidth) {
      float old = getBarWidth();
      this.barWidth = barWidth;
      setDirty(true);
      firePropertyChange("barWidth", old, getBarWidth());
    }
    
    public float getBarLength() {
      return barLength;
    }
    
    public void setBarLength(float barLength) {
      float old = getBarLength();
      this.barLength = barLength;
      setDirty(true);
      firePropertyChange("barLength", old, getBarLength());
    }
    
    public float getCenterDistance() {
      return centerDistance;
    }
    
    public void setCenterDistance(float centerDistance) {
      float old = getCenterDistance();
      this.centerDistance = centerDistance;
      setDirty(true);
      firePropertyChange("centerDistance", old, getCenterDistance());
    }
    
    public int getPoints() {
      return points;
    }
    
    public void setPoints(int points) {
      int old = getPoints();
      this.points = points;
      setDirty(true);
      firePropertyChange("points", old, getPoints());
    }
    
    public int getTrailLength() {
      return trailLength;
    }
    
    public void setTrailLength(int trailLength) {
      int old = getTrailLength();
      this.trailLength = trailLength;
      setDirty(true);
      firePropertyChange("trailLength", old, getTrailLength());
    }
    
  }
}
