SELECT COUNT(*) FROM (
(SELECT 
    0 AS ZAP_ST,
    (null) as IdKey,
    (null) as IdSifranta,
    (null) as IdSifre,
    <%DbSifrantModelDescription%> as Opis
 )
UNION ALL 
(SELECT 
    1 AS ZAP_ST,
    cast(Sifranti.IdSifranta as varchar)+'-'+cast(Sifranti.IdSifre as varchar) as IdKey, 
    Sifranti.IdSifranta,
    Sifranti.IdSifre,
    Sifranti.Opis
FROM 
    <%tb_sifranti%> Sifranti
INNER JOIN <%tb_seznam_sifrantov%> SeznamSifrantov ON (
    SeznamSifrantov.Id=Sifranti.IdSifranta
)
WHERE <%DbSifrantModelFilter%> AND <%ValuesConstraint%>
    SeznamSifrantov.validFrom<=<%ValidDate%> AND
    (SeznamSifrantov.validTo IS NULL OR SeznamSifrantov.validTo>=<%ValidDate%>) AND
    Sifranti.validFrom<=<%ValidDate%> AND
    (Sifranti.validTo IS NULL OR Sifranti.validTo>=<%ValidDate%>)

))  Sifranti