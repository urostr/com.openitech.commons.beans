SELECT
    [Id],
    [IdSifranta],
    [IdSifre],
    EventVersions.EventId
FROM
    ChangeLog.[dbo].[Versions]
LEFT OUTER JOIN
    ChangeLog.[dbo].EventVersions
    ON
    EventVersions.VersionId = Versions.Id
WHERE EventVersions.EventId IN (<%eventIds%>)

