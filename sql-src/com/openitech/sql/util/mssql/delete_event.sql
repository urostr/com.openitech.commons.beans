UPDATE
    <%ChangeLog%>.[dbo].[Events] 
    SET

    [valid]       = 0,
    [validTo]     = GETDATE(),
    [ChangedBy]   = SYSTEM_USER,
    [ChangedOn]   = GETDATE()
WHERE
    [Id] = ?