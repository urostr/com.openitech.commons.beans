SELECT
    [Id],
    [EventId],
    [IdSifranta],
    [IdSifre],
    [PrimaryKey]
FROM
    <%ChangeLog%>.[dbo].[EventsPK]
WHERE
      [IdSifranta] = ?
AND   [IdSifre]    = ?
AND   [PrimaryKey] = ?