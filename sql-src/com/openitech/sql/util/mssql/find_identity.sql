SELECT
    ev.[Id],
    ev.[Id] AS EventId,
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
    polje.TipPolja,
    (SELECT <%ValueType%> FROM ChangeLog.[dbo].[VariousValues] vval WHERE vval.Id = eval.[ValueId]) AS FieldValue

FROM
    ChangeLog.[dbo].[Events] ev
INNER JOIN ChangeLog.[dbo].[EventValues] eval WITH (NOLOCK)
ON
    (
        ev.[Id] = eval.[EventId]
    )
INNER JOIN ChangeLog.[dbo].[SifrantVnosnihPolj] polje WITH (NOLOCK)
ON
    (
        eval.[IdPolja] = polje.[Id] AND polje.ImePolja = CAST(? AS VARCHAR)
    )

WHERE ev.IdSifranta = 0 AND ev.IdSifre = 'ID01'
