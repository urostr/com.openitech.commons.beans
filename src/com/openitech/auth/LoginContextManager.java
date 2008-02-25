/*
 * LoginContextManager.java
 *
 * Created on Ponedeljek, 9 april 2007, 16:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.auth;

import com.openitech.util.Equals;
import com.openitech.util.PropertyChanges;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.jdesktop.swingx.JXLoginPanel;
import org.jdesktop.swingx.auth.LoginService;
import com.openitech.auth.jaas.LocalGroupPrincipal;
import com.openitech.auth.jaas.callback.LocalCommitCallback;


/**
 *
 * @author uros
 */
public class LoginContextManager extends PropertyChanges {
  
  private static final Logger LOG = Logger.getLogger(LoginContextManager.class.getCanonicalName());
  private static LoginContextManager instance = null;
  
  private Map<Class<? extends Callback>, Set<CallbackHandler>> handlers = Collections.synchronizedMap(new HashMap<Class<? extends Callback>, Set<CallbackHandler>>());
  private LoginContext context;
  
  private String server = "other";
  private String name = null;
  private char[] password = null;
  private Set<String> userRoles = new HashSet<String>();
  
  private JAASCallbackHandler jaasCallbackHandler = new JAASCallbackHandler();
  
  /** Creates a new instance of LoginContextManager */
  private LoginContextManager() throws LoginException {
    javax.security.auth.login.Configuration.setConfiguration(new com.openitech.auth.jaas.Configuration());
  }
  
  public static LoginContextManager getInstance() {
    if (instance==null) {
      try {
        instance = new LoginContextManager();
      } catch (LoginException ex) {
        LOG.log(Level.SEVERE, "Couldn't create a LoginContextManager instance", ex);
        instance = null;
      }
    }
    
    return instance;
  }
  
  private void contextLogin() throws LoginException {
    if (context==null) {
      context = new LoginContext(getServer(), jaasCallbackHandler);
    }
    firePropertyChange("connecting", false, true);
    context.login();
  }
  
  private void contextLogout()  {
    if (context!=null) {
      try {
        context.logout();
      } catch (LoginException ex) {
        LOG.log(Level.WARNING, "Couldn't close the security context", ex);
      }
      context=null;
      userRoles.clear();
    }
  }
  
  public boolean isLoggedOn() {
    return ((getSubject()!=null)&&(getSubject().getPrincipals(com.openitech.auth.jaas.LocalPrincipal.class).size()>0));
  }
  
  public Subject getSubject() {
    return context!=null?context.getSubject():null;
  }
  
  public void logon() throws LoginException {
    logon(getOwner());
  }
  
  public void logon(Object source) throws LoginException {
    if (name==null||
        password==null) {
      java.awt.Component owner = (source instanceof java.awt.Component)?((java.awt.Component) source):null;
      JXLoginPanel.showLoginDialog(owner, new LocalLoginService());
    } else {
      contextLogin();
    }
    final boolean newValue = isLoggedOn();
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        firePropertyChange("logon", !newValue, newValue);
      }
    });
  }
  
  public void logout() {
    final boolean oldValue = isLoggedOn();
    contextLogout();
    final boolean newValue = isLoggedOn();
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        firePropertyChange("logout", oldValue, newValue);
      }
    });
  }
  
  public void clearState() {
    final boolean oldValue = isLoggedOn();
    contextLogout();
    name = null;
    password = null;
    userRoles.clear();
    final boolean newValue = isLoggedOn();
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        firePropertyChange("logout", oldValue, newValue);
      }
    });
  }
  
  public void registerHandler(Class<? extends Callback> type, CallbackHandler handler) {
    Set<CallbackHandler> set = handlers.containsKey(type)?handlers.get(type):Collections.synchronizedSet(new HashSet<CallbackHandler>());
    set.add(handler);
    handlers.put(type, set);
  }
  
  public void unregisterHandler(Class<? extends Callback> type, CallbackHandler handler) {
    if (handlers.containsKey(type)) {
      Set<CallbackHandler> set = handlers.get(type);
      set.remove(handler);
      handlers.put(type, set);
    }
  }
    
  private class LocalLoginService extends LoginService {
    /**
     * This method is intended to be implemented by clients
     * wishing to authenticate a user with a given password.
     * Clients should implement the authentication in a
     * manner that the authentication can be cancelled at
     * any time.
     *
     * @param name username
     * @param password password
     * @param server server (optional)
     * @return <code>true</code> on authentication success
     * @throws Exception
     */
    public boolean authenticate(String aname, char[] apassword, String server) throws Exception {
      try {
        name = aname;
        password = apassword;
        contextLogin();
        return true;
      } catch (Throwable ex) {
        LOG.warning(ex.getMessage());
        return false;
      }
    }
    
    /**
     * Called immediately after a successful authentication. This method should return an array
     * of user roles or null if role based permissions are not used.
     *
     * @return per default <code>null</code>
     */
    public String[] getUserRoles() {
      if (userRoles==null) {
        return null;
      } else {
        return (String[]) userRoles.toArray();
      }
    }
  }
  
  private class JAASCallbackHandler implements CallbackHandler {
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
      for (Callback callback:callbacks) {
        if (callback instanceof NameCallback) {
          ((NameCallback) callback).setName(name);
        } else if (callback instanceof PasswordCallback) {
          ((PasswordCallback) callback).setPassword(password);
        } else {
          if (callback instanceof LocalCommitCallback) {
            for (LocalGroupPrincipal supplementaryGroup:((LocalCommitCallback) callback).getSupplementaryGroups()) {
              userRoles.add(supplementaryGroup.getName());
            }
          }
          if (handlers.containsKey(callback.getClass())) {
            Set<CallbackHandler> set = handlers.get(callback.getClass());
            for (Iterator<CallbackHandler> iterator=set.iterator(); iterator.hasNext(); ) {
              iterator.next().handle(new Callback[] {callback});
            }
          }
        }
      }
    }
  }

  /**
   * Holds value of property owner.
   */
  private java.awt.Component owner;

  /**
   * Getter for property owner.
   * @return Value of property owner.
   */
  public java.awt.Component getOwner() {
    return this.owner;
  }

  /**
   * Setter for property owner.
   * @param owner New value of property owner.
   */
  public void setOwner(java.awt.Component owner) {
    this.owner = owner;
  }

  /**
   * Getter for property server.
   * @return Value of property server.
   */
  public java.lang.String getServer() {
    return this.server;
  }

  /**
   * Setter for property server.
   * @param server New value of property server.
   */
  public void setServer(String server) {
    if (!Equals.equals(getServer(), server)) {
      contextLogout();
      this.server = server;
    }
  }

  /**
   * Called immediately after a successful authentication. This method should return an array
   * of user roles or null if role based permissions are not used.
   * 
   * @return per default <code>null</code>
   */
  public Set<String> getUserRoles() {
    return this.userRoles;
  }
  
  public String getName() {
    return this.name;
  }
}
