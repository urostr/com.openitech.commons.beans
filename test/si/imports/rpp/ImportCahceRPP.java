/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package si.imports.rpp;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.DbDataSource;
import com.openitech.io.ReadInputStream;
import com.openitech.sql.util.SqlUtilities;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author domenbasic
 */
public class ImportCahceRPP {

  public static void main(String[] args) throws SQLException {
    new ImportCahceRPP().execute();
  }

  public ImportCahceRPP() {
    DbDataSource.DUMP_SQL = true;
    DbConnection.register();
  }
  PreparedStatement insertPP;

  private void execute() throws SQLException {
    final int start = 279998;
    final int end = 1992534;
    final int korak = 50000;
    SqlUtilities sqlUtilities = SqlUtilities.getInstance();
    insertPP = ConnectionManager.getInstance().getTxConnection().prepareStatement(ReadInputStream.getResourceAsString(getClass(), "sql/insertCachePP.sql", "cp1250"));
    int param = 1;
    boolean commit = false;
    for (int i = start; i < end; i = i + korak) {
      sqlUtilities.beginTransaction();
      commit = false;
      try {
        param = 1;
        insertPP.clearParameters();
        insertPP.setInt(param++, i);
        insertPP.setInt(param++, (i + korak - 1));
        int rowsAdded = insertPP.executeUpdate();
        System.out.println("Inserted " + rowsAdded + " rows. Between " + i + " and " + (i + korak - 1));
        commit = true;
      } catch (Exception ex) {
        Logger.getAnonymousLogger().log(Level.SEVERE, "Napaka pri importu rpp!", ex);
        commit = false;
      } finally {
        sqlUtilities.endTransaction(commit);
      }
    }
  }
}
