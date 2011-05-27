SELECT
    [IdSifranta],
    [IdSifre],
    [CacheOnUpdate],
    [Valid]
FROM
    MViewCache.[dbo].[CACHED:EVENT:OBJECTS]
WHERE IdSifranta = ? AND IdSifre = ?