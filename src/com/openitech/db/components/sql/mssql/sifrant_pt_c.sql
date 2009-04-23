SELECT COUNT(*) FROM (
(
SELECT DISTINCT 
    pt_id,
    pt_ime, 
    pt_uime 
FROM 
    [RPE].[dbo].HS_neznane PT_s
WHERE 
    PT_s.pt_mid is null <%filter_pt%>
) 
UNION 
(
SELECT DISTINCT 
    PT_s.pt_id,
    PT_s.pt_ime, 
    PT_s.pt_uime 
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
    PT_s.pt_mid is not null <%filter_pt%>
)) u
