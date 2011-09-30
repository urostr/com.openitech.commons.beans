SELECT
    ev.[Id],
    ev.[IdSifranta],
    ev.[IdSifre],
    ev.[IdEventSource],
    ev.[Datum],
    ev.[Opomba],
    ev.[SessionUser],
    eval.[IdPolja],
    eval.[FieldValueIndex],
    eval.[ValueId],
    polje.[ImePolja],
    vval.[FieldType],
    vval.[IntValue],
    vval.[RealValue],
    vval.[StringValue],
    vval.[DateValue],
    vval.[ObjectValue],
    vval.[ClobValue],
    (SELECT [PrimaryKey] FROM <%ChangeLog%>.[dbo].SifrantiPolja WITH (NOLOCK)
     WHERE SifrantiPolja.IdSifranta = ev.[IdSifranta] AND
           SifrantiPolja.IdSifre = ev.IdSifre AND
           SifrantiPolja.IdPolja = eval.[IdPolja] AND
           SifrantiPolja.FieldValueIndex = eval.[FieldValueIndex]) AS [PrimaryKey],
           --ne dotikaj se imen stolpec za lookup
    EventLookupKeys.VersionId AS LOOKUP_VERSIONID_,
    EventLookupKeys.IdSifranta AS LOOKUP_IDSIFRANTA_,
    EventLookupKeys.IdSifre AS LOOKUP_IDSIFRE_,
    EventLookupKeys.PrimaryKey AS LOOKUP_PK_
FROM
    <%ChangeLog%>.[dbo].[Events] ev
INNER JOIN <%ChangeLog%>.[dbo].[EventValues] eval WITH (NOLOCK)
ON
    (
        ev.[Id] = eval.[EventId]
    )
INNER JOIN <%ChangeLog%>.[dbo].[SifrantVnosnihPolj] polje WITH (NOLOCK)
ON
    (
        eval.[IdPolja] = polje.[Id]
    )
LEFT OUTER JOIN <%ChangeLog%>.[dbo].[VariousValues] vval WITH (NOLOCK)
ON
    (
        eval.[ValueId] = vval.[Id]
    )
LEFT OUTER JOIN ChangeLog.[dbo].[EventLookupKeys] WITH (NOLOCK)
ON
    (
        EventLookupKeys.[EventId] = ev.[Id]
        AND EventLookupKeys.IdPolja = polje.Id
        AND EventLookupKeys.FieldValueIndex = eval.[FieldValueIndex]
    )
WHERE ev.[Id] = ?