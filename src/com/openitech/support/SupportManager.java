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
