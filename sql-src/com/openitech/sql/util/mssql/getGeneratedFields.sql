SELECT
    SifrantiPolja.[Id],
    ActivityEventsPolja.[ActivityId],
    SifrantiPolja.[IdSifranta],
    SifrantiPolja.[IdSifre],
    (SELECT Sifranti.Veljavnost FROM <%ChangeLog%>.[DBO].Sifranti
     WHERE Sifranti.IdSifranta = SifrantiPolja.[IdSifranta]
       AND Sifranti.IdSifre = SifrantiPolja.[IdSifre]) AS Veljavnost,
    SifrantiPolja.[IdPolja],
    SifrantiPolja.FieldValueIndex,
    CASE
      WHEN ActivityEventsPolja.Opis IS NOT NULL
      THEN ActivityEventsPolja.Opis
      ELSE SifrantiPolja.[Opis]
    END AS Opis,
    CASE
      WHEN ActivityEventsPolja.TabName IS NOT NULL
      THEN ActivityEventsPolja.TabName
      ELSE SifrantiPolja.[TabName]
    END AS TabName,
    CountTabNames.StTabNames,
    CASE
      WHEN ActivityEventsPolja.[ZapSt] IS NOT NULL
      THEN ActivityEventsPolja.[ZapSt]
      ELSE SifrantiPolja.[ZapSt]
    END AS [ZapSt],
    CASE
      WHEN ActivityEventsPolja.[NewLine] IS NOT NULL
      THEN ActivityEventsPolja.[NewLine]
      ELSE SifrantiPolja.[NewLine]
    END AS [NewLine],
    CASE
      WHEN ActivityEventsPolja.[Hidden] IS NOT NULL
      THEN ActivityEventsPolja.[Hidden]
      ELSE SifrantiPolja.[Hidden]
    END AS [Hidden],
    CASE
      WHEN ActivityEventsPolja.[Potrebno] IS NOT NULL
      THEN ActivityEventsPolja.[Potrebno]
      ELSE SifrantiPolja.[Potrebno]
    END AS [Potrebno],
    CASE
      WHEN ActivityEventsPolja.[ShowInTable] IS NOT NULL
      THEN ActivityEventsPolja.[ShowInTable]
      ELSE SifrantiPolja.[ShowInTable]
    END AS [ShowInTable],
    SifrantiPolja.[PrimaryKey],
    CASE
      WHEN ActivityEventsPolja.[UporabiPrivzetoVrednost] IS NOT NULL
      THEN ActivityEventsPolja.[UporabiPrivzetoVrednost]
      ELSE SifrantiPolja.[UporabiPrivzetoVrednost]
    END AS [UporabiPrivzetoVrednost],
    SifrantiPolja.[UrejajPrivzetoVrednost],
    SifrantiPolja.[NePrikaziKotDogodek],
    CASE
      WHEN ActivityEventsPolja.[OpisNadVnosom] IS NOT NULL
      THEN ActivityEventsPolja.[OpisNadVnosom]
      ELSE SifrantiPolja.[OpisNadVnosom]
    END AS [OpisNadVnosom],
    CASE
      WHEN ActivityEventsPolja.[OpisVNaslovu] IS NOT NULL
      THEN ActivityEventsPolja.[OpisVNaslovu]
      ELSE SifrantiPolja.[OpisVNaslovu]
    END AS [OpisVNaslovu],
    SifrantiPolja.[Lookup],
    CASE
      WHEN ActivityEventsPolja.[FieldActions] IS NOT NULL
      THEN (CASE WHEN ActivityEventsPolja.[FieldActions] = 'null'
              THEN null
              ELSE ActivityEventsPolja.[FieldActions]
            END)
      ELSE SifrantiPolja.[FieldActions]
    END AS [FieldActions],
    CASE
      WHEN ActivityEventsPolja.[SecondarySourceXML] IS NOT NULL
      THEN (CASE WHEN ActivityEventsPolja.[SecondarySourceXML] = 'null'
              THEN null
              ELSE ActivityEventsPolja.[SecondarySourceXML]
            END)
      ELSE SifrantiPolja.[SecondarySourceXML]
    END AS [SecondarySourceXML],
    CASE
      WHEN ActivityEventsPolja.[FieldLayout] IS NOT NULL
      THEN (CASE WHEN ActivityEventsPolja.[FieldLayout] = 'null'
              THEN null
              ELSE ActivityEventsPolja.[FieldLayout]
            END)
      ELSE SifrantiPolja.[FieldLayout]
    END AS [FieldLayout],

    CASE
      WHEN ActivityEventsPolja.[ReadOnly] IS NOT NULL
      THEN ActivityEventsPolja.[ReadOnly]
      ELSE SifrantiPolja.[ReadOnly]
    END AS [ReadOnly],
    CASE
      WHEN ActivityEventsPolja.[LastValueOnly] IS NOT NULL
      THEN ActivityEventsPolja.[LastValueOnly]
      ELSE SifrantiPolja.[LastValueOnly]
    END AS [LastValueOnly],
    SifrantiPolja.[Persisted],
    SifrantiPolja.[validFrom],
    SifrantiPolja.[validTo],
    SifrantVnosnihPolj.[ImePolja]+
    CASE
      WHEN SifrantiPolja.FieldValueIndex>1
      THEN CAST(SifrantiPolja.FieldValueIndex AS VARCHAR)
      ELSE ''
    END AS [ImePolja],
    CAST(
      CASE
        WHEN SifrantiPolja.[PrimaryKey]=1
        THEN 0
        ELSE SifrantVnosnihPolj.[VecVrednosti]
      END AS BIT) AS [VecVrednosti],
    SifrantVnosnihPolj.[PrikazkotTabela],
    SifrantVnosnihPolj.[TipPolja],
    SifrantVnosnihPolj.[DolzinaPolja],
    SifrantVnosnihPolj.[StDecimalnihMestPolja],
    SifrantVnosnihPolj.[IdentityField],
    SifrantVnosnihPolj.[BrezSifranta],
    SifrantVnosnihPolj.[UporabiSifrantIdSifranta],
    SeznamSifrantov.Skupina AS UporabiSifrantSkupina,
    SeznamSifrantov.Opis    AS UporabiSifrantOpis,
    SifrantVnosnihPolj.[UporabiSifrantPP],
    SifrantVnosnihPolj.[UporabiSifrantSvetovalcev],
    SifrantVnosnihPolj.[UporabiSifrantPodjetji],
    SifrantVnosnihPolj.[UporabiSifrantPonudb],
    SifrantVnosnihPolj.[UporabiSifrantXML],
    CASE
      WHEN SifrantiPolja.[LookupXML] is not null
      THEN SifrantiPolja.[LookupXML]
      ELSE SifrantVnosnihPolj.[LookupXML]
    END AS [LookupXML],

    SifrantVnosnihPolj.[UporabiWorkAreaId],
    SifrantVnosnihPolj.[Opombe],
    SifrantiPolja.[Changed],
    ActivityEventsPolja.[Changed],
    CAST(
      CASE
        WHEN ActivityEventsPolja.[Changed] IS NULL
        THEN 0
        ELSE ActivityEventsPolja.[Changed]
      END AS BINARY(8))+CAST(SifrantiPolja.[Changed] AS BINARY(8)) AS [Changed]
FROM
    <%ChangeLog%>.[dbo].[SifrantiPolja] as SifrantiPolja
LEFT OUTER JOIN
    <%ChangeLog%>.[dbo].SifrantVnosnihPolj as SifrantVnosnihPolj
    ON
    SifrantVnosnihPolj.Id = SifrantiPolja.IdPolja
LEFT OUTER JOIN
    <%ChangeLog%>.[dbo].SeznamSifrantov
    ON
    SeznamSifrantov.Id = SifrantVnosnihPolj.[UporabiSifrantIdSifranta]
LEFT OUTER JOIN (SELECT
    SifrantiPolja.[IdSifranta],
    SifrantiPolja.[IdSifre],
    CASE WHEN ActivityEventsPolja.[TabName] IS NOT NULL THEN ActivityEventsPolja.[TabName] ELSE SifrantiPolja.[TabName] END [TabName],
    MIN(CASE WHEN ActivityEventsPolja.[ZapSt] IS NOT NULL THEN ActivityEventsPolja.[ZapSt] ELSE SifrantiPolja.[ZapSt] END) AS MinZapSt,
    COUNT(*) AS StTabNames
    FROM <%ChangeLog%>.[dbo].[SifrantiPolja] LEFT OUTER JOIN
    <%ChangeLog%>.[dbo].[ActivityEventsPolja]
    ON
    ActivityEventsPolja.ActivityId = ? AND
    ActivityEventsPolja.ActivityIdSifranta = ? AND
    ActivityEventsPolja.ActivityIdSifre = ? AND
    ActivityEventsPolja.IdSifranta = SifrantiPolja.[IdSifranta] AND
    ActivityEventsPolja.IdSifre = SifrantiPolja.[IdSifre] AND
    ActivityEventsPolja.IdPolja = SifrantiPolja.[IdPolja] AND
    ActivityEventsPolja.FieldValueIndex = SifrantiPolja.FieldValueIndex
GROUP BY
    SifrantiPolja.[IdSifranta],
    SifrantiPolja.[IdSifre],
    SifrantiPolja.[TabName],
    ActivityEventsPolja.[TabName]) AS CountTabNames ON (
    CountTabNames.[IdSifranta] = SifrantiPolja.[IdSifranta] AND
    CountTabNames.[IdSifre] = SifrantiPolja.[IdSifre] AND
    CountTabNames.[TabName] = SifrantiPolja.[TabName])
LEFT OUTER JOIN
    <%ChangeLog%>.[dbo].[ActivityEventsPolja]
    ON
    ActivityEventsPolja.ActivityId = ? AND
    ActivityEventsPolja.ActivityIdSifranta = ? AND
    ActivityEventsPolja.ActivityIdSifre = ? AND
    ActivityEventsPolja.IdSifranta = SifrantiPolja.[IdSifranta] AND
    ActivityEventsPolja.IdSifre = SifrantiPolja.[IdSifre] AND
    ActivityEventsPolja.IdPolja = SifrantiPolja.[IdPolja] AND
    ActivityEventsPolja.FieldValueIndex = SifrantiPolja.FieldValueIndex
WHERE
    SifrantiPolja.IdSifranta = ?
    AND (1=? OR SifrantiPolja.IdSifre= ?)
    AND (1=? OR SifrantiPolja.Hidden = 0)
ORDER BY
    -- CountTabNames.MinZapSt,
 SifrantiPolja.ZapSt, SifrantiPolja.[Id]