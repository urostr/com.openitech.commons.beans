SELECT DISTINCT 
    HS_s.hs_mid
FROM 
    [RPE].[dbo].UL_s WITH (NOLOCK)
INNER JOIN
	[RPE].[dbo].UL_PT_s WITH (NOLOCK)
	ON (
        UL_s.ul_mid = UL_PT_s.ul_mid AND
        UL_s.na_mid = UL_PT_s.na_mid
	)
LEFT OUTER JOIN 
    [RPE].[dbo].HS_S WITH (NOLOCK)
    ON 
    ( 
        UL_s.ul_mid = HS_s.ul_mid AND
        UL_s.na_mid = HS_s.na_mid
    ) 

WHERE 
    HS_s.hs_mid is not null AND
    RTRIM(CAST(HS_s.hs as varchar)+HS_s.hd) = ? AND
    UL_s.ul_ime = ?
   