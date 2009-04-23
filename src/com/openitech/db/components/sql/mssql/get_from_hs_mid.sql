SELECT
    [HS_s].[hs_mid],
    [HS_s].[na_mid],
    [HS_s].[ul_mid],
    [HS_s].[pt_mid],
    [HS_s].[hs],
    [HS_s].[hd],
    [NA_s].[na_uime],
    [UL_s].[ul_uime],
    [PT_s].[pt_uime],
    [PT_s].[pt_id]
FROM
    [RPE].[dbo].[HS_s]
INNER JOIN
    [RPE].[dbo].[NA_s]
    ON
    (
        [HS_s].[na_mid] = [NA_s].[na_mid]
    )
INNER JOIN
    [RPE].[dbo].[UL_s]
    ON
    (
        [HS_s].[ul_mid] = [UL_s].[ul_mid]
    )
INNER JOIN
    [RPE].[dbo].[PT_s]
    ON
    (
        [HS_s].[pt_mid] = [PT_s].[pt_mid]
    )
WHERE [HS_s].[hs_mid] = ?