SELECT
    [EventId],
    [IdPolja],
    [FieldValueIndex],
    [VersionId],
    [IdSifranta],
    [IdSifre],
    [PrimaryKey]
FROM
    <%ChangeLog%>.[dbo].[EventLookupKeys]
WHERE EventId = ?
  AND IdPolja = ?
  AND FieldValueIndex = ?