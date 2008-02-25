/*
 * LocalLogoutCallback.java
 *
 * Created on Ponedeljek, 9 april 2007, 18:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.auth.jaas.callback;

import java.util.List;
import javax.security.auth.callback.Callback;
import com.openitech.auth.jaas.LocalGroupPrincipal;

/**
 *
 * @author uros
 */
public class LocalCommitCallback implements Callback {
  private static final String FALSE=Boolean.FALSE.toString();
  
  /** Creates a new instance of LocalLogoutCallback */
  public LocalCommitCallback(String username, char[] password, List<LocalGroupPrincipal> supplementaryGroups) throws ClassNotFoundException {
    this.supplementaryGroups = supplementaryGroups;
    this.username = username;
  }

  /**
   * Holds value of property supplementaryGroups.
   */
  private List<LocalGroupPrincipal> supplementaryGroups;

  /**
   * Getter for property secondaryPrincipals.
   * @return Value of property secondaryPrincipals.
   */
  public List<LocalGroupPrincipal> getSupplementaryGroups() {
    return this.supplementaryGroups;
  }

  /**
   * Holds value of property username.
   */
  private String username;

  /**
   * Getter for property username.
   * @return Value of property username.
   */
  public String getUsername() {
    return this.username;
  }
  
}
