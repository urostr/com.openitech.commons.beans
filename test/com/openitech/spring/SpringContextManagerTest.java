/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.spring;

import com.openitech.auth.LoginContextManager;
import javax.security.auth.login.LoginException;
import junit.framework.TestCase;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * @author uros
 */
public class SpringContextManagerTest extends TestCase {
    
    public SpringContextManagerTest(String testName) {
        super(testName);

        DbConnection.register();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

  /**
   * Test of getContext method, of class SpringContextManager.
   */
  public void testGetContext() throws LoginException {
    System.out.println("getContext");
    SpringContextManager instance = SpringContextManager.getInstance();
    LoginContextManager.getInstance().logon();
    ConfigurableApplicationContext result = instance.getContext();
    assertNotNull(result);
  }

}
