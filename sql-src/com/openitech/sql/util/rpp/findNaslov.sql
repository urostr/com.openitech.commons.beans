SELECT TOP 1
       [ID_PP],
      [ULICA],
      [HIŠNA_ŠTEVILKA] AS HisnaStevilka,
      [ID_POŠTA] AS PostnaStevilka,
      [HS_MID],
      [POŠTA] AS Posta,
      [NASELJE],
      [POŠTA_MID],
      [NASELJE_MID],
      [ULICA_MID],
      [ID_NASLOVA],
      [ID_RPP_OSEBE]
  FROM [ChangeLog].[dbo].[E_275_RPPNS01_valid] WITH (NOLOCK, NOEXPAND)
  WHERE ID_PP = ?
  ORDER BY [ID_NASLOVA] DESC


