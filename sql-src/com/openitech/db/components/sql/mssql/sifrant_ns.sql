(
(
SELECT DISTINCT 
    na_mid,
    na_ime, 
    na_uime 
FROM 
    [RPE].[dbo].HS_neznane NA_s
WHERE 
    NA_s.na_mid is null <%filter_na%>
) 
UNION 
(
SELECT DISTINCT 
    NA_s.na_mid,
    NA_s.na_ime, 
    NA_s.na_uime 
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
    NA_s.na_mid is not null <%filter_na%>
)) 