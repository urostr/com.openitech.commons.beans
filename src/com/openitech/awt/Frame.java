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
 * java.awt.Frame.java
 *
 * Created on Sreda, 27 februar 2008, 9:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.awt;

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
