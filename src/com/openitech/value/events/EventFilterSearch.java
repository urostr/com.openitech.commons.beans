/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.value.events;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSource.SqlParameter;
import com.openitech.db.model.sql.SQLDataSource;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.fields.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author domenbasic
 */
public class EventFilterSearch extends EventQueryParameter {

  private static final String EV_VERSIONED_SUBQUERY = SqlUtilities.EV_VERSIONED_SUBQUERY;
  private static final String EV_NONVERSIONED_SUBQUERY = SqlUtilities.EV_NONVERSIONED_SUBQUERY;
  private static final String EV_SEARCH_BY_PK_SUBQUERY = SqlUtilities.EV_SEARCH_BY_PK_SUBQUERY;
  private static final String EV_SEARCH_BY_VERSION_PK_SUBQUERY = SqlUtilities.EV_SEARCH_BY_VERSION_PK_SUBQUERY;
  DbDataSource.SubstSqlParameter sqlFindEventVersion = new DbDataSource.SubstSqlParameter("<%ev_version_filter%>");
  DbDataSource.SubstSqlParameter sqlFindEventType = new DbDataSource.SubstSqlParameter("<%ev_type_filter%>");
  DbDataSource.SubstSqlParameter sqlFindEventValid = new DbDataSource.SubstSqlParameter("<%ev_valid_filter%>");
  DbDataSource.SubstSqlParameter sqlFindEventSource = new DbDataSource.SubstSqlParameter("<%ev_source_filter%>");
  DbDataSource.SubstSqlParameter sqlFindEventDate = new DbDataSource.SubstSqlParameter("<%ev_date_filter%>");
  DbDataSource.SubstSqlParameter sqlFindEventPk = new DbDataSource.SubstSqlParameter("<%ev_pk_filter%>");
  DbDataSource.SubstSqlParameter sqlFindEventVersionPk = new DbDataSource.SubstSqlParameter("<%ev_version_pk_filter%>");
  DbDataSource.SubstSqlParameter sqlEventSifrant = new DbDataSource.SubstSqlParameter("<%ev_sifrant%>");
  DbDataSource.SubstSqlParameter sqlEventSifra = new DbDataSource.SubstSqlParameter("<%ev_sifra%>");
  String evVersionedSubquery;
  String evNonVersionedSubquery;
  List<Object> evNonVersionedParameters = new ArrayList<Object>();
  List<Object> evVersionedParameters = new ArrayList<Object>();
  Integer eventSource;
  java.util.Date eventDatum;
  int[] sifrant;
  String[] sifra;
  boolean validOnly;

  public EventFilterSearch(EventFilterSearch eventFilterSearch, int[] sifrant, String[] sifra) {
    this(eventFilterSearch.namedParameters, eventFilterSearch.eventSource, eventFilterSearch.eventDatum, sifrant, sifra, eventFilterSearch.validOnly, eventFilterSearch.eventPK);
  }

  public EventFilterSearch(Map<Field, DbDataSource.SqlParameter<Object>> namedParameters, Integer eventSource, java.util.Date eventDatum, int sifrant, String[] sifra, boolean validOnly, EventPK eventPK) {
    this(namedParameters, eventSource, eventDatum, new int[]{sifrant}, sifra, validOnly, eventPK);
  }

  public EventFilterSearch(Map<Field, DbDataSource.SqlParameter<Object>> namedParameters, Integer eventSource, java.util.Date eventDatum, int[] sifrant, String[] sifra, boolean validOnly, EventPK eventPK) {
    super(namedParameters);
    this.eventSource = eventSource;
    this.eventDatum = eventDatum;
    this.sifrant = sifrant;
    this.sifra = sifra;
    this.validOnly = validOnly;
    this.eventPK = eventPK;


    final SqlParameter<Integer> qpSifrant = new SqlParameter<Integer>(java.sql.Types.INTEGER, sifrant[0]);

    sqlFindEventVersion.setValue("?");
    sqlFindEventVersion.addParameter(this.versionId);

    sqlFindEventValid.setValue(validOnly ? " AND ev.valid = 1 " : "");
    if (sifra == null) {
      StringBuilder sbSifrant = new StringBuilder();
      for (int i = 0; i < sifrant.length; i++) {
        final SqlParameter<Integer> qpSifrant1 = new SqlParameter<Integer>(java.sql.Types.INTEGER, sifrant[i]);
        sqlFindEventType.addParameter(qpSifrant1);
        sbSifrant.append(sbSifrant.length() > 0 ? "," : "").append(" ? ");
      }
      sbSifrant.insert(0, " (");

      sbSifrant.append(" ) ");
      sbSifrant.insert(0, " ev.[IdSifranta] " + (sifrant.length > 1 ? " IN " : " = "));
      sqlFindEventType.setValue(sbSifrant.toString());

    } else if (sifra.length == 1) {
      if (ConnectionManager.getInstance().isConvertToVarchar()) {
        sqlFindEventType.setValue("ev.[IdSifranta] = ? AND ev.[IdSifre] = CAST(? AS VARCHAR)");
      } else {
        sqlFindEventType.setValue("ev.[IdSifranta] = ? AND ev.[IdSifre] = ?");
      }
      sqlFindEventType.addParameter(qpSifrant);
      sqlFindEventType.addParameter(new SqlParameter<String>(java.sql.Types.VARCHAR, sifra[0]));
    } else {
      StringBuilder sbet = new StringBuilder();
      sqlFindEventType.addParameter(qpSifrant);
      for (String s : sifra) {
        sbet.append(sbet.length() > 0 ? ", " : "");
        if (ConnectionManager.getInstance().isConvertToVarchar()) {
          sbet.append("CAST(? AS VARCHAR)");
        } else {
          sbet.append("?");
        }
        sqlFindEventType.addParameter(new SqlParameter<String>(java.sql.Types.VARCHAR, s));
      }
      sbet.insert(0, "ev.[IdSifranta] = ? AND ev.[IdSifre] IN (").append(") ");
      sqlFindEventType.setValue(sbet.toString());
    }

    sqlEventSifrant.setValue("ev.[IdSifranta]");
    sqlEventSifra.setValue("ev.[IdSifre]");

    if (eventSource == null) {
      sqlFindEventSource.setValue("");
    } else {
      sqlFindEventSource.setValue(" AND ev.[IdEventSource] = ?");
      sqlFindEventSource.addParameter(new SqlParameter<Integer>(java.sql.Types.INTEGER, eventSource));
    }
    if (eventDatum == null) {
      sqlFindEventDate.setValue("");
    } else {
      sqlFindEventDate.setValue(" AND ev.DATUM = ?");
      sqlFindEventDate.addParameter(new SqlParameter<Date>(java.sql.Types.TIMESTAMP, eventDatum));
    }

    //  setEventPK(eventPK);

    evVersionedParameters.add(sqlFindEventVersion);
    evVersionedParameters.add(sqlFindEventVersion);
    evVersionedParameters.add(sqlFindEventType);
    evVersionedParameters.add(sqlFindEventSource);
    evVersionedParameters.add(sqlFindEventDate);
    evVersionedParameters.add(sqlFindEventVersionPk);
    evVersionedSubquery = SQLDataSource.substParameters(EV_VERSIONED_SUBQUERY, evVersionedParameters);

    evNonVersionedParameters.add(sqlFindEventType);
    evNonVersionedParameters.add(sqlFindEventValid);
    evNonVersionedParameters.add(sqlFindEventSource);
    evNonVersionedParameters.add(sqlFindEventDate);
    evNonVersionedParameters.add(sqlFindEventPk);
    evNonVersionedSubquery = SQLDataSource.substParameters(EV_NONVERSIONED_SUBQUERY, evNonVersionedParameters);
  }

  @Override
  public boolean hasVersionId() {
    return versionId.getValue() != null && (((Long) versionId.getValue()) > 0);
  }
  protected EventPK eventPK;

  /**
   * Get the value of eventPK
   *
   * @return the value of eventPK
   */
  protected EventPK getEventPK() {
    return eventPK;
  }

  /**
   * Set the value of eventPK
   *
   * @param eventPK new value of eventPK
   */
  protected void setEventPK(EventPK eventPK) {
    this.eventPK = eventPK;
    if (eventPK == null) {
      sqlFindEventVersion.setValue(" null ");
      sqlFindEventVersion.clearParameters();
      sqlFindEventPk.setValue("");
      sqlFindEventPk.clearParameters();
    } else {
      sqlFindEventPk.setValue("ev.[Id] IN (" + EV_SEARCH_BY_PK_SUBQUERY + ")");
      sqlFindEventPk.addParameter(sqlEventSifrant);
      sqlFindEventPk.addParameter(sqlEventSifra);
      sqlFindEventPk.addParameter(eventPK.getPrimaryKey());

      sqlFindEventVersionPk.setValue("ev.[Id] IN (" + EV_SEARCH_BY_VERSION_PK_SUBQUERY + ")");
      sqlFindEventVersionPk.addParameter(sqlEventSifrant);
      sqlFindEventVersionPk.addParameter(sqlEventSifra);
      sqlFindEventVersionPk.addParameter(sqlFindEventVersion);
      sqlFindEventVersionPk.addParameter(eventPK.getPrimaryKey());
    }
    evVersionedSubquery = SQLDataSource.substParameters(EV_VERSIONED_SUBQUERY, evVersionedParameters);
    evNonVersionedSubquery = SQLDataSource.substParameters(EV_NONVERSIONED_SUBQUERY, evNonVersionedParameters);
  }

  @Override
  public List<Object> getParameters() {
    return Collections.unmodifiableList(hasVersionId() ? evVersionedParameters : evNonVersionedParameters);
  }

  @Override
  public String getValue() {
    return hasVersionId() ? evVersionedSubquery : evNonVersionedSubquery;
  }

  public int[] getSifrant() {
    return sifrant;
  }
}
