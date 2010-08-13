UPDATE
    <%ChangeLog%>.[dbo].[Events] WITH (ROWLOCK)
    SET
    [IdSifranta]  = ?,
    [IdSifre]     = ?,
    [IdEventSource] = ?,
    [Datum]       = ?,
    
    [valid]       = ?,
    [validTo]     = ?,
    [ChangedBy]   = SYSTEM_USER,
    [ChangedOn]   = GETDATE()
WHERE
    [Id] = ?