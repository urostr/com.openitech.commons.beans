SELECT
    MAX([VersionId]) AS Last_VersionId
FROM
    <%ChangeLog%>.[dbo].[EventVersions]
WHERE EventId = ?