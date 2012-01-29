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
 * SpringContextManager.java
 *
 * Created on Torek, 10 april 2007, 9:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.spring;

import com.openitech.auth.LoginContextManager;
import com.openitech.auth.jaas.callback.LocalAbortCallback;
import com.openitech.auth.jaas.callback.LocalCommitCallback;
import com.openitech.auth.jaas.callback.LocalLogoutCallback;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.springframework.context.ConfigurableApplicationContext;


/**
 *
 * @author uros
 */
public class SpringContextManager {
  private static final Logger LOG = Logger.getLogger(SpringContextManager.class.getCanonicalName());
  private static SpringContextManager instance = null;
  
  
  /** Creates a new instance of SpringContextManager */
  private SpringContextManager() {
    LoginContextManager lm = LoginContextManager.getInstance();
    LocalLoginCallbackHandler handler = new LocalLoginCallbackHandler();

    lm.registerHandler(LocalCommitCallback.class, handler);
    lm.registerHandler(LocalAbortCallback.class, handler);
    lm.registerHandler(LocalLogoutCallback.class, handler);
    
    Runtime.getRuntime().addShutdownHook(new ShutdownHook());
  }
  
  public static SpringContextManager getInstance() {
    if (instance==null) {
      instance = new SpringContextManager();
    }
    return instance;
  }
  
  private void startContext(String username) {
      if (contexts.containsKey(username)) {
        ConfigurableApplicationContext userContext = contexts.get(username);
        if (!((context==null) || (context==userContext))) {
          close(context);
          context = userContext;
        }
      }
      if (context==null) {
        context = new SpringApplicationContext();
        context.refresh();
        contexts.put(username, context);
      } else if (!context.isActive()||!context.isRunning()) {
        context.start();
      }
  }
  
  private void stopContext() {
    if ((context!=null)&&context.isRunning()) {
      close(context);
      context = null;
    }
  }
  
  private void close(ConfigurableApplicationContext context) {
    context.stop();
    context.close();
  }
  
  private Map<String,ConfigurableApplicationContext> contexts = Collections.synchronizedMap(new HashMap<String,ConfigurableApplicationContext>());
  
  /**
   * Holds value of property context.
   */
  private ConfigurableApplicationContext context = null;
  
  /**
   * Getter for property context.
   * @return Value of property context.
   */
  public ConfigurableApplicationContext getContext() {
    if ((this.context==null)&&(LoginContextManager.getInstance().isLoggedOn())) {
      startContext(LoginContextManager.getInstance().getName());
    }
    return this.context;
  }
  
  private class LocalLoginCallbackHandler implements CallbackHandler {
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
      for (Callback callback:callbacks) {
        if (callback instanceof LocalCommitCallback) {
          SpringContextManager.getInstance().startContext(((LocalCommitCallback) callback).getUsername());
        } else if ((callback instanceof LocalAbortCallback)||
                (callback instanceof LocalLogoutCallback)) {
          SpringContextManager.getInstance().stopContext();
        }
      }
    }
  }
  
  private class ShutdownHook extends Thread {
    public ShutdownHook() {
      super("spring-shutdown-hook");
    }

    /**
     * If this thread was constructed using a separate 
     * <code>Runnable</code> run object, then that 
     * <code>Runnable</code> object's <code>run</code> method is called; 
     * otherwise, this method does nothing and returns. 
     * <p>
     * Subclasses of <code>Thread</code> should override this method. 
     * 
     * 
     * 
     * @see java.lang.Thread#startContext()
     * @see java.lang.Thread#stop()
     * @see java.lang.Thread#Thread(java.lang.ThreadGroup, 
     *          java.lang.Runnable, java.lang.String)
     * @see java.lang.Runnable#run()
     */
    public void run() {
      stopContext();
    }
  }
}
