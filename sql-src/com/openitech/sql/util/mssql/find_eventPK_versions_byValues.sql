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
    (1=? OR VersionID  = ?)
    AND (1=? OR VersionID IS NULL)
    AND IdSifranta        = ?
    AND IdSifre           = ?
    AND PrimaryKey        = ?