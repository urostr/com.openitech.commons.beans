(
(
SELECT DISTINCT 
    na_mid,
    na_ime, 
    na_uime 
FROM 
    HS_neznane NA_s
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
    UL_s 
INNER JOIN 
	UL_PT_s
	ON (
        UL_s.ul_mid = UL_PT_s.ul_mid AND
        UL_s.na_mid = UL_PT_s.na_mid
	)
LEFT OUTER JOIN 
    HS_S
    ON 
    ( 
        UL_PT_s.ul_mid = HS_s.ul_mid AND
        UL_PT_s.na_mid = HS_s.na_mid
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
        UL_PT_s.na_mid = NA_s.na_mid 
    )
WHERE 
    NA_s.na_mid is not null <%filter_na%>
)) 