SELECT ev.*, --non-versioned secondary
     (null) as VersionId
FROM
  <%ev_table%>
<%ev_where%> <%ev_type_filter%> <%ev_valid_filter%> <%ev_source_filter%> <%ev_date_filter%> <%ev_pk_filter%>
