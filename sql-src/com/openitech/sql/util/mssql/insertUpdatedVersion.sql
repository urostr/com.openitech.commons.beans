INSERT
INTO
    <%ChangeLog%>.[dbo].[Versions]
    (
        [IdSifranta],
        [IdSifre],
        [Datum]
    )
    (
    SELECT
        [IdSifranta],
        [IdSifre],
        GETDATE()
    FROM
        <%ChangeLog%>.[dbo].[Versions]
    WHERE
        id = ?
    )