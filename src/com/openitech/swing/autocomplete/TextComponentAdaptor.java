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
 * $Id: TextComponentAdaptor.java,v 1.1 2008/08/29 12:44:13 uros Exp $
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
package com.openitech.swing.autocomplete;

import java.util.List;
import javax.swing.text.JTextComponent;

/**
 * An implementation of the AbstractAutoCompleteAdaptor that is suitable for a
 * JTextComponent.
 * 
 * @author Thomas Bierhance
 */
public class TextComponentAdaptor extends AbstractAutoCompleteAdaptor {
    
    /** a <tt>List</tt> containing the strings to be used for automatic
     * completion */
    List<?> items;
    /** the text component that is used for automatic completion*/
    JTextComponent textComponent;
    /** the item that is currently selected */
    Object selectedItem;
    
    /**
     * Creates a new <tt>TextComponentAdaptor</tt> for the given list and text
     * component.
     * 
     * @param items a <tt>List</tt> that contains the items that are used for
     * automatic completion
     * @param textComponent the text component that will be used automatic
     * completion
     */
    public TextComponentAdaptor(JTextComponent textComponent, List<?> items) {
        this.items = items;
        this.textComponent = textComponent;
    }
    
    public Object getSelectedItem() {
        return selectedItem;
    }
    
    public int getItemCount() {
        return items.size();
    }
    
    public Object getItem(int index) {
        return items.get(index);
    }
    
    public void setSelectedItem(Object item) {
        selectedItem = item;
    }
    
    public JTextComponent getTextComponent() {
        return textComponent;
    }
}
