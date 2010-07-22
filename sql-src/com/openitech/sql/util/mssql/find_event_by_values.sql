SELECT <%ev_result_limit%>
    ev.[Id],
    ev.[Id] as [EventId],
    ev.[IdSifranta],
    ev.[IdSifre],
    ev.[IdEventSource],
    ev.Datum
<%ev_field_results%>
FROM (SELECT ev.* FROM
    <%ChangeLog%>.[dbo].[Events] ev
WHERE <%ev_type_filter%> <%ev_source_filter%> <%ev_date_filter%>) ev
<%ev_values_filter%>
<%ev_valid_filter%>
