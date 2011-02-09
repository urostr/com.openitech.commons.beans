UPDATE
    <%ChangeLog%>.[dbo].[EventsPKVersions]
    SET    
    [EventId] = ?
WHERE
    (1=? OR VersionID  = ?)

    AND IdSifranta        = ?
    AND IdSifre           = ?
    AND PrimaryKey        = ?