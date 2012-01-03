/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.maps;

import com.openitech.db.components.DbNaslovDataModel.Naslov;
import com.openitech.maps.Maps.Location;
import com.openitech.value.fields.FieldValue;
import junit.framework.TestCase;

/**
 *
 * @author uros
 */
public class MapsTest extends TestCase {
  
  public MapsTest(String testName) {
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
   * Test of getInstance method, of class Maps.
   */
  public void testGetInstance() {
    System.out.println("getInstance");
    
    Maps result = Maps.getInstance();
    assertNotNull(result);
  }

  /**
   * Test of getLocation method, of class Maps.
   */
  public void testGetLocation() {
    System.out.println("getLocation");
    String ulica = "Puhova";
    String hisnaStevilka = "2";
    String hisnaStevilkaDodatek = "";
    String postnaStevilka = "1000";
    String posta = "Ljubljana";
    Maps instance = Maps.getInstance();
    Location location = instance.getLocation(ulica, hisnaStevilka, hisnaStevilkaDodatek, postnaStevilka, posta);
    assertNotNull(location);
    
    location = instance.getLocation(ulica, hisnaStevilka, hisnaStevilkaDodatek, null, null);
    assertNull(location);
  }
  
  
  public void testDistance() {
    System.out.println("getDistance");
    
    Naslov origin = new Naslov();
    
    origin.setUlica(new FieldValue("ULICA", java.sql.Types.VARCHAR, "Puhova"));
    origin.setHisnaStevilka(new FieldValue("HS", java.sql.Types.VARCHAR, "2"));
    origin.setPosta(new FieldValue("POSTA", java.sql.Types.VARCHAR, "Ljubljana"));
           
    Naslov destination = new Naslov();
    
    destination.setUlica(new FieldValue("ULICA", java.sql.Types.VARCHAR, "Brnèièeva"));
    destination.setHisnaStevilka(new FieldValue("HS", java.sql.Types.VARCHAR, "13"));
    destination.setPosta(new FieldValue("POSTA", java.sql.Types.VARCHAR, "Ljubljana"));
    
    Maps instance = Maps.getInstance();
    Double distance = instance.getDistance(origin, destination);
    
    assertNotNull(distance);
  }
}
