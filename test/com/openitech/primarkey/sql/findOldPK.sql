SELECT
    [Id],
    [EventId],
    [IdSifranta],
    [IdSifre],
    [PrimaryKey]
FROM
    <%ChangeLog%>.[dbo].[EventsPK] WITH (NOLOCK)
WHERE PrimaryKey = ?