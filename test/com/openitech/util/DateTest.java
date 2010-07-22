/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.util;

import java.util.Calendar;
import junit.framework.TestCase;

/**
 *
 * @author uros
 */
public class DateTest extends TestCase {
    
    public DateTest(String testName) {
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

    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testDateToLong() {
      Calendar calendar = Calendar.getInstance();
      
      System.out.println(calendar.getTimeInMillis());
    }

}
