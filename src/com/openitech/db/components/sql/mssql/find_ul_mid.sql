SELECT DISTINCT 
    UL_s.ul_mid
FROM         
    UL_s 
LEFT OUTER JOIN
    UL_PT_s 
    ON 
    (
              UL_s.ul_mid = UL_PT_s.ul_mid 
    )             
LEFT OUTER JOIN
    PT_s 
    ON 
    (
              UL_PT_s.pt_mid = PT_s.pt_mid 
    )          
LEFT OUTER JOIN
    NA_s 
    ON 
    (
              UL_s.na_mid = NA_s.na_mid    
    )
WHERE 
    UL_s.ul_mid is not null AND
    UL_s.ul_ime = ? AND
    PT_s.pt_id = ? AND
    NA_s.na_ime = ?

