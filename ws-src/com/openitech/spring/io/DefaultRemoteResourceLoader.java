/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.spring.io;

import com.openitech.spring.beans.factory.config.PropertyRetriever;
import com.openitech.spring.beans.factory.config.PropertyType;
import com.openitech.spring.io.util.ResourceUtils;
import com.openitech.sql.properties.SqlPropertyRetriever;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/**
 *
 * @author uros
 */
public class DefaultRemoteResourceLoader extends DefaultResourceLoader {

  protected PropertyRetriever propertyRetriever = new SqlPropertyRetriever();

  public DefaultRemoteResourceLoader(ClassLoader classLoader, PropertyRetriever propertyRetriever) {
    super(classLoader);
    this.propertyRetriever = propertyRetriever;
  }

  public DefaultRemoteResourceLoader(ClassLoader classLoader) {
    super(classLoader);
  }

  public DefaultRemoteResourceLoader() {
  }

  /**
   * Get the value of propertyRetriever
   *
   * @return the value of propertyRetriever
   */
  public PropertyRetriever getPropertyRetriever() {
    return propertyRetriever;
  }

  /**
   * Set the value of propertyRetriever
   *
   * @param propertyRetriever new value of propertyRetriever
   */
  public void setPropertyRetriever(PropertyRetriever propertyRetriever) {
    this.propertyRetriever = propertyRetriever;
  }

  @Override
  public Resource getResource(String location) {
    Matcher remotePath = ResourceUtils.REMOTE_PATH_PATTERN.matcher(location);
    if (remotePath.matches()) {
      PropertyType propertyType = ResourceUtils.getPropertyType(location);
      String propertyName = ResourceUtils.getPropertyName(location);
      String charsetName = ResourceUtils.getCharsetName(location);
     
      if (charsetName==null) {
        charsetName = Charset.defaultCharset().name();
      }
      
      Object value = propertyRetriever.getValue(propertyType, propertyName, charsetName);

      if (value==null) {
        return super.getResource(location);
      } else {
        return new PropertyResource(propertyType, propertyName, value);
      }
    } else {
      return super.getResource(location);
    }
  }

}
