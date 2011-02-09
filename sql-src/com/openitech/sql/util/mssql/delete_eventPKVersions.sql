DELETE
FROM
    <%ChangeLog%>.[dbo].[EventsPKVersions]
WHERE
    EventId = ? AND
    VersionId IS NULL