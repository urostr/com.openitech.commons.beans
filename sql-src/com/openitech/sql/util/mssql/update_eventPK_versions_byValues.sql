UPDATE
    <%ChangeLog%>.[dbo].[EventsPKVersions]
    SET    
    [EventId] = ?
WHERE
    (1=? OR VersionID  = ?)
    AND (1=? OR VersionID IS NULL)
    AND IdSifranta        = ?
    AND IdSifre           = ?
    AND PrimaryKey        = ?