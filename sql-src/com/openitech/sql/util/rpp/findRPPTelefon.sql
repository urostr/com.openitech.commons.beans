SELECT TOP 1
    [PPID],
    [PPKontaktID],
    [PPOmreznaTelefon],
    [PPStevilkaTelefon]
FROM
    RPP.[dbo].[PP_MAX_VALID_TELEFONI] WITH (NOLOCK)
WHERE PPID = ?