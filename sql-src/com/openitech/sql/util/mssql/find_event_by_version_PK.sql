SELECT
    [EventId]
FROM
    <%ChangeLog%>.[dbo].[EventsPKVersions]
WHERE
    [IdSifranta] = <%ev_sifrant%> AND
    [IdSifre] = <%ev_sifra%> AND
    [VersionId] = <%ev_version_filter%> AND
    [PrimaryKey] = ?