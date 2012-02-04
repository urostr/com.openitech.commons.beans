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
package com.openitech.events.concurrent;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Semaphore;

/**
 *
 * @author uros
 */
public class ObjectSemaphore {

  private static Map<Object, Semaphore> semaphores = Collections.synchronizedMap(new WeakHashMap<Object, Semaphore>());

  private ObjectSemaphore() {
  }

  public static void aquire(Object... objects) throws InterruptedException {
    Semaphore[] s = new Semaphore[objects.length];
    for (int i = 0; i < objects.length; i++) {
      if (!semaphores.containsKey(objects[i])) {
        s[i] = new Semaphore(1);
        semaphores.put(objects[i], s[i]);
      } else {
        s[i] = semaphores.get(objects[i]);
      }
    }

    for (Semaphore semaphore : s) {
      semaphore.acquire();
    }
  }

  public static void release(Object... objects) {
    Semaphore[] s = new Semaphore[objects.length];
    for (int i = 0; i < objects.length; i++) {
      if (semaphores.containsKey(objects[i])) {
        s[i] = semaphores.get(objects[i]);
      }
    }

    for (Semaphore semaphore : s) {
      if (semaphore != null) {
        semaphore.release();
      }
    }
  }
}
