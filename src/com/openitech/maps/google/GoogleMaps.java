/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.maps.google;

import com.openitech.SystemProperties;
import com.openitech.db.components.DbNaslovDataModel.Naslov;
import com.openitech.maps.Maps;
import com.openitech.maps.Maps.Location.Quality;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;

import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author uros
 */
public class GoogleMaps extends Maps {

  private static final String GEOCODE_API_URL = "http://maps.googleapis.com/maps/api/geocode/xml?address=";
  private static final String DIRECTIONS_API_URL = "http://maps.googleapis.com/maps/api/directions/xml?";
  private static final String ZERO_RESULTS = "ZERO_RESULTS";
  private static final String OK = "OK";
  private XPath xpath = XPathFactory.newInstance().newXPath();
  private XPathExpression xpGeocodeStatus = xpath.compile("GeocodeResponse/status");
  private XPathExpression xpResults = xpath.compile("//GeocodeResponse/result");
  private XPathExpression xpLattitude = xpath.compile("//GeocodeResponse/result[1]/*/location/lat");
  private XPathExpression xpLongitude = xpath.compile("//GeocodeResponse/result[1]/*/location/lng");

  private XPathExpression xpDirectionsStatus = xpath.compile("DirectionsResponse/status");
  private XPathExpression xpDistances = xpath.compile("//DirectionsResponse/route[1]/leg/step/distance/value");
  
  public GoogleMaps() throws javax.xml.xpath.XPathExpressionException {
  }

  @Override
  public Location getLocation(String ulica,
          String hisnaStevilka,
          String hisnaStevilkaDodatek,
          String postnaStevilka,
          String posta,
          String naselje) {

    Location result = null;
    Location.Quality quality = null;
    
    try {
      ulica = ulica == null ? null : URLEncoder.encode(ulica, "UTF-8");
      hisnaStevilka = hisnaStevilka == null ? null : URLEncoder.encode(hisnaStevilka, "UTF-8");
      hisnaStevilkaDodatek = hisnaStevilkaDodatek == null ? null : URLEncoder.encode(hisnaStevilkaDodatek, "UTF-8");
      postnaStevilka = postnaStevilka == null ? null : URLEncoder.encode(postnaStevilka, "UTF-8");
      posta = posta == null ? null : URLEncoder.encode(posta, "UTF-8");
      StringBuilder address = encodeUlica(ulica, hisnaStevilka, hisnaStevilkaDodatek, posta, postnaStevilka);
      URLConnection load = getHTTPConnection(address);     

      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.parse(load.getInputStream());


      String status = xpGeocodeStatus.evaluate(doc);
      Logger.getLogger(GoogleMaps.class.getName()).log(Level.INFO, "{0}:{1}", new Object[]{address, status});
      
      if (OK.equals(status)) {
        quality = Quality.EXACT;
      }
      
      if (postnaStevilka != null) {
        if (ZERO_RESULTS.equals(status)||!isResultValid(doc)) {
          address = encodeUlica(ulica, hisnaStevilka, hisnaStevilkaDodatek, postnaStevilka);

          load = getHTTPConnection(address);
          doc = builder.parse(load.getInputStream());

          status = xpGeocodeStatus.evaluate(doc);
          Logger.getLogger(GoogleMaps.class.getName()).log(Level.INFO, "{0}:{1}", new Object[]{address, status});
          
          if (OK.equals(status)) {
            quality = Quality.EXACT;
          }
        }
      }
      
      if (naselje != null) {
        if (ZERO_RESULTS.equals(status)||!isResultValid(doc)) {
          address = encodeUlica(ulica, hisnaStevilka, hisnaStevilkaDodatek, naselje, postnaStevilka);

          load = getHTTPConnection(address);
          doc = builder.parse(load.getInputStream());

          status = xpGeocodeStatus.evaluate(doc);
          Logger.getLogger(GoogleMaps.class.getName()).log(Level.INFO, "{0}:{1}", new Object[]{address, status});
          
          if (OK.equals(status)) {
            quality = Quality.GOOD_GUESS;
          }
        }
      }

      if (posta != null && postnaStevilka != null) {
        if (ZERO_RESULTS.equals(status)||!isResultValid(doc)) {
          address = encodePostnaStevilka(postnaStevilka, posta);

          load = getHTTPConnection(address);
          doc = builder.parse(load.getInputStream());

          status = xpGeocodeStatus.evaluate(doc);
          Logger.getLogger(GoogleMaps.class.getName()).log(Level.INFO, "{0}:{1}", new Object[]{address, status});
          
          if (OK.equals(status)) {
            quality = Quality.TOWN;
          }
        }
      }
      
      if (postnaStevilka != null) {
        if (ZERO_RESULTS.equals(status)||!isResultValid(doc)) {
          address = encodePosta(postnaStevilka);

          load = getHTTPConnection(address);
          doc = builder.parse(load.getInputStream());

          status = xpGeocodeStatus.evaluate(doc);
          Logger.getLogger(GoogleMaps.class.getName()).log(Level.INFO, "{0}:{1}", new Object[]{address, status});
          
          if (OK.equals(status)) {
            quality = Quality.TOWN;
          }
        }
      }           

      if (posta != null) {
        if (ZERO_RESULTS.equals(status)||!isResultValid(doc)) {
          address = encodePosta(posta);

          load = getHTTPConnection(address);
          doc = builder.parse(load.getInputStream());

          status = xpGeocodeStatus.evaluate(doc);
          Logger.getLogger(GoogleMaps.class.getName()).log(Level.INFO, "{0}:{1}", new Object[]{address, status});
          
          if (OK.equals(status)) {
            quality = Quality.TOWN;
          }
        }

        if (ZERO_RESULTS.equals(status)||!isResultValid(doc)) {
          address = encodePosta(naselje);

          load = getHTTPConnection(address);
          doc = builder.parse(load.getInputStream());

          status = xpGeocodeStatus.evaluate(doc);
          Logger.getLogger(GoogleMaps.class.getName()).log(Level.INFO, "{0}:{1}", new Object[]{address, status});
          
          if (OK.equals(status)) {
            quality = Quality.TOWN;
          }
        }
      }

//      dumpXML(doc);

      if (OK.equals(status)) {
        NodeList nodes = (NodeList) xpResults.evaluate(doc, XPathConstants.NODESET);

        if (nodes.getLength() == 1) {
          Double lattitude = (Double) xpLattitude.evaluate(doc, XPathConstants.NUMBER);
          Double longitude = (Double) xpLongitude.evaluate(doc, XPathConstants.NUMBER);

          result = new Location(lattitude, longitude);
          
          if (quality.equals(Quality.EXACT) && 
              (ulica == null ||
               hisnaStevilka == null)) {
            quality = Quality.TOWN;
          }
          
          result.setQuality(quality);
        }
      }
    } catch (Exception ex) {
      Logger.getLogger(GoogleMaps.class.getName()).log(Level.SEVERE, null, ex);
    }

    return result;
  }

  private URLConnection getHTTPConnection(StringBuilder address) throws MalformedURLException, IOException {
    URL url = new URL(address.toString());
    
    URLConnection load;

    if (SystemProperties.HttpProxyConfig.isUseHttpProxy()) {
      load = url.openConnection(SystemProperties.HttpProxyConfig.getProxy());
    } else {
      load = url.openConnection();
    }
    return load;
  }

  private boolean isResultValid(Document doc) throws XPathExpressionException {
    NodeList nodes = (NodeList) xpResults.evaluate(doc, XPathConstants.NODESET);
    
    return nodes.getLength() == 1;
  }

  private void dumpXML(Document doc) throws TransformerException, TransformerFactoryConfigurationError, TransformerConfigurationException {
    TransformerFactory tFactory = TransformerFactory.newInstance();
    Transformer transformer = tFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    StreamResult sr = new StreamResult(System.out);
    transformer.transform(source, sr);
  }

  private StringBuilder encodePosta(String posta) {
    StringBuilder address = new StringBuilder();
    address.append(GEOCODE_API_URL).append("+").append(posta).append(",+SI&sensor=false");
    return address;
  }

  private StringBuilder encodePostnaStevilka(String postnaStevilka, String posta) {
    StringBuilder address = new StringBuilder();
    if (postnaStevilka != null) {
      address.append(address.length() > 0 ? "," : "").append("+").append(postnaStevilka).append("+").append(posta);
    } else {
      address.append(address.length() > 0 ? "," : "").append("+").append(posta);
    }
    address.insert(0, GEOCODE_API_URL);
    address.append(",+SI&sensor=false");
    return address;
  }

  private StringBuilder encodeUlica(String ulica, String hisnaStevilka, String hisnaStevilkaDodatek, String posta, String postnaStevilka) {
    StringBuilder address = new StringBuilder();
    if (ulica != null) {
      address.append(ulica);
      if (hisnaStevilka != null) {
        address.append("%20").append(hisnaStevilka).append(hisnaStevilkaDodatek == null ? "" : hisnaStevilkaDodatek);
      }
    }
    
    if (postnaStevilka != null) {
      address.append(address.length() > 0 ? "," : "").append("+").append(postnaStevilka).append("+").append(posta);
    } else {
      address.append(address.length() > 0 ? "," : "").append("+").append(posta);
    }
    
    address.insert(0, GEOCODE_API_URL);
    address.append(",+SI&sensor=false");
    return address;
  }


  private StringBuilder encodeUlica(String ulica, String hisnaStevilka, String hisnaStevilkaDodatek, String postnaStevilka) {
    StringBuilder address = new StringBuilder();
    if (ulica != null) {
      address.append(ulica);
      if (hisnaStevilka != null) {
        address.append("%20").append(hisnaStevilka).append(hisnaStevilkaDodatek == null ? "" : hisnaStevilkaDodatek);
      }
    }
    
    if (postnaStevilka != null) {
      address.append(address.length() > 0 ? "," : "").append("+").append(postnaStevilka).append("+");
    }
    address.insert(0, GEOCODE_API_URL);
    address.append(",+SI&sensor=false");
    return address;
  }
  
  @Override
  public Double getDistance(Naslov origin, Naslov destination, Naslov... waypoints) {
    List<Naslov> route = new ArrayList<Naslov>();
    List<Double> distances = new ArrayList<Double>();

    waypoints = waypoints == null ? new Naslov[]{} : waypoints;
    int w = 0;

    if (origin != null) {
      if (origin.getLocation() == null) {
        throw new IllegalArgumentException("Unknown address:" + origin.toString());
      }
      route.add(origin);
    }

    while (w < waypoints.length) {
      if (waypoints[w].getLocation() == null) {
        throw new IllegalArgumentException("Unknown address:" + waypoints[w].toString());
      }
      route.add(waypoints[w]);

      if (route.size() >= 10) {
        distances.addAll(getDistance(route));
        route.clear();
        route.add(waypoints[w]);
      }

      w++;
    }

    if (destination != null) {
      if (destination.getLocation() == null) {
        throw new IllegalArgumentException("Unknown address:" + destination.toString());
      }

      route.add(destination);
    }

    if (route.size() > 1) {
      distances.addAll(getDistance(route));
    }

    if (!distances.isEmpty()) {
      double result = 0;

      for (Double distance : distances) {
        result += distance;
      }

      return result;
    } else {
      return null;
    }
  }

  private List<Double> getDistance(List<Naslov> route) {
    List<Double> distances = new ArrayList<Double>();
    
    try {
      StringBuilder address = new StringBuilder();

      address.append(DIRECTIONS_API_URL);
      address.append("origin=").append(route.get(0).getLocation().toString());
      address.append("&destination=").append(route.get(route.size() - 1).getLocation().toString());

      if (route.size() > 2) {
        address.append("&waypoints=optimize:false");
        for (int i = 1; i < route.size() - 1; i++) {
          address.append("|").append(route.get(i).getLocation().toString());
        }
      }

      address.append("&sensor=false");

      URLConnection load = getHTTPConnection(address);

      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.parse(load.getInputStream());
      
//      dumpXML(doc);
      
      String status = xpDirectionsStatus.evaluate(doc);
      Logger.getLogger(GoogleMaps.class.getName()).log(Level.INFO, "{0}:{1}", new Object[]{address, status});
      
      if (OK.equals(status)) {
        NodeList nodes = (NodeList) xpDistances.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < nodes.getLength(); i++) {
          Node item = nodes.item(i);
          
          distances.add(Double.valueOf(item.getFirstChild().getNodeValue()));
        }
      }
    } catch (Exception ex) {
      Logger.getLogger(GoogleMaps.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    return distances;
  }
}
