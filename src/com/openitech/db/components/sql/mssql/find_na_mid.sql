SELECT DISTINCT   
    NA_s.na_mid
FROM         
    NA_s 
LEFT OUTER JOIN
    UL_PT_s 
    ON 
    (
            NA_s.na_mid = UL_PT_s.na_mid 
    )        
LEFT OUTER JOIN
    PT_s 
    ON 
    (
            UL_PT_s.pt_mid = PT_s.pt_mid  
    )       
WHERE 
     NA_s.na_mid is not null AND
    PT_s.pt_id = ? AND
    NA_s.na_ime = ? 
    

