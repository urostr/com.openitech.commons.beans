SELECT DISTINCT
    ev.[Id],
    ev.[IdSifranta],
    ev.[IdSifre],
    ev.[IdEventSource],
    ev.[Datum]
FROM
    [ChangeLog].[dbo].[Events] ev
<%ev_values_filter%>
WHERE ev.[IdSifranta] = ? AND ev.[IdSifre] = ? <%ev_source_filter%> <%ev_date_filter%>