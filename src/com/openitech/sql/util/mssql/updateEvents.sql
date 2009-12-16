UPDATE
    <%ChangeLog%>.[dbo].[Events]
    SET
    [IdSifranta]  = ?,
    [IdSifre]     = ?,
    [IdEventSource] = ?,
    [Datum]       = ?,
    [Opomba]      = ?
WHERE
    [Id] = ?