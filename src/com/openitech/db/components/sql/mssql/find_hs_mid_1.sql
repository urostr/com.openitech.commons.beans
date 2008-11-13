SELECT DISTINCT 
    HS_s.hs_mid
FROM 
    UL_s 
LEFT OUTER JOIN 
    HS_S
    ON 
    ( 
        UL_s.ul_mid = HS_s.ul_mid AND
        UL_s.na_mid = HS_s.na_mid
    ) 

WHERE 
    HS_s.hs_mid is not null AND
    (CAST(HS_s.hs as varchar)+HS_s.hd) = ? AND
    UL_s.ul_ime = ?
   
