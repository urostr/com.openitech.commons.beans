SELECT <%ev_result_limit%>
    ev.[Id],
    ev.[Id] as [EventId],
    ev.[IdSifranta],
    ev.[IdSifre],
    ev.[IdEventSource],
    ev.Datum
<%ev_field_results%>
FROM (
    (SELECT ev.*,
            CAST(<%ev_version_filter%> as int) as VersionId
     FROM
        <%ChangeLog%>.[dbo].[Events] ev
     WHERE ev.id in (select eventid from EventVersions where versionid = <%ev_version_filter%>) AND <%ev_type_filter%> <%ev_source_filter%> <%ev_date_filter%>)
    UNION ALL
    (SELECT ev.*,
           (null) as VersionId
     FROM
        <%ChangeLog%>.[dbo].[Events] ev
     WHERE <%ev_version_filter%> IS NULL AND <%ev_type_filter%> <%ev_valid_filter%> <%ev_source_filter%> <%ev_date_filter%>)
    ) ev
<%ev_values_filter%>
<%ev_valid_filter%>
