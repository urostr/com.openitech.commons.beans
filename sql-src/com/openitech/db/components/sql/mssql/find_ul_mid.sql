SELECT DISTINCT 
    UL_s.ul_mid
FROM         
    [RPE].[dbo].UL_s WITH (NOLOCK)
INNER JOIN
    [RPE].[dbo].UL_PT_s WITH (NOLOCK)
    ON 
    (
              UL_s.ul_mid = UL_PT_s.ul_mid 
    )             
LEFT OUTER JOIN
    [RPE].[dbo].PT_s WITH (NOLOCK)
    ON 
    (
              UL_PT_s.pt_mid = PT_s.pt_mid 
    )          
LEFT OUTER JOIN
    [RPE].[dbo].NA_s WITH (NOLOCK)
    ON 
    (
              UL_s.na_mid = NA_s.na_mid    
    )
WHERE 
    UL_s.ul_mid is not null AND
    UL_s.ul_ime = CAST(? AS VARCHAR) AND
    PT_s.pt_id = ? AND
    NA_s.na_ime = CAST(? AS VARCHAR)

