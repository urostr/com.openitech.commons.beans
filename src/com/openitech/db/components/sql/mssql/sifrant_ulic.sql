(
SELECT DISTINCT 
    UL_s.ul_ime, 
    UL_s.ul_uime 
FROM 
    [RPE].[dbo].HS_neznane UL_s
WHERE 
    UL_s.ul_mid is null <%filter_ulice%>
) 
UNION 
(
SELECT DISTINCT 
    UL_s.ul_ime, 
    UL_s.ul_uime 
FROM 
    [RPE].[dbo].UL_s
INNER JOIN 
	[RPE].[dbo].UL_PT_s
	ON (
        UL_s.ul_mid = UL_PT_s.ul_mid AND
        UL_s.na_mid = UL_PT_s.na_mid
	)
LEFT OUTER JOIN 
    [RPE].[dbo].HS_S
    ON 
    ( 
        UL_PT_s.ul_mid = HS_s.ul_mid AND
        UL_PT_s.na_mid = HS_s.na_mid
    ) 
LEFT OUTER JOIN 
    [RPE].[dbo].PT_s
    ON 
    ( 
        UL_PT_s.pt_mid = PT_s.pt_mid 
    ) 
LEFT OUTER JOIN 
    [RPE].[dbo].NA_s
    ON 
    ( 
        UL_PT_s.na_mid = NA_s.na_mid 
    ) 
WHERE 
    UL_s.ul_mid is not null <%filter_ulice%>
) 
ORDER BY UL_s.ul_uime