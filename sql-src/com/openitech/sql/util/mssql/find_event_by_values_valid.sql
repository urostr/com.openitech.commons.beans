SELECT ev.*, --non-versioned secondary
     (null) as VersionId
FROM
  <%ChangeLog%>.[dbo].[Events] ev
WHERE <%ev_type_filter%> <%ev_valid_filter%> <%ev_source_filter%> <%ev_date_filter%>
