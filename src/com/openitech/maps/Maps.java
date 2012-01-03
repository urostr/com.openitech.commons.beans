/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.maps;

import com.openitech.db.components.DbNaslovDataModel.Naslov;
import com.openitech.maps.google.GoogleMaps;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.NumberFormatter;
import javax.xml.xpath.XPathExpressionException;

/**
 *
 * @author uros
 */
public abstract class Maps {
  private static Maps instance;

  protected Maps() {
  }
  
  public static Maps getInstance() {
    if (instance==null) {
      try {
        instance = new GoogleMaps();
      } catch (XPathExpressionException ex) {
        Logger.getLogger(Maps.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    
    return instance;
  }
  
  public Location getLocation(Naslov naslov) {
    return naslov.getLocation();
  }
  
  public abstract Location getLocation(String ulica, 
                                       String hisnaStevilka, 
                                       String hisnaStevilkaDodatek, 
                                       String postnaStevilka,
                                       String posta,
                                       String naselje);
  
  public abstract Double getDistance(Naslov origin, Naslov destination, Naslov... waypoints);
  
  /**
   * An immutable coordinate in the real (geographic) world, 
   * composed of a latitude and a longitude.
   * @author rbair
   */
  public static class Location {

    private static final NumberFormatter nf;
    private double latitude;
    private double longitude;

    static {
      DecimalFormat df = new DecimalFormat("#0.0000000", new DecimalFormatSymbols(Locale.US));
      df.setMaximumFractionDigits(7);
      df.setDecimalSeparatorAlwaysShown(true);
      
      nf = new NumberFormatter(df);
    }
    /**
     * Creates a new instance of Location from the specified
     * latitude and longitude. These are double values in decimal degrees, not
     * degrees, minutes, and seconds.  Use the other constructor for those.
     * @param latitude a latitude value in decmial degrees
     * @param longitude a longitude value in decimal degrees
     */
    public Location(double latitude, double longitude) {
      this.latitude = latitude;
      this.longitude = longitude;
    }
    // must be an array of length two containing lat then long in that order.

    /**
     * Creates a new instance of Location from the specified
     * latitude and longitude as an array of two doubles, with the
     * latitude first. These are double values in decimal degrees, not
     * degrees, minutes, and seconds.  Use the other constructor for those.
     * @param coords latitude and longitude as a double array of length two
     */
    public Location(double[] coords) {
      this(coords[0],coords[1]);
    }

    /**
     * Creates a new instance of Location from the specified
     * latitude and longitude. 
     * Each are specified as degrees, minutes, and seconds; not
     * as decimal degrees. Use the other constructor for those.
     * @param latDegrees the degrees part of the current latitude
     * @param latMinutes the minutes part of the current latitude
     * @param latSeconds the seconds part of the current latitude
     * @param lonDegrees the degrees part of the current longitude
     * @param lonMinutes the minutes part of the current longitude
     * @param lonSeconds the seconds part of the current longitude
     */
    public Location(double latDegrees, double latMinutes, double latSeconds,
            double lonDegrees, double lonMinutes, double lonSeconds) {
      this(latDegrees + (latMinutes + latSeconds / 60.0) / 60.0,
              lonDegrees + (lonMinutes + lonSeconds / 60.0) / 60.0);
    }

    /**
     * Get the latitude as decimal degrees
     * @return the latitude as decimal degrees
     */
    public double getLatitude() {
      return latitude;
    }

    /**
     * Get the longitude as decimal degrees
     * @return the longitude as decimal degrees
     */
    public double getLongitude() {
      return longitude;
    }

    /**
     * Returns true the specified Location and this Location represent
     * the exact same latitude and longitude coordinates.
     * @param obj a Location to compare this Location to
     * @return returns true if the specified Location is equal to this one
     */
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Location) {
        Location coord = (Location) obj;
        return coord.latitude == latitude && coord.longitude == longitude;
      }
      return false;
    }

    @Override
    public int hashCode() {
      int hash = 3;
      hash = 67 * hash + (int) (Double.doubleToLongBits(this.latitude) ^ (Double.doubleToLongBits(this.latitude) >>> 32));
      hash = 67 * hash + (int) (Double.doubleToLongBits(this.longitude) ^ (Double.doubleToLongBits(this.longitude) >>> 32));
      return hash;
    }

    /**
     * {@inheritDoc}
     * @return 
     */
    @Override
    public String toString() {
      try {
        return nf.valueToString(latitude) + "," + nf.valueToString(longitude);
      } catch (ParseException ex) {
        return latitude + "," +longitude;
      }
    }
  }
  
  
}
