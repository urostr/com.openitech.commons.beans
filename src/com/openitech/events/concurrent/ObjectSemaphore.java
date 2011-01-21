/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.events.concurrent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 *
 * @author uros
 */
public class ObjectSemaphore {

  private static Map<Object, Semaphore> semaphores = Collections.synchronizedMap(new HashMap<Object, Semaphore>());

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
      System.out.println("Acquireing: " + semaphore.toString());
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
        System.out.println("Releaseing: " + semaphore.toString());
        semaphore.release();
      }
    }
  }
}
