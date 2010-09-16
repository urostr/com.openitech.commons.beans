SELECT
    [Id],
    [EventId],
    [IdSifranta],
    [IdSifre],
    [PrimaryKey]
FROM
    <%ChangeLog%>.[dbo].[EventsPK]
WHERE
    EventId        = ?
    AND IdSifranta = ?
    AND IdSifre    = ?
    AND PrimaryKey = ?