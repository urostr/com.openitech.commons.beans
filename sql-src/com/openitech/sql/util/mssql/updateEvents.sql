UPDATE
    <%ChangeLog%>.[dbo].[Events] WITH (ROWLOCK)
    SET
    [IdSifranta]  = ?,
    [IdSifre]     = ?,
    [IdEventSource] = ?,
    [Datum]       = ?,
    [Opomba]      = ?,
    [validFrom]   = ?,
    [valid]       = ?,
    [validTo]     = ?,
    [ChangedBy]   = SYSTEM_USER,
    [ChangedOn]   = GETDATE()
WHERE
    [Id] = ?