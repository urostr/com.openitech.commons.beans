SELECT TOP 1
       [ID_PP],
      [ULICA],
      [HI�NA_�TEVILKA] AS HisnaStevilka,
      [ID_PO�TA] AS PostnaStevilka,
      [HS_MID],
      [PO�TA] AS Posta,
      [NASELJE],
      [PO�TA_MID],
      [NASELJE_MID],
      [ULICA_MID],
      [ID_NASLOVA],
      [ID_RPP_OSEBE]
  FROM [ChangeLog].[dbo].[E_275_RPPNS01_valid] WITH (NOLOCK, NOEXPAND)
  WHERE ID_PP = ?
  ORDER BY [ID_NASLOVA] DESC


