SELECT
    [id],
    [pt_mid],
    [pt_id],
    [pt_ime],
    [pt_uime],
    [na_mid],
    [na_ime],
    [na_uime],
    [ul_mid],
    [ul_ime],
    [ul_uime],
    [hs_mid],
    [hs],
    [hd],
    [izvor],
    [valid_from],
    [valid_to]
FROM
    [HS_neznane]
WHERE
    [pt_id]  = ?,
    [pt_ime] = ?,
    [na_ime] = ?,
    [ul_ime] = ?,
    [hs]     = ?,
    [hd]     = ?
