(
(
SELECT DISTINCT 
    RTRIM(CAST(hs as varchar)+hd) as hs_hd,
    hs, 
    hd 
FROM 
    [RPE].[dbo].HS_neznane HS_s
WHERE 
    HS_s.hs_mid is null <%filter_hs%>
) 
UNION 
(
SELECT DISTINCT 
    RTRIM(CAST(HS_s.hs as varchar)+HS_s.hd),
    HS_s.hs, 
    HS_s.hd 
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
        UL_PT_s.ul_mid = HS_s.ul_mid AND
        UL_PT_s.na_mid = HS_s.na_mid
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
        UL_PT_s.na_mid = NA_s.na_mid 
    ) 
WHERE 
    HS_s.hs_mid is not null <%filter_hs%>
)) 
ORDER BY hs, hd