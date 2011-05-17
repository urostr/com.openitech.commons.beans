/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.spring;

import com.openitech.auth.LoginContextManager;
import java.util.logging.Logger;
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
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("getContext");
    LoginContextManager.getInstance().logon();
    ConfigurableApplicationContext result = SpringContextManager.getInstance().getContext();
    assertNotNull(result);
  }

}
