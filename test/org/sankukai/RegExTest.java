/*
 * RegExTest.java
 * JUnit based test
 *
 * Created on Èetrtek, 13 julij 2006, 14:13
 */

package org.sankukai;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.*;

/**
 *
 * @author uros
 */
public class RegExTest extends TestCase {
  private static final Pattern column = Pattern.compile("(\\$.\\{([^\\}]*)\\})");
  
  public RegExTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
  }

  protected void tearDown() throws Exception {
  }
  
  // TODO add test methods here. The name must begin with 'test'. For example:
  // public void testHello() {}
  public void testMatch() {
     Matcher match = column.matcher("$C{NAZIV}$S{-}$C{KRAJ}");
    assertTrue(match.find());
    
    System.out.println(match.group(2));
    assertEquals("NAZIV", match.group(2));
    //assertTrue(match.hitEnd());
  }

}
