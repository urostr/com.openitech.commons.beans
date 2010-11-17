UPDATE
    <%ChangeLog%>.[dbo].[EventsPKVersions]
    SET
    [VersionID] = ?,
    [IdSifranta] = ?,
    [IdSifre]    = ?,
    [PrimaryKey] = ?
WHERE
    EventId = ?