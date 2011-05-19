/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.components;

/**
 *
 * @author domenbasic
 */
/*
 * $Id: JXCustomImagePanel.java,v 1.14 2006/07/11 17:11:35 rbair Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
import com.openitech.Settings;
import com.openitech.awt.Desktop;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.db.model.FieldObserver;
import com.openitech.ref.events.PropertyChangeWeakListener;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.swingx.JXPanel;

/**
 * <p>A panel that draws an image. The standard (and currently only supported)
 * mode is to draw the specified image starting at position 0,0 in the
 * panel. The component&amp;s preferred size is based on the image, unless
 * explicitly set by the user.</p>
 *
 * <p>In the future, the JXCustomImagePanel will also support tiling of images,
 * scaling, resizing, cropping, segways etc.</p>
 *
 * <p>This component also supports allowing the user to set the image. If the
 * <code>JXCustomImagePanel</code> is editable, then when the user clicks on the
 * <code>JXCustomImagePanel</code> a FileChooser is shown allowing the user to pick
 * some other image to use within the <code>JXCustomImagePanel</code>.</p>
 *
 * <p>Images to be displayed can be set based on URL, Image, etc.
 *
 * @author rbair
 */
public class JDbJXImagePanel extends JXPanel implements PropertyChangeListener, FieldObserver {

  private DbFieldObserver dbFieldObserver = new DbFieldObserver();
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient PropertyChangeWeakListener imagePropertyChangeWeakListener;
  private Image defaultImage;
  private static final Logger LOG = Logger.getLogger(JDbJXImagePanel.class.getName());
  /**
   * Text informing the user that clicking on this component will allow them to set the image
   */
  private static final String TEXT = java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("CLICK_HERE_TO_SET_THE_IMAGE");
  /**
   * The image to draw
   */
  private Image img = null;
  /**
   * If true, then the image can be changed. Perhaps a better name is
   * &quot;readOnly&quot;, but editable was chosen to be more consistent
   * with other Swing components.
   */
  private boolean editable = true;
  /**
   * The mouse handler that is used if the component is editable
   */
  private MouseHandler mhandler = new MouseHandler();
  /**
   * Specifies how to draw the image, i.e. what kind of Style to use
   * when drawing
   */
  private Style style = Style.SCALED_KEEP_ASPECT_RATIO;
  private boolean updating = false;

  public JDbJXImagePanel() {
    setLayout(new BorderLayout());
    try {
      defaultImage = ImageIO.read(getClass().getResource("/com/openitech/icons/no_image.gif")); //NOI18N

    } catch (Exception ex) {
      Logger.getLogger(JDbJXImagePanel.class.getName()).log(Level.SEVERE, null, ex);
    }

    //addControl();
    addListeners();

  }

  private void addListeners() {
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this, "dataSource_fieldValueChanged", null); //NOI18N
      imagePropertyChangeWeakListener = new PropertyChangeWeakListener(this, "propertyChange"); //NOI18N
      addPropertyChangeListener("image", imagePropertyChangeWeakListener); //NOI18N
    } catch (NoSuchMethodException ex) {
      throw (RuntimeException) new IllegalStateException().initCause(ex);
    }
    dbFieldObserver.addActiveRowChangeListener(activeRowChangeWeakListener);


    addMouseListener(mhandler);
    invalidate();
    validate();
  }
  private JPanel jpControl = null;

  protected JPanel getControl() {
    if (jpControl == null) {
      JPanel jpHolding = new JPanel(new FlowLayout());
      jpHolding.add(getRemoveImageButton());
      jpHolding.add(getAddImageButton());
      jpHolding.add(getOpenExternalButton());
      jpControl = new JPanel(new GridBagLayout());
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
      gridBagConstraints.anchor = GridBagConstraints.WEST;
      gridBagConstraints.weightx = 1;
      jpControl.add(jpHolding, gridBagConstraints);
    }
    return jpControl;
  }
  private volatile Boolean addedControl = Boolean.FALSE;

  private void addControl() {
    synchronized (addedControl) {
      if (!addedControl) {
        add(getControl(), BorderLayout.NORTH);
        EventQueue.invokeLater(repaint);
        addedControl = Boolean.TRUE;
      }
    }
  }

  private void removeControl() {
    synchronized (addedControl) {
      if (addedControl) {
        remove(getControl());
        EventQueue.invokeLater(repaint);
        addedControl = Boolean.FALSE;
      }
    }
  }
  private JButton jbRemoveImage = null;

  protected JButton getRemoveImageButton() {
    if (jbRemoveImage == null) {
      jbRemoveImage = new JButton(java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("REMOVE_IMAGE"));
      jbRemoveImage.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          if (img != null) {
            setImage((BufferedImage) null);
          }
        }
      });
    }
    return jbRemoveImage;
  }
  private JButton jbAddImage = null;

  protected JButton getAddImageButton() {
    if (jbAddImage == null) {
      jbAddImage = new JButton(java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("LOAD_IMAGE"));
      jbAddImage.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          showImageChooser();
        }
      });
    }
    return jbAddImage;
  }
  private JButton jbOpenImage = null;

  protected JButton getOpenExternalButton() {
    if (jbOpenImage == null) {
      jbOpenImage = new JButton(java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("OPEN_IMAGE"));
      jbOpenImage.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            if (img != null) {
              String filename = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "Image " + System.currentTimeMillis() + ".jpg"; //NOI18N
              java.io.File tempFile = new java.io.File(filename);
              if (tempFile.exists()) {
                tempFile.delete();
              }
              tempFile.createNewFile();

              ImageIO.write((BufferedImage) img, "jpg", tempFile); //NOI18N
              Desktop.open(tempFile);
            }
          } catch (IOException ex) {
            Logger.getLogger(JDbJXImagePanel.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      });
    }
    return jbOpenImage;
  }

  @Override
  public void setDataSource(DbDataSource dataSource) {
    dbFieldObserver.setDataSource(dataSource);
  }

  public DbFieldObserver getDbFieldObserver() {
    return dbFieldObserver;
  }

  @Override
  public DbDataSource getDataSource() {
    return dbFieldObserver.getDataSource();
  }

  @Override
  public void setColumnName(String columnName) {
    dbFieldObserver.setColumnName(columnName);
  }

  @Override
  public String getColumnName() {
    return dbFieldObserver.getColumnName();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("image")) { //NOI18N
      updateColumn();
    }
  }

  public void dataSource_fieldValueChanged(ActiveRowChangeEvent event) throws SQLException {
    updating = true;
    try {
      byte[] byteArray = dbFieldObserver.getValueAsByteArray();
      BufferedInputStream is = new BufferedInputStream(new ByteArrayInputStream(byteArray));
      try {
        setImage(ImageIO.read(is));

      } catch (IOException ex) {
        Logger.getLogger(JDbJXImagePanel.class.getName()).log(Level.SEVERE, null, ex);
      }
    } finally {
      updating = false;
    }
  }

  private void updateColumn() {
    if (!updating) {
      activeRowChangeWeakListener.setEnabled(false);
      try {
        if (img != null) {
          ByteArrayOutputStream baos = new ByteArrayOutputStream(2000);
          BufferedImage bufferedImage = (BufferedImage) this.img;
          ImageIO.write(bufferedImage, "jpg", baos); //NOI18N
          baos.flush();
          byte[] byteArray = baos.toByteArray();
          dbFieldObserver.updateValue(byteArray);

        } else {
          dbFieldObserver.updateValue(new byte[]{});
        }
      } catch (Exception ex) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't update the value in the dataSource.", ex); //NOI18N
      } finally {
        activeRowChangeWeakListener.setEnabled(true);
      }
    }
  }

  private void setImage(BufferedImage image) {
    if (image != img) {
      Image oldImage = img;
      img = image;
      try {
        firePropertyChange("image", oldImage, img); //NOI18N
      } finally {
        EventQueue.invokeLater(repaint);
      }
    }
  }

  /**
   * Sets the image to use for the background of this panel. This image is
   * painted whether the panel is opaque or translucent.
   * @param File if null, clears the image. Otherwise, this will set the
   * image to be painted. If the preferred size has not been explicitly set,
   * then the image dimensions will alter the preferred size of the panel.
   */
  public void setImage(File file) throws IOException {
    BufferedImage bufferedImage = ImageIO.read(file);
//    Image image = Toolkit.getDefaultToolkit().getImage(file.toString());
    setImage(bufferedImage);
  }

  /**
   * @return the image used for painting the background of this panel
   */
  public Image getImage() {
    return img;
  }

  /**
   * @param editable
   */
  public void setEditable(boolean editable) {
    if (editable != this.editable) {
      this.editable = editable;
      setToolTipText(editable ? TEXT : ""); //NOI18N
      firePropertyChange("editable", !editable, editable); //NOI18N
      repaint();
    }
  }

  /**
   * @return whether the image for this panel can be changed or not via
   * the UI. setImage may still be called, even if <code>isEditable</code>
   * returns false.
   */
  public boolean isEditable() {
    return editable;
  }

  /**
   * Sets what style to use when painting the image
   *
   * @param s
   */
  public void setStyle(Style s) {
    if (style != s) {
      Style oldStyle = style;
      style = s;
      firePropertyChange("style", oldStyle, s); //NOI18N
      repaint();
    }
  }

  /**
   * @return the Style used for drawing the image (CENTERED, TILED, etc).
   */
  public Style getStyle() {
    return style;
  }

  @Override
  public Dimension getMaximumSize() {
    return new Dimension(600, 400);
  }

  @Override
  public Dimension getMinimumSize() {
    final Image temp = img == null ? defaultImage : img;
    if (temp != null) {
      //it has not been explicitly set, so return the width/height of the image
      int width = temp.getWidth(null);
      int height = temp.getHeight(null);
      if (width == -1 || height == -1) {
        return super.getMinimumSize();
      }

      Dimension maximumSize = getMaximumSize();
      if (width < maximumSize.width && height < maximumSize.height) {
        return new Dimension(width, height);
      } else {
        return maximumSize;
      }

    } else {
      return super.getMinimumSize();
    }
  }

  @Override
  public int getHeight() {
    return getPreferredSize().height;
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension maximumSize = getMaximumSize();
    Dimension minimumSize = getMinimumSize();
    if (minimumSize.width < maximumSize.width && minimumSize.height < maximumSize.height) {
      return getMinimumSize();
    } else {
      return getMaximumSize();
    }
//    if (preferredSize == null && img != null) {
//      //it has not been explicitly set, so return the width/height of the image
//      int width = img.getWidth(null);
//      int height = img.getHeight(null);
//      if (width == -1 || height == -1) {
//        return super.getPreferredSize();
//      }
//      return new Dimension(width, height);
//    } else {
//      return super.getPreferredSize();
//    }
  }
  private final Runnable repaint = new Runnable() {

    @Override
    public void run() {
      boolean ready = true;
      if (img != null) {
        final int imgWidth = img.getWidth(null);
        final int imgHeight = img.getHeight(null);
        ready = (imgWidth != -1 && imgHeight != -1);
      }

      if (ready) {
        final Container owner = JDbJXImagePanel.this.getParent()==null?JDbJXImagePanel.this:JDbJXImagePanel.this.getParent();
        owner.invalidate();
        owner.validate();
        owner.repaint(10);
      } else {
        EventQueue.invokeLater(repaint);
      }
    }
  };

  /**
   * Overriden to paint the image on the panel
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    Image tempImage = img == null ? defaultImage : img;

    if (tempImage != null) {
      final int tempImageWidth = tempImage.getWidth(null);
      final int tempImageHeight = tempImage.getHeight(null);
      if (tempImageWidth == -1 || tempImageHeight == -1) {
        //image hasn't completed loading, return
        return;
      }

      Insets insets = getInsets();
      final int pw = getWidth() - insets.left - insets.right;
      final int ph = getHeight() - insets.top - insets.bottom;

      switch (style) {
        case CENTERED:
          Rectangle clipRect = g2.getClipBounds();
          int imageX = (pw - tempImageWidth) / 2 + insets.left;
          int imageY = (ph - tempImageHeight) / 2 + insets.top;
          Rectangle r = SwingUtilities.computeIntersection(
                  imageX, imageY, tempImageWidth, tempImageHeight, clipRect);
          if (r.x == 0 && r.y == 0 && (r.width == 0 || r.height == 0)) {
            return;
          }
          //I have my new clipping rectangle "r" in clipRect space.
          //It is therefore the new clipRect.
          clipRect = r;
          //since I have the intersection, all I need to do is adjust the
          //x & y values for the image
          int txClipX = clipRect.x - imageX;
          int txClipY = clipRect.y - imageY;
          int txClipW = clipRect.width;
          int txClipH = clipRect.height;

          g2.drawImage(tempImage, clipRect.x, clipRect.y, clipRect.x + clipRect.width, clipRect.y + clipRect.height,
                  txClipX, txClipY, txClipX + txClipW, txClipY + txClipH, null);
          break;
        case TILED:
        case SCALED:
          g2.drawImage(tempImage, insets.left, insets.top, pw,
                  ph, null);
          break;
        case SCALED_KEEP_ASPECT_RATIO:
          int w;
          int h;
          if ((tempImageWidth - pw) > (tempImageHeight - ph)) {
            w = pw;
            final float ratio = ((float) w) / ((float) tempImageWidth);
            h = (int) (tempImageHeight * ratio);
          } else {
            h = ph;
            final float ratio = ((float) h) / ((float) tempImageHeight);
            w = (int) (tempImageWidth * ratio);
          }
          final int x = (pw - w) / 2 + insets.left;
          final int y = (ph - h) / 2 + insets.top;
          g2.drawImage(tempImage, x, y, w, h, null);
          break;
        case FIT_TO_SCREEN:
          //Image tempImageage = tempImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
          g2.drawImage(tempImage, 0, 0, tempImage.getWidth(this), tempImage.getHeight(this), null);
          break;
        default:
          LOG.fine("unimplemented"); //NOI18N
          g2.drawImage(tempImage, insets.left, insets.top, this);
          break;
      }
    }
  }
  private JFileChooser chooser;

  protected void showImageChooser() {
    if (isEditable()) {
      if (chooser == null) {
        chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new FileFilter() {

          @Override
          public boolean accept(File f) {
            return f.isDirectory()
                    || f.getName().toLowerCase().endsWith(".jpg") //NOI18N
                    || f.getName().toLowerCase().endsWith(".jpeg") //NOI18N
                    || f.getName().toLowerCase().endsWith(".gif") //NOI18N
                    || f.getName().toLowerCase().endsWith(".png") //NOI18N
                    || f.getName().toLowerCase().endsWith(".tif"); //NOI18N
          }

          @Override
          public String getDescription() {
            return java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("IMAGE FILES");
          }
        });
      }
      int retVal = chooser.showOpenDialog(JDbJXImagePanel.this);
      if (retVal == JFileChooser.APPROVE_OPTION) {
        try {
          setImage(chooser.getSelectedFile());
        } catch (Exception ex) {
          Logger.getLogger(JDbJXImagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
  }
  Timer timer = null;

  /**
   * Handles click events on the component
   */
  private class MouseHandler extends MouseAdapter {

    @Override
    public void mouseEntered(MouseEvent evt) {
      Cursor hourglassCursor = new Cursor(Cursor.HAND_CURSOR);
      setCursor(hourglassCursor);
      addControl();
      if (timer != null) {
        timer.cancel();
      }
      (timer = new Timer()).schedule(new TimerTask() {

        @Override
        public void run() {
          removeControl();
        }
      }, 3000);
    }

    @Override
    public void mouseExited(MouseEvent evt) {
      Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
      setCursor(normalCursor);
    }
  }

  public static enum Style {

    CENTERED, TILED, SCALED, SCALED_KEEP_ASPECT_RATIO, FIT_TO_SCREEN
  };
}
