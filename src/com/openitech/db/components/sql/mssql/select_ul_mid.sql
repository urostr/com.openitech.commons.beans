SELECT   
    HS_s.hs_mid,
    HS_s.hs,
    HS_s.hd, 
    UL_s.ul_mid, 
    UL_s.ul_ime, 
    UL_s.ul_uime, 
    PT_s.pt_mid, 
    PT_s.pt_id, 
    PT_s.pt_ime,
    PT_s.pt_uime,
    NA_s.na_mid, 
    NA_s.na_ime, 
    NA_s.na_uime
FROM       
    UL_s 
INNER JOIN 
	UL_PT_s
	ON (
        UL_s.ul_mid = UL_PT_s.ul_mid AND
        UL_s.na_mid = UL_PT_s.na_mid
	)
LEFT OUTER JOIN
    HS_s 
        ON ( 
        UL_s.ul_mid = HS_s.ul_mid AND
        UL_s.na_mid = HS_s.na_mid 
            )
LEFT OUTER JOIN 
    PT_s 
    ON 
    ( 
        UL_PT_s.pt_mid = PT_s.pt_mid 
    ) 
LEFT OUTER JOIN
    NA_s 
        ON (
        UL_s.na_mid = NA_s.na_mid
        )
WHERE 
    UL_s.ul_mid = ?

