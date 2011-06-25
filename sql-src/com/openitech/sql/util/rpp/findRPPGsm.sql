SELECT TOP 1
    [PPID],
    [PPKontaktID],
    [PPOmreznaGSM],
    [PPStevilkaGSM]
FROM
    RPP.[dbo].[PP_MAX_VALID_GSMI] WITH (NOLOCK)
WHERE PPID = ?