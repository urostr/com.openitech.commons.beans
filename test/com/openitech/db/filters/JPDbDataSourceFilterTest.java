/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db.filters;

import com.openitech.db.filters.JPDbDataSourceFilter.FiltersMap;
import javax.swing.JMenu;
import junit.framework.TestCase;

/**
 *
 * @author uros
 */
public class JPDbDataSourceFilterTest extends TestCase {
    
    public JPDbDataSourceFilterTest(String testName) {
        super(testName);
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
   * Test of getFilters method, of class JPDbDataSourceFilter.
   */
  public void testGetFilters() {
    System.out.println("getFilters");
    JPDbDataSourceFilter instance = new JPDbDataSourceFilter();
    FiltersMap expResult = null;
    FiltersMap result = instance.getFilters();
    assertNotNull(result);
  }

  /**
   * Test of getFilterMenuItem method, of class JPDbDataSourceFilter.
   */
  public void testGetFilterMenuItem() {
    System.out.println("getFilterMenuItem");
    JPDbDataSourceFilter instance = new JPDbDataSourceFilter();
    JMenu expResult = null;
    JMenu result = instance.getFilterMenuItem();
    assertNotNull(result);
  }

}
