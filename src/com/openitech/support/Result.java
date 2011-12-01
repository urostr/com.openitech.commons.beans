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
