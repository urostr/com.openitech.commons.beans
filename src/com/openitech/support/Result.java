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

/**
 *
 * @author domenbasic
 */
public class Result {

  public static int OK = 0;
  public static int CANCEL = 1;
  
  private String title;
  private String description;
  private int result;

  public Result(String title, String description, int result) {
    this.title = title;
    this.description = description;
    this.result = result;
  }

  public String getDescription() {
    return description;
  }

  public int getResult() {
    return result;
  }

  public String getTitle() {
    return title;
  }
}
