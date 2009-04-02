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
import java.io.InputStreamReader;
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
  private final String[] contextDefinition;
  private final Logger logger = Logger.getLogger(SpringApplicationContext.class.getName());
  
  /** Creates a new instance of SpringApplicationContext */
  public SpringApplicationContext() {
    this.contextDefinition = SpringApplicationContextPath.CONTEXT_DEFINITION;
  }
  
  public SpringApplicationContext(String[] contextDefinition) {
    this.contextDefinition = contextDefinition;
  }
  
  public void refresh() throws BeansException, IllegalStateException {
    XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(this);
    xmlReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
    for (String location:contextDefinition) {
      Resource[] resources;
      String description = null;
      try {
        description = "[undefined]";
        resources = resolver.getResources(location);
        
        for (Resource resource:resources) {
          description = resource.getDescription();
          logger.log(Level.CONFIG, "Loading Bean definitions from  '"+description+"'");

          /*
          if (log.isDebugEnabled()) {
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());
            
            char[] cb = new char[1024];
            int count;
            StringBuffer document = new StringBuffer();
            
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
          } else
            xmlReader.loadBeanDefinitions(resource);
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
