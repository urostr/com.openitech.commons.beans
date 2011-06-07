SELECT
 (SELECT [VariousValues].StringValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 515 AND [EventValues].[FieldValueIndex] = 1)) AS [ID_KONTAKTA]
FROM (SELECT ev.*, --non-versioned secondary
     (null) as VersionId
FROM
  [ChangeLog].[dbo].[Events] ev WITH (NOLOCK)
WHERE ev.[IdSifranta] = 243 AND ev.[IdSifre] = 'KTEL01'  AND ev.valid = 1    ) ev

WHERE
EXISTS (SELECT [VariousValues].Id FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 445 AND [EventValues].[FieldValueIndex] = 1)AND [VariousValues].IntValue = ? )
AND
EXISTS (SELECT [VariousValues].Id FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 446 AND [EventValues].[FieldValueIndex] = 1)AND [VariousValues].IntValue = ? )
AND
EXISTS (SELECT [VariousValues].Id FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 532 AND [EventValues].[FieldValueIndex] = 1)AND [VariousValues].StringValue = CAST(? AS VARCHAR) )

