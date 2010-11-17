SELECT
    [Id],
    [EventId],
    [VersionID],
    [IdSifranta],
    [IdSifre],
    [PrimaryKey]
FROM
    <%ChangeLog%>.[dbo].[EventsPKVersions]
WHERE
    EventId        = ?
    AND (1=? OR VersionID  = ?)