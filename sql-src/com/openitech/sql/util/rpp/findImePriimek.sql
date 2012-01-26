SELECT TOP 1
       [ID_PP]

      ,[RPP_NAZIV_IME] AS Ime
      ,[RPP_NAZIV_PRIIMEK] AS Priimek

  FROM [ChangeLog].[dbo].[E_225_RPPTIP01_valid] WITH (NOLOCK, NOEXPAND)
  WHERE ID_PP = ? 


