/*
 * Configuration.java
 *
 * Created on Nedelja, 8 april 2007, 9:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.auth.jaas;

import java.io.*;
import java.util.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.AuthPermission;
import javax.security.auth.login.AppConfigurationEntry;
import sun.security.util.ResourcesMgr;
import sun.security.util.PropertyExpander;

/**
 *
 * @author uros
 */
public class Configuration extends javax.security.auth.login.Configuration {
  private static final String CLASS_NAME = Configuration.class.getCanonicalName();
  private static final String SECURITY_CONFIGURATION = "security.configuration";
  private HashMap<String,LinkedList<AppConfigurationEntry>> configuration;
  
  /** Creates a new instance of Configuration */
  public Configuration() {
    try {
      init();
    } catch (IOException ioe) {
      throw (SecurityException)
      new SecurityException(ioe.getMessage()).initCause(ioe);
    }
  }
  
  /**
   * Retrieve the AppConfigurationEntries for the specified <i>name</i>
   * from this Configuration.
   *
   * <p>
   *
   * @param name the name used to index the Configuration.
   *
   * @return an array of AppConfigurationEntries for the specified <i>name</i>
   *		from this Configuration, or null if there are no entries
   *		for the specified <i>name</i>
   */
  public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
    LinkedList<AppConfigurationEntry> list = null;
    synchronized (configuration) {
      list = configuration.get(name);
    }
    
    if (list == null || list.size() == 0)
      return null;
    
    AppConfigurationEntry[] entries =
            new AppConfigurationEntry[list.size()];
    Iterator<AppConfigurationEntry> iterator = list.iterator();
    for (int i = 0; iterator.hasNext(); i++) {
      AppConfigurationEntry e = iterator.next();
      entries[i] = new AppConfigurationEntry(e.getLoginModuleName(),
              e.getControlFlag(),
              e.getOptions());
    }
    return entries;
  }
  
  /**
   * Refresh and reload the Configuration by re-reading all of the
   * login configurations.
   *
   * <p>
   *
   * @exception SecurityException if the caller does not have permission
   *				to refresh the Configuration.
   */
  public synchronized void refresh() {
    
    java.lang.SecurityManager sm = System.getSecurityManager();
    if (sm != null)
      sm.checkPermission(new AuthPermission("refreshLoginConfiguration"));
    
    java.security.AccessController.doPrivileged
            (new java.security.PrivilegedAction() {
      public Object run() {
        try {
          init();
        } catch (java.io.IOException ioe) {
          throw new SecurityException(ioe.getLocalizedMessage());
        }
        return null;
      }
    });
  }
  
  private void init() throws IOException {
    HashMap<String,LinkedList<AppConfigurationEntry>> newConfig = new HashMap<String,LinkedList<AppConfigurationEntry>>();
    InputStreamReader isr
            = new InputStreamReader(getClass().getResourceAsStream(SECURITY_CONFIGURATION), "UTF-8");
    try {
      readConfig(isr, newConfig);
    } finally {
      isr.close();
    }
    configuration=newConfig;
  }
  
  
  // com.sun.security.auth.login.ConfigFile
  private StreamTokenizer st;
  private int lookahead;
  private int linenum;
  private boolean expandProp = true;
  
  private void readConfig(Reader reader, HashMap newConfig)
  throws IOException {
    
    int linenum = 1;
    
    if (!(reader instanceof BufferedReader))
      reader = new BufferedReader(reader);
    
    st = new StreamTokenizer(reader);
    st.quoteChar('"');
    st.wordChars('$', '$');
    st.wordChars('_', '_');
    st.wordChars('-', '-');
    st.lowerCaseMode(false);
    st.slashSlashComments(true);
    st.slashStarComments(true);
    st.eolIsSignificant(true);
    
    lookahead = nextToken();
    while (lookahead != StreamTokenizer.TT_EOF) {
      Logger.getLogger(CLASS_NAME).config("Reading next config entry");
      parseLoginEntry(newConfig);
    }
  }
  
  private void parseLoginEntry(HashMap newConfig) throws IOException {
    
    String appName;
    String moduleClass;
    String sflag;
    AppConfigurationEntry.LoginModuleControlFlag controlFlag;
    LinkedList configEntries = new LinkedList();
    
    // application name
    appName = st.sval;
    lookahead = nextToken();
    
    Logger.getLogger(CLASS_NAME).config("appName = " + appName);
    
    match("{");
    
    // get the modules
    while (peek("}") == false) {
      HashSet argSet = new HashSet();
      
      // get the module class name
      moduleClass = match("module class name");
      
      // controlFlag (required, optional, etc)
      sflag = match("controlFlag");
      if (sflag.equalsIgnoreCase("REQUIRED"))
        controlFlag =
                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
      else if (sflag.equalsIgnoreCase("REQUISITE"))
        controlFlag =
                AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
      else if (sflag.equalsIgnoreCase("SUFFICIENT"))
        controlFlag =
                AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
      else if (sflag.equalsIgnoreCase("OPTIONAL"))
        controlFlag =
                AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
      else {
        MessageFormat form = new MessageFormat(ResourcesMgr.getString
                ("Configuration Error:\n\tInvalid control flag, flag",
                "sun.security.util.AuthResources"));
        Object[] source = {sflag};
        throw new IOException(form.format(source));
      }
      
      // get the args
      HashMap<String,String> options = new HashMap<String,String>();
      String key;
      String value;
      while (peek(";") == false) {
        key = match("option key");
        match("=");
        try {
          value = expand(match("option value"));
        } catch (PropertyExpander.ExpandException peee) {
          throw new IOException(peee.getLocalizedMessage());
        }
        options.put(key, value);
      }
      
      lookahead = nextToken();
      
      // create the new element
      if (Logger.getLogger(CLASS_NAME).isLoggable(Level.CONFIG)) {
        StringBuffer config = new StringBuffer(108);
        config.append("\t\t" + moduleClass + ", " + sflag);
        java.util.Iterator<String> i = options.keySet().iterator();
        while (i.hasNext()) {
          key = i.next();
          config.append(", " +
                  key +
                  "=" +
                  options.get(key));
        }
        Logger.getLogger(CLASS_NAME).config(config.toString());
      }
      AppConfigurationEntry entry = new AppConfigurationEntry
              (moduleClass,
              controlFlag,
              options);
      configEntries.add(entry);
    }
    
    match("}");
    match(";");
    
    // add this configuration entry
    if (newConfig.containsKey(appName)) {
      MessageFormat form = new MessageFormat(ResourcesMgr.getString
              ("Configuration Error:\n\t" +
              "Can not specify multiple entries for appName",
              "sun.security.util.AuthResources"));
      Object[] source = {appName};
      throw new IOException(form.format(source));
    }
    newConfig.put(appName, configEntries);
    Logger.getLogger(CLASS_NAME).config("\t\t***Added entry for " +
            appName + " to overall configuration***");
  }
  
  private String match(String expect) throws IOException {
    
    String value = null;
    
    switch(lookahead) {
      case StreamTokenizer.TT_EOF:
        
        MessageFormat form1 = new MessageFormat(ResourcesMgr.getString
                ("Configuration Error:\n\texpected [expect], " +
                "read [end of file]",
                "sun.security.util.AuthResources"));
        Object[] source1 = {expect};
        throw new IOException(form1.format(source1));
        
      case '"':
      case StreamTokenizer.TT_WORD:
        
        if (expect.equalsIgnoreCase("module class name") ||
                expect.equalsIgnoreCase("controlFlag") ||
                expect.equalsIgnoreCase("option key") ||
                expect.equalsIgnoreCase("option value")) {
          value = st.sval;
          lookahead = nextToken();
        } else {
          MessageFormat form = new MessageFormat(ResourcesMgr.getString
                  ("Configuration Error:\n\tLine line: " +
                  "expected [expect], found [value]",
                  "sun.security.util.AuthResources"));
          Object[] source = {new Integer(linenum), expect, st.sval};
          throw new IOException(form.format(source));
        }
        break;
        
      case '{':
        
        if (expect.equalsIgnoreCase("{")) {
          lookahead = nextToken();
        } else {
          MessageFormat form = new MessageFormat(ResourcesMgr.getString
                  ("Configuration Error:\n\tLine line: expected [expect]",
                  "sun.security.util.AuthResources"));
          Object[] source = {new Integer(linenum), expect, st.sval};
          throw new IOException(form.format(source));
        }
        break;
        
      case ';':
        
        if (expect.equalsIgnoreCase(";")) {
          lookahead = nextToken();
        } else {
          MessageFormat form = new MessageFormat(ResourcesMgr.getString
                  ("Configuration Error:\n\tLine line: expected [expect]",
                  "sun.security.util.AuthResources"));
          Object[] source = {new Integer(linenum), expect, st.sval};
          throw new IOException(form.format(source));
        }
        break;
        
      case '}':
        
        if (expect.equalsIgnoreCase("}")) {
          lookahead = nextToken();
        } else {
          MessageFormat form = new MessageFormat(ResourcesMgr.getString
                  ("Configuration Error:\n\tLine line: expected [expect]",
                  "sun.security.util.AuthResources"));
          Object[] source = {new Integer(linenum), expect, st.sval};
          throw new IOException(form.format(source));
        }
        break;
        
      case '=':
        
        if (expect.equalsIgnoreCase("=")) {
          lookahead = nextToken();
        } else {
          MessageFormat form = new MessageFormat(ResourcesMgr.getString
                  ("Configuration Error:\n\tLine line: expected [expect]",
                  "sun.security.util.AuthResources"));
          Object[] source = {new Integer(linenum), expect, st.sval};
          throw new IOException(form.format(source));
        }
        break;
        
      default:
        MessageFormat form = new MessageFormat(ResourcesMgr.getString
                ("Configuration Error:\n\tLine line: " +
                "expected [expect], found [value]",
                "sun.security.util.AuthResources"));
        Object[] source = {new Integer(linenum), expect, st.sval};
        throw new IOException(form.format(source));
    }
    return value;
  }
  
  private boolean peek(String expect) {
    boolean found = false;
    
    switch (lookahead) {
      case ',':
        if (expect.equalsIgnoreCase(","))
          found = true;
        break;
      case ';':
        if (expect.equalsIgnoreCase(";"))
          found = true;
        break;
      case '{':
        if (expect.equalsIgnoreCase("{"))
          found = true;
        break;
      case '}':
        if (expect.equalsIgnoreCase("}"))
          found = true;
        break;
      default:
    }
    return found;
  }
  
  private int nextToken() throws IOException {
    int tok;
    while ((tok = st.nextToken()) == StreamTokenizer.TT_EOL) {
      linenum++;
    }
    return tok;
  }
  
  private String expand(String value)
  throws PropertyExpander.ExpandException, IOException {
    
    if ("".equals(value)) {
      return value;
    }
    
    if (expandProp) {
      
      String s = PropertyExpander.expand(value);
      
      if (s == null || s.length() == 0) {
        MessageFormat form = new MessageFormat(ResourcesMgr.getString
                ("Configuration Error:\n\tLine line: " +
                "system property [value] expanded to empty value",
                "sun.security.util.AuthResources"));
        Object[] source = {new Integer(linenum), value};
        throw new IOException(form.format(source));
      }
      return s;
    } else {
      return value;
    }
  }
}
