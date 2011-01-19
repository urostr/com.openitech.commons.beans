UPDATE
    <%ChangeLog%>.[dbo].[EventsPKVersions]
    SET
    
    [IdSifranta] = ?,
    [IdSifre]    = ?,
    [PrimaryKey] = ?
WHERE
    EventId = ?
   AND ([VersionID] = ? OR (1=? AND VersionID is null))