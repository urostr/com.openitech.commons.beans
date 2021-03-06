SELECT DISTINCT   
    NA_s.na_mid
FROM         
    [RPE].[dbo].NA_s WITH (NOLOCK)
LEFT OUTER JOIN
    [RPE].[dbo].UL_PT_s WITH (NOLOCK)
    ON 
    (
            NA_s.na_mid = UL_PT_s.na_mid 
    )        
LEFT OUTER JOIN
    [RPE].[dbo].PT_s WITH (NOLOCK)
    ON 
    (
            UL_PT_s.pt_mid = PT_s.pt_mid  
    )       
WHERE 
     NA_s.na_mid is not null AND
    PT_s.pt_id = ? AND
    NA_s.na_ime = CAST(? AS VARCHAR)
    

