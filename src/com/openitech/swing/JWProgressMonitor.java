/*
 * JWProgressMonitor.java
 *
 * Created on Nedelja, 11 junij 2006, 11:57
 */

package com.openitech.swing;

import java.awt.EventQueue;

/**
 *
 * @author  uros
 */
public class JWProgressMonitor extends javax.swing.JDialog {
  /** Creates new form JWProgressMonitor */
  public JWProgressMonitor(java.awt.Frame owner) {
    super(owner,true);
    setUndecorated(true);
    initComponents();
    setLocationRelativeTo(owner);
  }
  
  public JWProgressMonitor(java.awt.Dialog owner) {
    super(owner,true);
    setUndecorated(true);
    initComponents();
    setLocationRelativeTo(owner);
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    jPanel1 = new javax.swing.JPanel();
    jlTitle = new javax.swing.JLabel();
    jlPage = new javax.swing.JLabel();
    jProgressBar = new javax.swing.JProgressBar();

    setAlwaysOnTop(true);
    setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

    jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jPanel1.setLayout(new java.awt.GridBagLayout());

    jlTitle.setText("Stran");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
    jPanel1.add(jlTitle, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.ipadx = 27;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
    jPanel1.add(jlPage, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(jProgressBar, gridBagConstraints);

    getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

    pack();
  }// </editor-fold>//GEN-END:initComponents
  
  int progress = 0;
  
  public void setMin(int min) {
    jProgressBar.setMinimum(min);
  }

  public int getMin() {
    return jProgressBar.getMinimum();
  }

  public void setMax(int max) {
    if (max==0)
      jlPage.setText("");
    jProgressBar.setMaximum(max);
  }

  public int getMax() {
    return jProgressBar.getMaximum();
  }

  public int getProgress() {
    return jProgressBar.getValue();
  }
  
  public void setProgress(int progress) {
    this.progress = progress;
    jlPage.setText(jProgressBar.getMaximum()<1?"":Integer.toString(progress)+"/"+Integer.toString(jProgressBar.getMaximum()));
    jProgressBar.setValue(progress);
    this.repaint();
  }
  
  public void next() {
    setProgress(++progress);
    if ((progress % 100)==0) {
      pack();
    }
  }

  @Override
  public void setTitle(String title) {
    jlTitle.setText(title);
    pack();
  }
  
  @Override
  public String getTitle() {
    return jlTitle.getText();
  }

  @Override
  public void setVisible(final boolean b) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        setModal(b);
        inheritedVisible(b);
      }
    });
  }

  private final void inheritedVisible(boolean b) {
    super.setVisible(b);
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel jPanel1;
  private javax.swing.JProgressBar jProgressBar;
  private javax.swing.JLabel jlPage;
  private javax.swing.JLabel jlTitle;
  // End of variables declaration//GEN-END:variables
  
}