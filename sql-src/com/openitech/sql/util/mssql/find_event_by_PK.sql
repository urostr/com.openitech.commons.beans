SELECT
    [EventId]
FROM
    <%ChangeLog%>.[dbo].[EventsPK]
WHERE
        [IdSifranta] = ?
    AND (1 = ? OR [IdSifre] = CAST(? AS VARCHAR))
    AND [PrimaryKey] = CAST(? AS VARCHAR(700))