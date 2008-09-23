(
(
SELECT DISTINCT 
    CAST(hs as varchar)+'  '+hd as hs_hd,
    hs, 
    hd 
FROM 
    HS_znane HS_s
WHERE 
    HS_s.hs_mid is null <%filter_hs%>
) 
UNION 
(
SELECT DISTINCT 
    CAST(HS_s.hs as varchar)+HS_s.hd,
    HS_s.hs, 
    HS_s.hd 
FROM 
    UL_s 
LEFT OUTER JOIN 
    HS_S
    ON 
    ( 
        UL_s.ul_mid = HS_s.ul_mid AND
        UL_s.na_mid = HS_s.na_mid
    ) 
LEFT OUTER JOIN 
    PT_s 
    ON 
    ( 
        HS_s.pt_mid = PT_s.pt_mid 
    ) 
LEFT OUTER JOIN 
    NA_s 
    ON 
    ( 
        UL_s.na_mid = NA_s.na_mid 
    ) 
WHERE 
    HS_s.hs_mid is not null <%filter_hs%>
)) 
ORDER BY hs, hd