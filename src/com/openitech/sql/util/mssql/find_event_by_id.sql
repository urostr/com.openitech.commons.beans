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
    vval.[ClobValue]
FROM
    <%ChangeLog%>.[dbo].[Events] ev
INNER JOIN <%ChangeLog%>.[dbo].[EventValues] eval
ON
    (
        ev.[Id] = eval.[EventId]
    )
INNER JOIN <%ChangeLog%>.[dbo].[SifrantVnosnihPolj] polje
ON
    (
        eval.[IdPolja] = polje.[Id]
    )
LEFT OUTER JOIN <%ChangeLog%>.[dbo].[VariousValues] vval
ON
    (
        eval.[ValueId] = vval.[Id]
    )
WHERE ev.[Id] = ?