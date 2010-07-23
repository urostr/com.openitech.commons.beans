/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.spring.io.support;

import com.openitech.spring.io.DefaultRemoteResourceLoader;
import com.openitech.spring.io.util.ResourceUtils;
import java.io.IOException;
import java.util.regex.Pattern;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 *
 * @author uros
 */
public class RemotePathMatchingResourcePatternResolver extends PathMatchingResourcePatternResolver {

  public RemotePathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {
    super(resourceLoader);
  }

  public RemotePathMatchingResourcePatternResolver(ClassLoader classLoader) {
    this(new DefaultRemoteResourceLoader(classLoader));
  }

  public RemotePathMatchingResourcePatternResolver() {
    this(new DefaultRemoteResourceLoader());
  }

  @Override
  public Resource[] getResources(String locationPattern) throws IOException {
    if (ResourceUtils.REMOTE_PATH_PATTERN.matcher(locationPattern).matches()) {
      return new Resource[] { getResource(locationPattern) };
    } else {
      return super.getResources(locationPattern);
    }
  }

}
