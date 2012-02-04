SELECT *
FROM
(SELECT E_51.*,
       (SELECT ID_PP FROM MViewCache.dbo.[CACHE:E_85_FMD01] E_85_FMD01 WHERE E_85_FMD01.ID_AKCIJE_PP = E_51.[ID_AKCIJE_PP]) AS E_85_ID_PP

FROM (SELECT
    ev.[Id],
    ev.[Id] as [EventId],
    ev.[IdSifranta],
    ev.[IdSifre],
    ev.[IdEventSource],
    ev.[VersionId],
    ev.[Version],
    ev.[Datum],
    ev.[DatumSpremembe],
(SELECT [VariousValues].StringValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 145 AND [EventValues].[FieldValueIndex] = 1)) AS [ID_AKCIJE],
(SELECT [VariousValues].StringValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 173 AND [EventValues].[FieldValueIndex] = 1)) AS [ID_AKCIJE_PP],
(SELECT [VariousValues].StringValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 174 AND [EventValues].[FieldValueIndex] = 1)) AS [ID_KLICA],
(SELECT [VariousValues].DateValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 106 AND [EventValues].[FieldValueIndex] = 1)) AS [DATUM_KLICA],
(SELECT [VariousValues].DateValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 175 AND [EventValues].[FieldValueIndex] = 1)) AS [DATUM_ÈAS_KLICA],
(SELECT [VariousValues].StringValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 311 AND [EventValues].[FieldValueIndex] = 1)) AS [VRSTA_KOMUNIKACIJE],
(SELECT [VariousValues].IntValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 45 AND [EventValues].[FieldValueIndex] = 1)) AS [ID_PP],
(SELECT [VariousValues].IntValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 1 AND [EventValues].[FieldValueIndex] = 1)) AS [ID_PONUDBE],
(SELECT [VariousValues].IntValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 22 AND [EventValues].[FieldValueIndex] = 1)) AS [ID_BONA]
FROM (SELECT ev.*, --non-versioned secondary
     (null) as VersionId
FROM
  [ChangeLog].[dbo].[Events] ev WITH (NOLOCK)
WHERE ev.[IdSifranta] = 51
AND EXISTS (SELECT [VariousValues].StringValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].StringValue = 'FMD01' AND [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 145 AND [EventValues].[FieldValueIndex] = 1))  ) ev
) E_51
WHERE ID_PP IS NULL) E_51_85
WHERE E_85_ID_PP IS NOT NULL