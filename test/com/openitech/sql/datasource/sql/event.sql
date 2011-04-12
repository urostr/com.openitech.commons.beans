SELECT 
    ev.[Id],
    ev.[Id] as [EventId],
    ev.[IdSifranta],
    ev.[IdSifre],
    ev.[IdEventSource],
    ev.[VersionId],
    ev.[Version],
    ev.[Datum],
    ev.[DatumSpremembe]
FROM (<%ev_events_subquery%>) ev

