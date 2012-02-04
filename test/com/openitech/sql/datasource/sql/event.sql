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

WHERE
EXISTS (SELECT [VariousValues].Id FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK)
WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 580 AND [EventValues].[FieldValueIndex] = 1)
  AND [VariousValues].StringValue = 'AAA000000022' )
