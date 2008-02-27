/*
 * java.awt.Frame.java
 *
 * Created on Sreda, 27 februar 2008, 9:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.util;

/**
 *
 * @author uros
 */
public class Frame {
  
  /** Creates a new instance of Frame */
  private Frame() {
  }
  
// This method iconifies a frame; the maximized bits are not affected.
    public static void iconify(java.awt.Frame frame) {
        int state = frame.getExtendedState();
    
        // Set the iconified bit
        state |= java.awt.Frame.ICONIFIED;
    
        // Iconify the frame
        frame.setExtendedState(state);
    }
    
    // This method deiconifies a frame; the maximized bits are not affected.
    public static void deiconify(java.awt.Frame frame) {
        int state = frame.getExtendedState();
    
        // Clear the iconified bit
        state &= ~java.awt.Frame.ICONIFIED;
    
        // Deiconify the frame
        frame.setExtendedState(state);
    }
    
    // This method minimizes a frame; the iconified bit is not affected
    public static void minimize(java.awt.Frame frame) {
        int state = frame.getExtendedState();
    
        // Clear the maximized bits
        state &= ~java.awt.Frame.MAXIMIZED_BOTH;
    
        // Maximize the frame
        frame.setExtendedState(state);
    }
    
    // This method minimizes a frame; the iconified bit is not affected
    public static void maximize(java.awt.Frame frame) {
        int state = frame.getExtendedState();
    
        // Set the maximized bits
        state |= java.awt.Frame.MAXIMIZED_BOTH;
    
        // Maximize the frame
        frame.setExtendedState(state);
    }
}
