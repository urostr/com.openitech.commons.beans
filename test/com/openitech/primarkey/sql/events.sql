SELECT
    [Id],
    [IdSifranta],
    [IdSifre]
FROM
    [dbo].[Events] WITH (NOLOCK)
WHERE
    valid = 1 AND validTo is null AND idSifranta = 66 --AND Id > ? AND id < ? --AND Id > 1100892 AND id < 1140892