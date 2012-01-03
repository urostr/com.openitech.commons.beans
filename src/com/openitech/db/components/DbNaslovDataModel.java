package com.openitech.db.components;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.model.DbDataSource;
import com.openitech.maps.Maps.Location;
import com.openitech.value.fields.FieldValue;
import com.openitech.io.ReadInputStream;
import com.openitech.maps.Maps;
import com.sun.rowset.CachedRowSetImpl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.rowset.CachedRowSet;

public class DbNaslovDataModel {

  public final DbDataSource dsUlice = new DbDataSource();
  protected DsFilterRPE dsUliceFilter;
  public final DbDataSource dsHisneStevilke = new DbDataSource();
  protected DsFilterRPE dsHisneStevilkeFilter;
  public final DbDataSource dsPoste = new DbDataSource();
  protected DsFilterRPE dsPosteFilter;
  public final DbDataSource dsPostneStevilke = new DbDataSource();
  protected DsFilterRPE dsPostneStevilkeFilter;
  protected final DbDataSource dsNaselja = new DbDataSource();
  public DsFilterRPE dsNaseljaFilter;
  public final DbDataSource dsMIDs = new DbDataSource();
  private PreparedStatement findHS_MID_1 = null;
  private PreparedStatement findHS_MID_2 = null;
  private PreparedStatement findHS_MID_3 = null;
  private PreparedStatement findUL_MID = null;
  private PreparedStatement findPT_MID = null;
  private PreparedStatement findNA_MID = null;
  private PreparedStatement selectHsMid = null;
  private boolean initDataSources = false;
  private static String dialect = null;

  private static String getDialect() {
    if (dialect == null) {
      dialect = ConnectionManager.getInstance().getDialect();
      if (dialect != null && dialect.length() > 0 && !dialect.endsWith("/")) {
        dialect = dialect + "/";
      }
    }
    return dialect;
  }

  public DbNaslovDataModel() {
    super();
  }

  protected boolean initDataSources() throws SQLException {
    boolean result = false;
    try {
      final String dialect = getDialect();
      final Connection connection = ConnectionManager.getInstance().getConnection();

      dsUliceFilter = new DsFilterRPE("<%filter_ulice%>");
      dsHisneStevilkeFilter = new DsFilterRPE("<%filter_hs%>");
      dsPosteFilter = new DsFilterRPE("<%filter_pt%>");
      dsPostneStevilkeFilter = new DsFilterRPE("<%filter_pt%>");
      dsNaseljaFilter = new DsFilterRPE("<%filter_na%>");

      dsUliceFilter.setFilterRequired(true);
      dsUliceFilter.addRequired(dsUliceFilter.I_TYPE_UL_IME);
      dsUliceFilter.addRequired(dsUliceFilter.I_TYPE_NA_IME);
      dsUliceFilter.addRequired(dsUliceFilter.I_TYPE_PT_IME, 2);
      dsUliceFilter.addRequired(dsUliceFilter.I_TYPE_PT_ID, 2);
      dsUliceFilter.setOperator("and");
      dsUlice.setCanAddRows(false);
      dsUlice.setCanDeleteRows(false);
      dsUlice.setReadOnly(true);
      dsUlice.setName("ulice");
      dsUlice.setShareResults(true);
      List parameters = new ArrayList();
      parameters.add(dsUliceFilter);
      parameters.add(dsUliceFilter);
      dsUlice.setParameters(parameters);
      dsUlice.setCountSql(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_ulic_c.sql", "cp1250"));
      dsUlice.setSelectSql(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_ulic.sql", "cp1250"));
      dsUlice.setFetchSize(108);
//        dsUlice.setQueuedDelay(108);
      dsUliceFilter.addDataSource(dsUlice);
      dsHisneStevilkeFilter.setFilterRequired(true);
      dsHisneStevilkeFilter.addRequired(dsHisneStevilkeFilter.I_TYPE_UL_IME);
      dsHisneStevilkeFilter.addRequired(dsHisneStevilkeFilter.I_TYPE_NA_IME);
      dsHisneStevilkeFilter.addRequired(dsHisneStevilkeFilter.I_TYPE_PT_IME, 2);
      dsHisneStevilkeFilter.addRequired(dsHisneStevilkeFilter.I_TYPE_PT_ID, 2);
      dsHisneStevilkeFilter.setOperator("and");
      dsHisneStevilke.setCanAddRows(false);
      dsHisneStevilke.setCanDeleteRows(false);
      dsHisneStevilke.setReadOnly(true);
      dsHisneStevilke.setName("hisne_stevilke");
      dsHisneStevilke.setShareResults(true);

      parameters = new ArrayList();
      parameters.add(dsHisneStevilkeFilter);
      parameters.add(dsHisneStevilkeFilter);
      dsHisneStevilke.setParameters(parameters);
      dsHisneStevilke.setCountSql(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_hs_c.sql", "cp1250"));
      dsHisneStevilke.setSelectSql(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_hs.sql", "cp1250"));
      dsHisneStevilke.setFetchSize(108);
      //       dsHisneStevilke.setQueuedDelay(50);
      dsHisneStevilkeFilter.addDataSource(dsHisneStevilke);
      dsPosteFilter.setFilterRequired(true);
      dsPosteFilter.addRequired(dsPosteFilter.I_TYPE_UL_IME);
      dsPosteFilter.addRequired(dsPosteFilter.I_TYPE_NA_IME);
      dsPosteFilter.addRequired(dsPosteFilter.I_TYPE_PT_IME);
      dsPosteFilter.addRequired(dsPosteFilter.I_TYPE_PT_ID);
      dsPosteFilter.setOperator("and");
      dsPoste.setCanAddRows(false);
      dsPoste.setCanDeleteRows(false);
      dsPoste.setReadOnly(true);
      dsPoste.setName("poste");
      dsPoste.setShareResults(true);

      parameters = new ArrayList();
      parameters.add(dsPosteFilter);
      parameters.add(dsPosteFilter);
      dsPoste.setParameters(parameters);
      dsPoste.setCountSql(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_pt_c.sql", "cp1250"));
      dsPoste.setSelectSql(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_pt.sql", "cp1250") + "ORDER BY pt_ime");
      dsPoste.setFetchSize(108);
      //      dsPoste.setQueuedDelay(54);
      dsPosteFilter.addDataSource(dsPoste);
      dsPostneStevilkeFilter.setFilterRequired(true);
      dsPostneStevilkeFilter.addRequired(dsPostneStevilkeFilter.I_TYPE_UL_IME);
      dsPostneStevilkeFilter.addRequired(dsPostneStevilkeFilter.I_TYPE_NA_IME);
      dsPostneStevilkeFilter.addRequired(dsPostneStevilkeFilter.I_TYPE_PT_IME);
      dsPostneStevilkeFilter.addRequired(dsPostneStevilkeFilter.I_TYPE_PT_ID);
      dsPostneStevilkeFilter.setOperator("and");
      dsPostneStevilke.setCanAddRows(false);
      dsPostneStevilke.setCanDeleteRows(false);
      dsPostneStevilke.setReadOnly(true);
      dsPostneStevilke.setName("postne_stevilke");
      dsPostneStevilke.setShareResults(true);
      parameters = new ArrayList();
      parameters.add(dsPostneStevilkeFilter);
      parameters.add(dsPostneStevilkeFilter);
      dsPostneStevilke.setParameters(parameters);
      dsPostneStevilke.setCountSql(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_pt_c.sql", "cp1250"));
      dsPostneStevilke.setSelectSql(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_pt.sql", "cp1250") + "ORDER BY pt_id");
      dsPostneStevilke.setFetchSize(108);
      //      dsPostneStevilke.setQueuedDelay(54);
      dsPostneStevilkeFilter.addDataSource(dsPostneStevilke);

      dsNaseljaFilter.setFilterRequired(true);
      dsNaseljaFilter.addRequired(dsNaseljaFilter.I_TYPE_UL_IME);
      dsNaseljaFilter.setOperator("and");
      dsNaselja.setCanAddRows(false);
      dsNaselja.setCanDeleteRows(false);
      dsNaselja.setReadOnly(true);
      dsNaselja.setName("naselja");
      dsNaselja.setShareResults(true);
      parameters = new ArrayList();
      parameters.add(dsNaseljaFilter);
      parameters.add(dsNaseljaFilter);
      dsNaselja.setParameters(parameters);
      dsNaselja.setCountSql(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_ns_c.sql", "cp1250"));
      dsNaselja.setSelectSql(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_ns.sql", "cp1250") + "ORDER BY na_ime");
      dsNaselja.setFetchSize(108);
//        dsNaselja.setQueuedDelay(54);
      dsNaseljaFilter.addDataSource(dsNaselja);

      result = true;
    } catch (NullPointerException ex) {
      if (dialect != null && dialect.length() > 0) {
        throw (SQLException) (new SQLException("Napaka pri inicializaciji podatkovnega modela").initCause(ex));
      }
    }

    initDataSources = result;

    return result;
  }

  public void disableFilters(boolean disable) {
    if (initDataSources) {
      dsHisneStevilkeFilter.setDisabled(disable);
      dsUliceFilter.setDisabled(disable);
      dsNaseljaFilter.setDisabled(disable);
      dsPosteFilter.setDisabled(disable);
      dsPostneStevilkeFilter.setDisabled(disable);
    }
  }

  public Naslov getNaslov(int hs_mid) {
    Naslov result = null;

    try {
      if (selectHsMid == null) {
        final String dialect = getDialect();
        final Connection connection = ConnectionManager.getInstance().getConnection();
        selectHsMid = connection.prepareStatement(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "get_from_hs_mid.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      }
      selectHsMid.setInt(1, hs_mid);
      CachedRowSet rsMid = new CachedRowSetImpl();
      rsMid.populate(selectHsMid.executeQuery());
      try {
        if (rsMid.next()) {
          result = new Naslov();

          result.ulicaMID = new FieldValue("ul_mid", java.sql.Types.INTEGER, rsMid.getInt("ul_mid"));
          result.ulica = new FieldValue("ul_uime", java.sql.Types.VARCHAR, rsMid.getString("ul_uime"));
          result.postnaStevilkaMID = new FieldValue("pt_mid", java.sql.Types.INTEGER, rsMid.getInt("pt_mid"));
          result.postnaStevilka = new FieldValue("pt_id", java.sql.Types.INTEGER, rsMid.getInt("pt_id"));
          result.posta = new FieldValue("pt_uime", java.sql.Types.VARCHAR, rsMid.getString("pt_uime"));
          result.naseljeMID = new FieldValue("na_mid", java.sql.Types.INTEGER, rsMid.getInt("na_mid"));
          result.naselje = new FieldValue("na_uime", java.sql.Types.VARCHAR, rsMid.getString("na_uime"));
          result.hisnaStevilka = new FieldValue("hs", java.sql.Types.INTEGER, rsMid.getInt("hs"));
          result.hisnaStevilkaDodatek = new FieldValue("hd", java.sql.Types.VARCHAR, rsMid.getString("hd") == null ? null : rsMid.getString("hd").trim());
        }
      } finally {
        rsMid.close();
      }
    } catch (SQLException ex) {
      Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  public Object[] getMids(int hs_mid) {
    Object[] result = null;
    try {
      if (selectHsMid == null) {
        final String dialect = getDialect();
        final Connection connection = ConnectionManager.getInstance().getConnection();
        selectHsMid = connection.prepareStatement(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "get_from_hs_mid.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      }
      selectHsMid.setInt(1, hs_mid);
      CachedRowSet rsMid = new CachedRowSetImpl();
      rsMid.populate(selectHsMid.executeQuery());
      try {
        if (rsMid.next()) {
          result = new Object[]{rsMid.getInt("ul_mid"), rsMid.getString("ul_uime"), rsMid.getInt("pt_mid"), rsMid.getInt("pt_id"), rsMid.getString("pt_uime"), rsMid.getInt("na_mid"), rsMid.getString("na_uime")};
        }
      } finally {
        rsMid.close();
      }
    } catch (SQLException ex) {
      Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  public int getHisnaStevilkaMID(String ul_ime, String hs_hd, String pt_id, String na_ime) {
    int result = -1;
    ul_ime = ul_ime == null ? null : ul_ime.toUpperCase();
    na_ime = na_ime == null ? null : na_ime.toUpperCase();
    hs_hd = hs_hd == null ? null : hs_hd.replaceAll(" ", "");
    pt_id = pt_id == null || pt_id.length() == 0 ? null : pt_id;
    long timer;
    try {
      if (findHS_MID_1 == null) {
        final String dialect = getDialect();
        final Connection connection = ConnectionManager.getInstance().getConnection();
        findHS_MID_1 = connection.prepareStatement(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "find_hs_mid_1.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      }
      if (findHS_MID_2 == null) {
        final String dialect = getDialect();
        final Connection connection = ConnectionManager.getInstance().getConnection();
        findHS_MID_2 = connection.prepareStatement(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "find_hs_mid_2.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      }
      if (findHS_MID_3 == null) {
        final String dialect = getDialect();
        final Connection connection = ConnectionManager.getInstance().getConnection();
        findHS_MID_3 = connection.prepareStatement(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "find_hs_mid_3.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      }

      int param = 1;
      if (pt_id == null) {
        param = 1;
        findHS_MID_1.clearParameters();
        findHS_MID_1.setString(param++, hs_hd);
        findHS_MID_1.setString(param++, ul_ime);
        timer = System.currentTimeMillis();
        ResultSet rsHS_MID = findHS_MID_1.executeQuery();
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("izbiranaslova:findHS_MID_1: " + (System.currentTimeMillis() - timer) + "ms");
        if (rsHS_MID.next() && rsHS_MID.isLast()) {
          result = rsHS_MID.getInt(1);
        }
        rsHS_MID.close();
      }
      if (na_ime == null || na_ime.length() == 0) {
        if (result == -1) {
          param = 1;
          findHS_MID_2.clearParameters();
          findHS_MID_2.setString(param++, hs_hd);
          findHS_MID_2.setString(param++, ul_ime);
          findHS_MID_2.setObject(param++, pt_id, Types.INTEGER);
          timer = System.currentTimeMillis();
          ResultSet rsHS_MID_2 = findHS_MID_2.executeQuery();
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("izbiranaslova:findHS_MID_2: " + (System.currentTimeMillis() - timer) + "ms");
          if (rsHS_MID_2.next() && rsHS_MID_2.isLast()) {
            result = rsHS_MID_2.getInt(1);
          }
          rsHS_MID_2.close();
        }
      }

      if (result == -1) {
        param = 1;
        findHS_MID_3.clearParameters();
        findHS_MID_3.setString(param++, hs_hd);
        findHS_MID_3.setString(param++, ul_ime);
        findHS_MID_3.setObject(param++, pt_id, Types.INTEGER);
        findHS_MID_3.setString(param++, na_ime);
        timer = System.currentTimeMillis();
        ResultSet rsHS_MID_3 = findHS_MID_3.executeQuery();
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("izbiranaslova:findHS_MID_3: " + (System.currentTimeMillis() - timer) + "ms");
        if (rsHS_MID_3.next() && rsHS_MID_3.isLast()) {
          result = rsHS_MID_3.getInt(1);
        }
        rsHS_MID_3.close();
      }
    } catch (SQLException ex) {
      Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  public int getUlicaMID(String ul_ime, String pt_id, String na_ime) {
    int result = -1;
    long timer;
    ul_ime = ul_ime == null ? null : ul_ime.toUpperCase();
    na_ime = na_ime == null ? null : na_ime.toUpperCase();
    pt_id = pt_id == null || pt_id.length() == 0 ? null : pt_id;
    try {
      int param = 1;
      if (findUL_MID == null) {
        final String dialect = getDialect();
        final Connection connection = ConnectionManager.getInstance().getConnection();
        findUL_MID = connection.prepareStatement(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "find_ul_mid.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      }
      findUL_MID.clearParameters();
      findUL_MID.setString(param++, ul_ime);
      findUL_MID.setObject(param++, pt_id, Types.INTEGER);
      findUL_MID.setString(param++, na_ime);
      timer = System.currentTimeMillis();
      ResultSet rsUL_MID = findUL_MID.executeQuery();
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("izbiranaslova:findUL_MID: " + (System.currentTimeMillis() - timer) + "ms");
      if (rsUL_MID.next()) {
        result = rsUL_MID.getInt(1);
      }
    } catch (SQLException ex) {
      Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  public int getPostnaStevilkaMID(String pt_id, String pt_ime) {
    int result = -1;
    long timer;
    pt_id = pt_id == null || pt_id.length() == 0 ? null : pt_id;
    pt_ime = pt_ime == null ? null : pt_ime.toUpperCase();
    try {
      if (findPT_MID == null) {
        final String dialect = getDialect();
        final Connection connection = ConnectionManager.getInstance().getConnection();
        findPT_MID = connection.prepareStatement(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "find_pt_mid.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      }
      int param = 1;
      findPT_MID.clearParameters();
      findPT_MID.setObject(param++, pt_id, Types.INTEGER);
      findPT_MID.setString(param++, pt_ime);
      timer = System.currentTimeMillis();
      ResultSet rsPT_MID = findPT_MID.executeQuery();
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("izbiranaslova:findPT_MID: " + (System.currentTimeMillis() - timer) + "ms");
      if (rsPT_MID.next() && rsPT_MID.isLast()) {
        result = rsPT_MID.getInt(1);
      }
    } catch (SQLException ex) {
      Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  public int getNaseljeMID(String pt_id, String na_ime) {
    int result = -1;
    long timer;
    pt_id = pt_id == null || pt_id.length() == 0 ? null : pt_id;
    na_ime = na_ime == null ? null : na_ime.toUpperCase();
    try {
      if (findNA_MID == null) {
        final String dialect = getDialect();
        final Connection connection = ConnectionManager.getInstance().getConnection();
        findNA_MID = connection.prepareStatement(ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "find_na_mid.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      }
      int param = 1;
      findNA_MID.clearParameters();
      findNA_MID.setObject(param++, pt_id, Types.INTEGER);
      findNA_MID.setString(param++, na_ime);
      timer = System.currentTimeMillis();
      ResultSet rsNA_MID = findNA_MID.executeQuery();
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("izbiranaslova:findNA_MID: " + (System.currentTimeMillis() - timer) + "ms");
      if (rsNA_MID.next() && rsNA_MID.isLast()) {
        result = rsNA_MID.getInt(1);
      }
    } catch (SQLException ex) {
      Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  protected static class DsFilterRPE extends DataSourceFilters {

    public final SeekType I_TYPE_UL_IME = new SeekType("ul_ime", 8, 2);
    public final SeekType I_TYPE_NA_IME = new SeekType("na_ime", 8, 1);
    public final SeekType I_TYPE_PT_IME = new SeekType("pt_ime", 8, 1);
    public final SeekType I_TYPE_PT_ID = DbNaslovDataModel.getDialect().length() == 0 ? new SeekType("CAST(pt_id as varchar) like (?+\'%\')", 7, 1) : new SeekType(ReadInputStream.getResourceAsString(getClass(), "sql/" + DbNaslovDataModel.getDialect() + "fragment_filter_pt_id.sql", "cp1250"), 7, 1);
    public final IntegerSeekType I_TYPE_HS = new IntegerSeekType("hs", 4);
    public final SeekType I_TYPE_HD = new SeekType("hd", 4, 1);

    public DsFilterRPE(String replace) {
      super(replace);
    }
  }

  public static class Naslov {

    final static Pattern pattern = Pattern.compile("(^(\\d*)(\\s*)(.*)$)");
    protected DbDataSource dataSource;
    protected FieldValue hs_mid;
    protected Long hs_neznana_mid;
    protected FieldValue ulica;
    protected FieldValue hisnaStevilka;
    protected FieldValue hisnaStevilkaDodatek;
    protected FieldValue postnaStevilka;
    protected FieldValue posta;
    protected FieldValue naselje;
    protected FieldValue ulicaMID;
    protected FieldValue postnaStevilkaMID;
    protected FieldValue naseljeMID;
    protected Maps.Location location;
    protected int izvor;

    /**
     * Get the value of izvor
     *
     * @return the value of izvor
     */
    public int getIzvor() {
      return izvor;
    }

    /**
     * Set the value of izvor
     *
     * @param izvor new value of izvor
     */
    public void setIzvor(int izvor) {
      this.izvor = izvor;
    }

    /**
     * Get the value of dataSource
     *
     * @return the value of dataSource
     */
    public DbDataSource getDataSource() {
      return dataSource;
    }

    /**
     * Get the value of hisnaStevilkaMID
     *
     * @return the value of hisnaStevilkaMID
     */
    public FieldValue getHsMID() {
      return hs_mid;
    }

    /**
     * Get the value of hs_neznana_mid
     *
     * @return the value of hs_neznana_mid
     */
    public Long getHsNeznanaMID() {
      return hs_neznana_mid;
    }

    public void setHsNeznanaMID(Long hs_neznana_mid) {
      this.hs_neznana_mid = hs_neznana_mid;
    }

    public boolean isHsNeznana() {
      return hs_neznana_mid != null;
    }

    /**
     * Get the value of ulica
     *
     * @return the value of ulica
     */
    public FieldValue getUlica() {
      return ulica;
    }

    /**
     * Get the value of hisnaStevilka
     *
     * @return the value of hisnaStevilka
     */
    public FieldValue getHisnaStevilka() {
      return hisnaStevilka;
    }

    /**
     * Get the value of postnaStevilka
     *
     * @return the value of postnaStevilka
     */
    public FieldValue getPostnaStevilka() {
      return postnaStevilka;
    }

    /**
     * Get the value of posta
     *
     * @return the value of posta
     */
    public FieldValue getPosta() {
      return posta;
    }

    /**
     * Get the value of naselje
     *
     * @return the value of naselje
     */
    public FieldValue getNaselje() {
      return naselje;
    }

    /**
     * Get the value of ulicaMID
     *
     * @return the value of ulicaMID
     */
    public FieldValue getUlicaMID() {
      return ulicaMID;
    }

    /**
     * Get the value of postnaStevilkaMID
     *
     * @return the value of postnaStevilkaMID
     */
    public FieldValue getPostnaStevilkaMID() {
      return postnaStevilkaMID;
    }

    /**
     * Get the value of naseljeMID
     *
     * @return the value of naseljeMID
     */
    public FieldValue getNaseljeMID() {
      return naseljeMID;
    }

    /**
     * Get the value of hisnaStevilkaDodatek
     *
     * @return the value of hisnaStevilkaDodatek
     */
    public FieldValue getHisnaStevilkaDodatek() {
      return hisnaStevilkaDodatek;
    }

    public Location getLocation() {
      if (location==null) {
        Maps instance = Maps.getInstance();
        
        location = instance.getLocation(toString(getUlica()), 
                                        toString(getHisnaStevilka()),
                                        toString(getHisnaStevilkaDodatek()), 
                                        toString(getPostnaStevilka()), 
                                        toString(getPosta()));
      }
      return location;
    }
    
    private String toString(FieldValue fv) {
      return fv==null||fv.getValue()==null?null:fv.getValue().toString();
    }
    
    

    public void setDataSource(DbDataSource dataSource) {
      this.dataSource = dataSource;
    }

    public void setHisnaStevilka(FieldValue hisnaStevilka) {
      this.hisnaStevilka = hisnaStevilka;
    }

    public void setHisnaStevilkaDodatek(FieldValue hisnaStevilkaDodatek) {
      this.hisnaStevilkaDodatek = hisnaStevilkaDodatek;
    }

    public void setHs_mid(FieldValue hs_mid) {
      this.hs_mid = hs_mid;
    }

    public void setHs_neznana_mid(Long hs_neznana_mid) {
      this.hs_neznana_mid = hs_neznana_mid;
    }

    public void setNaselje(FieldValue naselje) {
      this.naselje = naselje;
    }

    public void setNaseljeMID(FieldValue naseljeMID) {
      this.naseljeMID = naseljeMID;
    }

    public void setPosta(FieldValue posta) {
      this.posta = posta;
    }

    public void setPostnaStevilka(FieldValue postnaStevilka) {
      this.postnaStevilka = postnaStevilka;
    }

    public void setPostnaStevilkaMID(FieldValue postnaStevilkaMID) {
      this.postnaStevilkaMID = postnaStevilkaMID;
    }

    public void setUlica(FieldValue ulica) {
      this.ulica = ulica;
    }

    public void setUlicaMID(FieldValue ulicaMID) {
      this.ulicaMID = ulicaMID;
    }

    public static FieldValue[] splitHS_HD(Object hshd, FieldValue hs, FieldValue hd) {
      String text = hshd != null ? hshd.toString() : null;

      if (hs == null) {
        hs = new FieldValue("HS", java.sql.Types.INTEGER);
      }
      if (hd == null) {
        hd = new FieldValue("HD", java.sql.Types.VARCHAR);
      }
      Object hs_v = hs.getValue();
      Object hd_v = hd.getValue();

      if ((text != null) && (text.length() > 0)) {
        text = text.trim();
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
          if (matcher.group(2) != null && matcher.group(2).length() > 0) {
            hs_v = Integer.parseInt(matcher.group(2));
          }
          if (matcher.group(4) != null && matcher.group(4).length() > 0) {
            hd_v = matcher.group(4);
          }
        }

        hs.setValue(hs_v);
        hd.setValue(hd_v);
      }

      return new FieldValue[]{hs, hd};
    }
  }
}
