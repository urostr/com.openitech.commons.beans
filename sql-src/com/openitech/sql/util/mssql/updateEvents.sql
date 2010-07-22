UPDATE
    <%ChangeLog%>.[dbo].[Events] WITH (ROWLOCK)
    SET
    [IdSifranta]  = ?,
    [IdSifre]     = ?,
    [IdEventSource] = ?,
    [Datum]       = ?,
    [Opomba]      = ?
WHERE
    [Id] = ?