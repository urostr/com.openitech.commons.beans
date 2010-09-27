SELECT
    [Id],
    [IdSifranta],
    [IdSifre]
FROM
    [dbo].[Events] WITH (NOLOCK)
WHERE
    valid = 1 AND validTo is null AND Id > 1154892