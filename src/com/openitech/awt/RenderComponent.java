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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.awt;

/**
 *
 * @author uros
 */


import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class RenderComponent {
    public static void main(String[] args) throws Exception {
       JDialog dialog = new JDialog();

        dialog.add(new JButton("Test Button"));

        BufferedImage image =
                convertToImage(dialog,new java.awt.Dimension(400,300));

        ImageIO.write( image, "PNG", new java.io.File( "test.png" ) );

        System.exit(0);
    }
    /**Paints the current visual state of the component at the time of this
     * call to a BufferedImage (which can then be written to a file through
     * the  <code>javax.imageio.ImageIO.write()</code> methods).
     *
     * Note that if the component extends Window and is not not visible
     * the window decorations can not be painted.  Only the root pane
     * (if it is a Swing window) can be painted.  Passing a non-visible
     * AWT window as the parameter will only produce a blank image.
     * Furthermore if the component passed is a non-displayable window,
     * it will be made displayable by calling pack().
     *
     * @param c The component to be painted
     * @param size In the event that c is an invalid component, it will be
                   validated at the given size or if null then at its
     *             preferred size.
     */
    public static BufferedImage convertToImage(java.awt.Component c,
            java.awt.Dimension size)
    {
          if(!c.isValid()) {
            if (c instanceof java.awt.Window) {
                if (!c.isDisplayable()) {
                    ((java.awt.Window) c).pack();
                }
                if(size != null) {
                    c.setSize(size);
                    c.validate();
                }
            }else {
                c.setSize(size==null?c.getPreferredSize():size);
                c.doLayout();
            }
        }
        if (!c.isVisible() && c instanceof java.awt.Window) {
            if (c instanceof JFrame) {
                c = ((JFrame) c).getRootPane();
            } else if (c instanceof JDialog) {
                c = ((JDialog) c).getRootPane();
            } else if (c instanceof JApplet) {
                c = ((JApplet) c).getRootPane();
            } else if (c instanceof JWindow) {
                c = ((JWindow) c).getRootPane();
            }
        }

        int type;
        if(c instanceof java.awt.Window) {
            type = BufferedImage.TYPE_4BYTE_ABGR;
        }else {
            type = c.isOpaque()?BufferedImage.TYPE_3BYTE_BGR:
                                BufferedImage.TYPE_4BYTE_ABGR;
        }

        BufferedImage componentView =
                new BufferedImage(c.getWidth(),c.getHeight(),type);
        java.awt.Graphics g = componentView.getGraphics();

        if(c instanceof java.awt.Window)
            c.printAll(g);
        else
            c.paint(g);

        g.dispose();
        return componentView;
    }
}
