SELECT TOP 100 PERCENT
     PPPolja.Id,
     PPPolja.[Opis],
     PPPolja.[TabName],
     CountTabNames.StTabNames,
     PPPolja.FieldValueIndex,
     PPPolja.[ZapSt],
     PPPolja.[NewLine],
     PPPolja.[IdPolja],
     PPPolja.[Hidden],
     PPPolja.[Potrebno],
     PPPolja.[ShowInTable],
     PPPolja.[PrimaryKey],
     PPPolja.[UporabiPrivzetoVrednost],
     PPPolja.[OpisNadVnosom],
     PPPolja.[OpisVNaslovu],
     PPPolja.[Lookup],
     PPPolja.[FieldActions],
     PPPolja.[SecondarySourceXML],
     PPPolja.[FieldLayout],
     PPPolja.[ReadOnly],
     PPPolja.[LastValueOnly],
     PPPolja.validFrom,
     PPPolja.validTo,
    SifrantVnosnihPolj.[ImePolja]+CASE WHEN PPPolja.FieldValueIndex>1 THEN CAST(PPPolja.FieldValueIndex AS VARCHAR) ELSE '' END AS [ImePolja],
    SifrantVnosnihPolj.[Opis],
    CAST(CASE WHEN PPPolja.[PrimaryKey]=1 THEN 0 ELSE SifrantVnosnihPolj.[VecVrednosti] END AS BIT) AS [VecVrednosti],
    SifrantVnosnihPolj.[PrikazkotTabela],
    SifrantVnosnihPolj.[TipPolja],
    SifrantVnosnihPolj.[DolzinaPolja],
    SifrantVnosnihPolj.[StDecimalnihMestPolja],
    SifrantVnosnihPolj.[IdentityField],
    SifrantVnosnihPolj.[BrezSifranta],
    SifrantVnosnihPolj.[UporabiSifrantIdSifranta],
    SeznamSifrantov.Skupina as UporabiSifrantSkupina,
    SeznamSifrantov.Opis as UporabiSifrantOpis,
    SifrantVnosnihPolj.[UporabiSifrantPP],
    SifrantVnosnihPolj.[UporabiSifrantSvetovalcev],
    SifrantVnosnihPolj.[UporabiSifrantPodjetji],
    SifrantVnosnihPolj.[UporabiSifrantPonudb],
    SifrantVnosnihPolj.[UporabiSifrantXML],
    SifrantVnosnihPolj.[LookupXML],
    SifrantVnosnihPolj.[UporabiWorkAreaId],
    SifrantVnosnihPolj.[Opombe],
    1 AS Persisted
FROM
    <%ChangeLog%>.[dbo].PPPolja
INNER JOIN
    <%ChangeLog%>.[dbo].SifrantVnosnihPolj
    ON
    (
         PPPolja.IdPolja =  SifrantVnosnihPolj.Id
    )
LEFT OUTER JOIN (SELECT
    PPPolja.[TabName],
    MIN(PPPolja.ZapSt) AS MinZapSt,
    COUNT(*) AS StTabNames
    FROM <%ChangeLog%>.[dbo].PPPolja GROUP BY
    PPPolja.[TabName]) AS CountTabNames ON (
    CountTabNames.[TabName] = PPPolja.[TabName])
LEFT OUTER JOIN <%ChangeLog%>.[dbo].SeznamSifrantov ON
    SeznamSifrantov.Id = SifrantVnosnihPolj.UporabiSifrantIdSifranta
ORDER BY CountTabNames.MinZapSt,PPPolja.ZapSt,PPPolja.Id