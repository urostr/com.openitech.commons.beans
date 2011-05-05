/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.importer;

import com.openitech.db.model.DbTableModel;
import java.sql.SQLException;

/**
 *
 * @author uros
 */
public interface ImportPreviewModel {

  void cancel();

  DbTableModel preview();

  void save() throws SQLException;

}
