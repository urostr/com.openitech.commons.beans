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
 * ActiveRowChangeConcurrentListener.java
 *
 * Created on Ponedeljek, 17 julij 2006, 20:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.events;

import com.openitech.events.concurrent.ConcurrentEvent;

/**
 *
 * @author uros
 */
public class ActiveRowChangeConcurrentListener extends ActiveRowChangeWeakListener implements ConcurrentEvent {
  public ActiveRowChangeConcurrentListener(ActiveRowChangeListener owner) {
    super(owner);
  }
  public ActiveRowChangeConcurrentListener(Object owner, String fieldValueChangedMethod, String activeRowChangedMethod) throws
      NoSuchMethodException {
    super(owner,fieldValueChangedMethod,activeRowChangedMethod);
  }
}
