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
package com.openitech.sql.util.mssql.users;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.io.ReadInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author domenbasic
 */
public class UserManager {

  private final String addUserSql = ReadInputStream.getResourceAsString(getClass(), "addUser.sql");
  private final String changePasswordSql = ReadInputStream.getResourceAsString(getClass(), "changePassword.sql");
  private final String deleteUserSql = ReadInputStream.getResourceAsString(getClass(), "deleteUser.sql");
  private static UserManager instance;

  public static UserManager getInstance() {
    if (instance == null) {
      instance = new UserManager();
    }
    return instance;
  }

  public void addUser(String userName, String password) throws SQLException {
    if (password == null || password.equals("")) {
      throw new SQLException("Password can't be empty!");
    }
    if (userName == null || userName.equals("")) {
      throw new SQLException("Username can't be empty!");
    }
    Connection connection = ConnectionManager.getInstance().getConnection();
    Statement statement = connection.createStatement();
    String sqlAddUser = addUserSql;
    sqlAddUser = sqlAddUser.replaceAll("<%username%>", userName);
    sqlAddUser = sqlAddUser.replaceAll("<%password%>", password);
    statement.execute(sqlAddUser);
  }

  public void editUser(String userName, String password) throws SQLException {
    if (password == null || password.equals("")) {
      throw new SQLException("Password can't be empty!");
    }
    Connection connection = ConnectionManager.getInstance().getConnection();
    Statement statement = connection.createStatement();
    String sqlChangePassword = changePasswordSql;
    sqlChangePassword = sqlChangePassword.replaceAll("<%username%>", userName);
    sqlChangePassword = sqlChangePassword.replaceAll("<%password%>", password);
    statement.execute(sqlChangePassword);
  }

  public void deleteUser(String userName) throws SQLException {
    Connection connection = ConnectionManager.getInstance().getConnection();
    Statement statement = connection.createStatement();
    String sqlDeleteUser = deleteUserSql;
    sqlDeleteUser = sqlDeleteUser.replaceAll("<%username%>", userName);
    statement.execute(sqlDeleteUser);
  }
}
