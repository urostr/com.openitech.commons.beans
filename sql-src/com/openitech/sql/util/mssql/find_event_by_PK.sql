SELECT
    [EventId]
FROM
    <%ChangeLog%>.[dbo].[EventsPK]
WHERE
        [IdSifranta] = ?
    AND [IdSifre] = CAST(? AS VARCHAR)
    AND [PrimaryKey] = CAST(? AS VARCHAR(700))