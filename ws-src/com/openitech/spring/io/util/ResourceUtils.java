/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.spring.io.util;

import com.openitech.spring.beans.factory.config.PropertyType;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author uros
 */
public abstract class ResourceUtils extends org.springframework.util.ResourceUtils {
  public static final Pattern REMOTE_PATH_PATTERN = Pattern.compile("^remote:(sql|ruby|groovy|bsh):(\\w+)(:(.+))?");
  public static final PropertyType getPropertyType(String location) {
    Matcher matcher = REMOTE_PATH_PATTERN.matcher(location);
    if (matcher.matches()) {
      return PropertyType.valueOf(matcher.group(1).toUpperCase());
    } else {
      return null;
    }
  }
  public static final String getPropertyName(String location) {
    Matcher matcher = REMOTE_PATH_PATTERN.matcher(location);
    if (matcher.matches()) {
      return matcher.group(2);
    } else {
      return null;
    }
  }
  public static final String getCharsetName(String location) {
    Matcher matcher = REMOTE_PATH_PATTERN.matcher(location);
    if (matcher.matches()&&matcher.groupCount()>2) {
      return Charset.forName(matcher.group(4)).name();
    } else {
      return null;
    }
  }
}
