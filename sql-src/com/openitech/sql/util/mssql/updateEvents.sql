UPDATE
    <%ChangeLog%>.[dbo].[Events] WITH (ROWLOCK)
    SET
    [IdSifranta]  = ?,
    [IdSifre]     = ?,
    [IdEventSource] = ?,
    [Datum]       = ?,
    [Opomba]      = ?,
    [valid]       = ?,
    [validTo]     = ?,
    [ChangedBy]   = SESSION_USER,
    [ChangedOn]   = GETDATE()
WHERE
    [Id] = ?