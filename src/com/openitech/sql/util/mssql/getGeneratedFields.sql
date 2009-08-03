SELECT
    SifrantiPolja.[Id],
    SifrantiPolja.[IdSifranta],
    SifrantiPolja.[IdSifre],
    SifrantiPolja.[Opis],
    SifrantiPolja.[TabName],
    CountTabNames.StTabNames,
    SifrantiPolja.[ZapSt],
    SifrantiPolja.[NewLine],
    SifrantiPolja.[IdPolja],
    SifrantiPolja.[Hidden],
    SifrantiPolja.[Potrebno],
    SifrantiPolja.[ShowInTable],
    SifrantiPolja.[PrimaryKey],
    SifrantiPolja.[validFrom],
    SifrantiPolja.[validTo],
    SifrantVnosnihPolj.[ImePolja],
    SifrantVnosnihPolj.[Opis],
    CAST(CASE WHEN SifrantiPolja.[PrimaryKey]=1 THEN 0 ELSE SifrantVnosnihPolj.[VecVrednosti] END AS BIT) AS [VecVrednosti],
    SifrantVnosnihPolj.[TipPolja],
    SifrantVnosnihPolj.[DolzinaPolja],
    SifrantVnosnihPolj.[StDecimalnihMestPolja],
    SifrantVnosnihPolj.[BrezSifranta],
    SifrantVnosnihPolj.[UporabiSifrantIdSifranta],
    SeznamSifrantov.Skupina as UporabiSifrantSkupina,
    SeznamSifrantov.Opis as UporabiSifrantOpis,
    SifrantVnosnihPolj.[UporabiSifrantPP],
    SifrantVnosnihPolj.[UporabiSifrantSvetovalcev],
    SifrantVnosnihPolj.[UporabiSifrantPodjetji],
    SifrantVnosnihPolj.[UporabiSifrantPonudb],
    SifrantVnosnihPolj.[UporabiSifrantXML],
    SifrantVnosnihPolj.[Opombe],
    SifrantVnosnihPolj.[validFrom],
    SifrantVnosnihPolj.[validTo]
FROM
    [ChangeLog].[dbo].[SifrantiPolja] as SifrantiPolja
LEFT OUTER JOIN
    [ChangeLog].[dbo].SifrantVnosnihPolj as SifrantVnosnihPolj
    ON
    SifrantVnosnihPolj.Id = SifrantiPolja.IdPolja
LEFT OUTER JOIN
    [ChangeLog].[dbo].SeznamSifrantov
    ON
    SeznamSifrantov.Id = SifrantVnosnihPolj.[UporabiSifrantIdSifranta]
LEFT OUTER JOIN (SELECT
    SifrantiPolja.[IdSifranta],
    SifrantiPolja.[IdSifre],
    SifrantiPolja.[TabName],
    MIN(SifrantiPolja.[ZapSt]) AS MinZapSt,
    COUNT(*) AS StTabNames
    FROM [ChangeLog].[dbo].[SifrantiPolja] GROUP BY SifrantiPolja.[IdSifranta],
    SifrantiPolja.[IdSifre], SifrantiPolja.[TabName]) AS CountTabNames ON (
    CountTabNames.[IdSifranta] = SifrantiPolja.[IdSifranta] AND
    CountTabNames.[IdSifre] = SifrantiPolja.[IdSifre] AND
    CountTabNames.[TabName] = SifrantiPolja.[TabName])
WHERE
    SifrantiPolja.IdSifranta = ?
    AND (1=? OR SifrantiPolja.IdSifre= ?)
ORDER BY
    CountTabNames.MinZapSt, SifrantiPolja.ZapSt, SifrantiPolja.[Id]