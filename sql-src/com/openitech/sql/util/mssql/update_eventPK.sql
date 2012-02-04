UPDATE
    <%ChangeLog%>.[dbo].[EventsPK]
    SET
    [IdSifranta] = ?,
    [IdSifre]    = ?,
    [PrimaryKey] = ?
WHERE
    EventId = ?