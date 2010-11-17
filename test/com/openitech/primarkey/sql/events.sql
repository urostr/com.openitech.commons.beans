SELECT
    [Id],
    [IdSifranta],
    [IdSifre],
    (SELECT
            MAX([VersionId])
        FROM
            [dbo].[EventVersions]
        where EventId = Id
    ) AS versionid
FROM
    [dbo].[Events] WITH (NOLOCK)

WHERE
    valid = 1 AND validTo is null AND idSifranta = 72 --AND Id > ? AND id < ? --AND Id > 1100892 AND id < 1140892