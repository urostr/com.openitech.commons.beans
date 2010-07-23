/*
 * PatternTest.java
 * JUnit based test
 *
 * Created on Torek, 23 september 2008, 13:42
 */

package com.openitech.util;

import com.openitech.db.model.DbDataSource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.*;

/**
 *
 * @author uros
 */
public class PatternTest extends TestCase {
  
  public PatternTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
  }

  protected void tearDown() throws Exception {
  }
  
  public void testSetName() {
    DbDataSource dsNaselja = new DbDataSource();
    
    String selectSql = com.openitech.io.ReadInputStream.getResourceAsString(com.openitech.db.components.JPIzbiraNaslova.class, "sql/mssql/sifrant_ns.sql", "cp1250")+"ORDER BY na_ime";
    String match = selectSql.toLowerCase().replaceAll("[\\r|\\n]"," ");
    
    Pattern namePattern = Pattern.compile(".*from\\W*(\\w*)\\W.*");

    Matcher matcher = namePattern.matcher(match);
    String name = selectSql.substring(0,Math.min(selectSql.length(),9));
    
    assertTrue(matcher.matches());
    
    name = matcher.group(1);
  }

}
