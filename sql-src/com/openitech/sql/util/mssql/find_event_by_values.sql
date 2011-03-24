SELECT <%ev_result_limit%>
    ev.[Id],
    ev.[Id] as [EventId],
    ev.[IdSifranta],
    ev.[IdSifre],
    ev.[IdEventSource],
    ev.[VersionId],
    ev.[Version],
    ev.[Datum],
    ev.[DatumSpremembe]<%ev_field_results%>
FROM (<%ev_events_subquery%>) ev
<%ev_values_filter%>
<%ev_valid_filter%>
