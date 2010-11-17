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

    AND IdSifranta        = ?
    AND IdSifre           = ?
    AND PrimaryKey        = ?