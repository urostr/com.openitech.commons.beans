SELECT
    [EventId]
FROM
    <%ChangeLog%>.[dbo].[EventsPK]
WHERE
    [IdSifranta] = <%ev_sifrant%> AND
    [IdSifre] = <%ev_sifra%> AND
    [PrimaryKey] = ?