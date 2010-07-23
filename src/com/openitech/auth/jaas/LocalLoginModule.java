/*
 * LocalLoginModule.java
 *
 * Created on Nedelja, 8 april 2007, 9:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.auth.jaas;


import javax.security.auth.*;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import javax.security.auth.spi.*;
import java.util.Map;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.openitech.db.connection.DbAuthService;
import com.openitech.auth.jaas.callback.LocalAbortCallback;
import com.openitech.auth.jaas.callback.LocalCommitCallback;
import com.openitech.auth.jaas.callback.LocalLogoutCallback;

/**
 *
 * @author uros
 */
public class LocalLoginModule implements LoginModule {
  private static final String CLASS_NAME=LocalLoginModule.class.getCanonicalName();
  private static final Logger logger = Logger.getLogger(CLASS_NAME);
  
  static final java.util.ResourceBundle rb =
          java.util.ResourceBundle.getBundle("sun.security.util.AuthResources");
  
  // configurable options
  private boolean useFirstPass = false;
  private boolean tryFirstPass = false;
  private boolean storePass = false;
  private boolean clearPass = false;
  
  // the authentication status
  private boolean succeeded = false;
  private boolean commitSucceeded = false;
  
  // username, password, and JNDI context
  private String username;
  private char[] password;
  
  // the user (assume it is a UnixPrincipal)
  private LocalPrincipal userPrincipal;
  //private LocalGroupPrincipal groupPrincipal;
  private LinkedList<LocalGroupPrincipal> supplementaryGroups = new LinkedList<LocalGroupPrincipal>();
  
  // initial state
  private Subject subject;
  private CallbackHandler callbackHandler;
  private Map sharedState;
  private Map options;
  
  private static final String NAME = "javax.security.auth.login.name";
  private static final String PWD = "javax.security.auth.login.password";
  private static final String GROUPS = "javax.security.auth.login.supplementary.groups";
  
  /**
   * Initialize this <code>LoginModule</code>.
   *
   * <p>
   *
   * @param subject the <code>Subject</code> to be authenticated. <p>
   *
   * @param callbackHandler a <code>CallbackHandler</code> for communicating
   *			with the end user (prompting for usernames and
   *			passwords, for example). <p>
   *
   * @param sharedState shared <code>LoginModule</code> state. <p>
   *
   * @param options options specified in the login
   *			<code>Configuration</code> for this particular
   *			<code>LoginModule</code>.
   */
  public void initialize(Subject subject, CallbackHandler callbackHandler,
          Map<String,?> sharedState,
          Map<String,?> options) {
    
    this.subject = subject;
    this.callbackHandler = callbackHandler;
    this.sharedState = sharedState;
    this.options = options;
    
    // initialize any configured options
    tryFirstPass =
            "true".equalsIgnoreCase((String)options.get("tryFirstPass"));
    useFirstPass =
            "true".equalsIgnoreCase((String)options.get("useFirstPass"));
    storePass =
            "true".equalsIgnoreCase((String)options.get("storePass"));
    clearPass =
            "true".equalsIgnoreCase((String)options.get("clearPass"));
  }
  
  /**
   * <p> Prompt for username and password.
   * Verify the password against the relevant name service.
   *
   * <p>
   *
   * @return true always, since this <code>LoginModule</code>
   *		should not be ignored.
   *
   * @exception FailedLoginException if the authentication fails. <p>
   *
   * @exception LoginException if this <code>LoginModule</code>
   *		is unable to perform the authentication.
   */
  public boolean login() throws LoginException {
    
    // attempt the authentication
    if (tryFirstPass) {
      
      try {
        // attempt the authentication by getting the
        // username and password from shared state
        attemptAuthentication(true);
        
        // authentication succeeded
        succeeded = true;
        logger.config("\t\t[LocalLoginModule] " +
                "tryFirstPass succeeded");
        return true;
      } catch (LoginException le) {
        // authentication failed -- try again below by prompting
        cleanState();
        logger.config("\t\t[LocalLoginModule] " +
                "tryFirstPass failed with:" +
                le.toString());
      }
      
    } else if (useFirstPass) {
      
      try {
        // attempt the authentication by getting the
        // username and password from shared state
        attemptAuthentication(true);
        
        // authentication succeeded
        succeeded = true;
        logger.config("\t\t[LocalLoginModule] " +
                "useFirstPass succeeded");
        return true;
      } catch (LoginException le) {
        // authentication failed
        cleanState();
        logger.config("\t\t[LocalLoginModule] " +
                "useFirstPass failed");
        throw le;
      }
    }
    
    // attempt the authentication by prompting for the username and pwd
    try {
      attemptAuthentication(false);
      
      // authentication succeeded
      succeeded = true;
      logger.config("\t\t[LocalLoginModule] " +
              "regular authentication succeeded");
      return true;
    } catch (LoginException le) {
      cleanState();
      logger.config("\t\t[LocalLoginModule] " +
              "regular authentication failed");
      throw le;
    }
  }
  
  /**
   * Abstract method to commit the authentication process (phase 2).
   *
   * <p> This method is called if the LoginContext's
   * overall authentication succeeded
   * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
   * succeeded).
   *
   * <p> If this LoginModule's own authentication attempt
   * succeeded (checked by retrieving the private state saved by the
   * <code>login</code> method), then this method associates a
   * <code>UnixPrincipal</code>
   * with the <code>Subject</code> located in the
   * <code>LoginModule</code>.  If this LoginModule's own
   * authentication attempted failed, then this method removes
   * any state that was originally saved.
   *
   * <p>
   *
   * @exception LoginException if the commit fails
   *
   * @return true if this LoginModule's own login and commit
   *		attempts succeeded, or false otherwise.
   */
  public boolean commit() throws LoginException {
    
    if (succeeded == false) {
      return false;
    } else {
      if (subject.isReadOnly()) {
        cleanState();
        throw new LoginException("Subject is Readonly");
      }
      // add Principals to the Subject
      if (!subject.getPrincipals().contains(userPrincipal))
        subject.getPrincipals().add(userPrincipal);
      /*if (!subject.getPrincipals().contains(groupPrincipal))
        subject.getPrincipals().add(groupPrincipal);//*/
      for (int i = 0; i < supplementaryGroups.size(); i++) {
        if (!subject.getPrincipals().contains
                ((LocalGroupPrincipal)supplementaryGroups.get(i)))
          subject.getPrincipals().add((LocalGroupPrincipal) supplementaryGroups.get(i));
      }
      
      logger.info("[LocalLoginModule]: " +
              "added LocalPrincipal, LocalGroupPrincipal(s), to Subject");
    }
    // notify that we logged in
    if (callbackHandler != null) {
      try {
        Callback[] callbacks = new Callback[1];
        callbacks[0] = new LocalCommitCallback(username, password, supplementaryGroups);

        callbackHandler.handle(callbacks);
      } catch (java.lang.ClassNotFoundException e) {
        throw (LoginException) (new LoginException()).initCause(e);
      } catch (java.lang.Exception e) {
        logger.fine(e.getMessage());
      }
    }
    // in any case, clean out state
    cleanState();
    commitSucceeded = true;
    
    return true;
  }
  
  /**
   * <p> This method is called if the LoginContext's
   * overall authentication failed.
   * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
   * did not succeed).
   *
   * <p> If this LoginModule's own authentication attempt
   * succeeded (checked by retrieving the private state saved by the
   * <code>login</code> and <code>commit</code> methods),
   * then this method cleans up any state that was originally saved.
   *
   * <p>
   *
   * @exception LoginException if the abort fails.
   *
   * @return false if this LoginModule's own login and/or commit attempts
   *		failed, and true otherwise.
   */
  public boolean abort() throws LoginException {
    logger.info("\t\t[LocalLoginModule]: " +
            "aborted authentication failed");
    
    if (succeeded == false) {
      return false;
    } else if (succeeded == true && commitSucceeded == false) {
      
      // Clean out state
      succeeded = false;
      cleanState();
      
      userPrincipal = null;
      //groupPrincipal = null;
      supplementaryGroups = new LinkedList<LocalGroupPrincipal>();
      // notify that we've aborted the login
      if (callbackHandler != null) {
        Callback[] callbacks = new Callback[1];
        callbacks[0] = new LocalAbortCallback();
        
        try {
          callbackHandler.handle(callbacks);
        } catch (java.lang.Exception e) {
          logger.fine(e.getMessage());
        }
      }
    } else {
      // overall authentication succeeded and commit succeeded,
      // but someone else's commit failed
      logout();
    }
    return true;
  }
  
  /**
   * Logout a user.
   *
   * <p> This method removes the Principals
   * that were added by the <code>commit</code> method.
   *
   * <p>
   *
   * @exception LoginException if the logout fails.
   *
   * @return true in all cases since this <code>LoginModule</code>
   *		should not be ignored.
   */
  public boolean logout() throws LoginException {
    if (subject.isReadOnly()) {
      cleanState();
      throw new LoginException("Subject is Readonly");
    }
    subject.getPrincipals().remove(userPrincipal);
    //subject.getPrincipals().remove(groupPrincipal);
    for (int i = 0; i < supplementaryGroups.size(); i++) {
      subject.getPrincipals().remove
              ((LocalGroupPrincipal)supplementaryGroups.get(i));
    }
    
    
    // clean out state
    cleanState();
    succeeded = false;
    commitSucceeded = false;
    
    userPrincipal = null;
    //groupPrincipal = null;
    supplementaryGroups = new LinkedList<LocalGroupPrincipal>();
    
    // notify that we logged out
    if (callbackHandler != null) {
      Callback[] callbacks = new Callback[1];
      callbacks[0] = new LocalLogoutCallback();
      
      try {
        callbackHandler.handle(callbacks);
      } catch (java.lang.Exception e) {
        logger.fine(e.getMessage());
      }
    }
    
    logger.info("\t\t[LocalLoginModule]: " +
            "logged out Subject");
    return true;
  }
  
  /**
   * Attempt authentication
   *
   * <p>
   *
   * @param getPasswdFromSharedState boolean that tells this method whether
   *		to retrieve the password from the sharedState.
   */
  private void attemptAuthentication(boolean getPasswdFromSharedState) throws LoginException {
    String encryptedPassword = null;
    
    // first get the username and password
    boolean gotPasswdFromSharedState = getUsernamePassword(getPasswdFromSharedState);
    
    try {
      if (!DbAuthService.getInstance().authenticate(username, password, supplementaryGroups, gotPasswdFromSharedState)) {
        // authentication failed
        logger.info("\t\t[LocalLoginModule] " +
                "attemptAuthentication() failed");
        throw new FailedLoginException("Login incorrect");
      }
      
      // save input as shared state only if
      // authentication succeeded
      if (storePass &&
              !sharedState.containsKey(NAME) &&
              !sharedState.containsKey(PWD)) {
        sharedState.put(NAME, username);
        sharedState.put(PWD, password);
      }
      
      // create the user principal
      userPrincipal = new LocalPrincipal(username);
      
    } catch (ClassNotFoundException ex) {
      logger.config("Can't access the local database");
      throw new FailedLoginException("User not found");
    }
    // authentication succeeded
  }
  
  /**
   * Get the username and password.
   * This method does not return any value.
   * Instead, it sets global name and password variables.
   *
   * <p> Also note that this method will set the username and password
   * values in the shared state in case subsequent LoginModules
   * want to use them via use/tryFirstPass.
   *
   * <p>
   *
   *
   * @param getPasswdFromSharedState boolean that tells this method whether
   * 		to retrieve the password from the sharedState.
   * @throws javax.security.auth.login.LoginException
   * @return true if password was in the shared state
   */
  private boolean getUsernamePassword(boolean getPasswdFromSharedState)
  throws LoginException {
    
    if (getPasswdFromSharedState) {
      // use the password saved by the first module in the stack
      username = (String)sharedState.get(NAME);
      password = (char[])sharedState.get(PWD);
      
      if (sharedState.containsKey(GROUPS)) {
        String[] groups = (String[]) sharedState.get(GROUPS);
        supplementaryGroups = new LinkedList<LocalGroupPrincipal>();
        
        for (String group:groups) {
          supplementaryGroups.add(new LocalGroupPrincipal(group));
        }
      }
      
      if (username!=null&&password!=null)
        return true;
    }
    
    // prompt for a username and password
    if (callbackHandler == null)
      throw new LoginException("Error: no CallbackHandler available " +
              "to garner authentication information from the user");
    
    Callback[] callbacks = new Callback[2];
    callbacks[0] = new NameCallback(rb.getString("username: "));
    callbacks[1] = new PasswordCallback(rb.getString("password: "),false);
    
    try {
      callbackHandler.handle(callbacks);
      username = ((NameCallback)callbacks[0]).getName();
      char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
      password = new char[tmpPassword.length];
      System.arraycopy(tmpPassword, 0,
              password, 0, tmpPassword.length);
      ((PasswordCallback)callbacks[1]).clearPassword();
    } catch (java.io.IOException ioe) {
      throw new LoginException(ioe.toString());
    } catch (UnsupportedCallbackException uce) {
      throw new LoginException("Error: " + uce.getCallback().toString() +
              " not available to garner authentication information " +
              "from the user");
    }
    
    // print debugging information
    if (logger.isLoggable(Level.FINEST)) {
      logger.finest("\t\t[LocalLoginModule] " +
              "user entered username: " +
              username);
      StringBuilder message = new StringBuilder();
      message.append("\t\t[LocalLoginModule] " +
              "user entered password: ");
      message.append(password);
      logger.finest(message.toString());
    }
    
    return false;
  }
  
  /**
   * Clean out state because of a failed authentication attempt
   */
  private void cleanState() {
    username = null;
    if (password != null) {
      for (int i = 0; i < password.length; i++)
        password[i] = ' ';
      password = null;
    }
    
    if (clearPass) {
      sharedState.remove(NAME);
      sharedState.remove(PWD);
    }
  }
}
