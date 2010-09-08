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
           SifrantiPolja.FieldValueIndex = eval.[FieldValueIndex]) AS [PrimaryKey]
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
WHERE ev.[Id] = ?