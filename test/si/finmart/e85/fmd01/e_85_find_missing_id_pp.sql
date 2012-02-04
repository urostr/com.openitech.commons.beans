SELECT E_85.*,
       ppsifre.ppid
FROM
(SELECT
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
(SELECT [VariousValues].DateValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 131 AND [EventValues].[FieldValueIndex] = 1)) AS [DATUM_AKCIJE],
(SELECT [VariousValues].IntValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 22 AND [EventValues].[FieldValueIndex] = 1)) AS [ID_BONA],
(SELECT [VariousValues].StringValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 311 AND [EventValues].[FieldValueIndex] = 1)) AS [VRSTA_KOMUNIKACIJE],
(SELECT [VariousValues].StringValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 172 AND [EventValues].[FieldValueIndex] = 1)) AS [REZULTAT_KOMUNIKACIJE],
[val_ID_PP].IntValue AS [ID_PP],
(SELECT [VariousValues].IntValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].[Id] = (SELECT [ValueId] FROM [ChangeLog].[dbo].[EventValues] WITH (NOLOCK) WHERE [EventValues].[EventId] = ev.[Id] AND [EventValues].[IdPolja] = 1 AND [EventValues].[FieldValueIndex] = 1)) AS [ID_PONUDBE]
FROM (SELECT ev.*, --non-versioned secondary
     (null) as VersionId
FROM
  [ChangeLog].[dbo].[Events] ev WITH (NOLOCK)
WHERE ev.[IdSifranta] = 85 AND ev.[IdSifre] = 'FMD01'  ) ev

LEFT OUTER JOIN [ChangeLog].[dbo].[EventValues] [ev_ID_PP] WITH (NOLOCK) ON (ev.[Id] = [ev_ID_PP].[EventId] AND [ev_ID_PP].[IdPolja] = 45 AND [ev_ID_PP].[FieldValueIndex] = 1 )
LEFT OUTER JOIN [ChangeLog].[dbo].[VariousValues] [val_ID_PP] WITH (NOLOCK) ON ([ev_ID_PP].[ValueId] = [val_ID_PP].[Id] )) E_85
INNER JOIN rpp.dbo.PPSifre
ON Customer_ID_Sifra = (SELECT Customer_ID FROM InsOffers WHERE InsOffers.InsOffer_ID = [ID_PONUDBE] )
where id_pp is null
--[1]=85
--[2]=FMD01