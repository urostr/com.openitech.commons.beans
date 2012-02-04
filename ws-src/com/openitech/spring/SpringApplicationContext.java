/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
 * SpringApplicationContext.java
 *
 * Created on Sreda, 4 april 2007, 1:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.spring;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Document;

/**
 *
 * @author uros
 */
public class SpringApplicationContext extends GenericApplicationContext {

  PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
  private String[] contextDefinition;
  private final Logger logger = Logger.getLogger(SpringApplicationContext.class.getName());

  /** Creates a new instance of SpringApplicationContext */
  public SpringApplicationContext() {
    try {
      this.contextDefinition = SpringApplicationContextPath.getContextDefinition();
    } catch (Exception ex) {
      this.contextDefinition = SpringApplicationContextPath.CONTEXT_DEFINITION;
      Logger.getLogger(SpringApplicationContext.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public SpringApplicationContext(String[] contextDefinition) {
    this.contextDefinition = contextDefinition;
  }

  @Override
  public void refresh() throws BeansException, IllegalStateException {
    XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(this);
    xmlReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
    for (String location : contextDefinition) {
      Resource[] resources;
      String description = null;
      try {
        description = "[undefined]";
        resources = resolver.getResources(location);

        for (Resource resource : resources) {
          description = resource.getDescription();
          logger.log(Level.CONFIG, "Loading Bean definitions from  '" + description + "'");

          /*
          if (log.isDebugEnabled()) {
          InputStreamReader reader = new InputStreamReader(resource.getInputStream());

          char[] cb = new char[1024];
          int count;
          StringBuilder document = new StringBuilder();

          do {
          count = reader.read(cb);
          if (count>0)
          document.append(cb, 0, count);
          } while (count!=-1);

          logger.log(Level.CONFIG, document.toString());
          }//*/

          if (resource instanceof UrlResource) {
            Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(resource.getInputStream());
            xmlReader.registerBeanDefinitions(dom, resource);
          } else {
            xmlReader.loadBeanDefinitions(resource);
          }
        }
      } catch (ParserConfigurationException ex) {
        throw new BeanDefinitionStoreException(description,
                "Parser configuration exception parsing XML from " + description, ex);
      } catch (IOException ex) {
        throw new BeanDefinitionStoreException(description,
                "IOException parsing XML document from " + description, ex);
      } catch (Throwable ex) {
        throw new BeanDefinitionStoreException(description,
                "Unexpected exception parsing XML document from " + description, ex);
      }
    }
    super.refresh();
  }
}
