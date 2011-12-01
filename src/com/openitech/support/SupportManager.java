/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.support;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.redmine.ta.AuthenticationException;
import org.redmine.ta.NotFoundException;
import org.redmine.ta.RedmineException;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.Issue;
import org.redmine.ta.beans.Project;
import org.redmine.ta.beans.User;

/**
 *
 * @author domenbasic
 */
public class SupportManager {

  private RedmineManager manager;
  
  private String host;
  private String apiAccessKey;
  private String projectKey;
  private Project project;
  
  public SupportManager(String host, String apiAccessKey, String projectKey) {
    this.host = host;
    this.apiAccessKey = apiAccessKey;
    this.projectKey = projectKey;
    this.project = new Project();
    this.project.setIdentifier(projectKey);
    manager = new RedmineManager(host, apiAccessKey);
  }

  public void uploadIssue(String title, String description){
    try {
      Issue issueToCreate = new Issue();
      issueToCreate.setSubject(title);
      issueToCreate.setDescription(description);


      issueToCreate.setProject(project);
      
      manager.createIssue(projectKey, issueToCreate);
    } catch (Exception ex) {
      Logger.getLogger(SupportManager.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
