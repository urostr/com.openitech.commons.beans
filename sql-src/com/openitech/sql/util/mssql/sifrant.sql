SELECT * FROM (
(SELECT 
    0 AS OrderId,
    (null) as IdKey,
    (null) as IdSifranta,
    (null) as IdSifre,
    (null) as ZapSt,
    <%DbSifrantModelDescription%> as Opis
 )
UNION ALL 
(SELECT 
    1 AS OrderId,
    cast(Sifranti.IdSifranta as varchar)+'-'+cast(Sifranti.IdSifre as varchar) as IdKey, 
    Sifranti.IdSifranta,
    Sifranti.IdSifre,
    Sifranti.ZapSt,
    Sifranti.Opis
FROM 
    <%tb_sifranti%> Sifranti
LEFT OUTER JOIN <%tb_seznam_sifrantov%> SeznamSifrantov ON (
    SeznamSifrantov.Id=Sifranti.IdSifranta
)
WHERE <%DbSifrantModelFilter%> AND <%ValuesConstraint%>
    SeznamSifrantov.validFrom<=CURRENT_TIMESTAMP AND
    (SeznamSifrantov.validTo IS NULL OR SeznamSifrantov.validTo>=CURRENT_TIMESTAMP) AND
    Sifranti.validFrom<=CURRENT_TIMESTAMP AND
    (Sifranti.validTo IS NULL OR Sifranti.validTo>=CURRENT_TIMESTAMP)

))  Sifranti
ORDER BY OrderId,IdSifranta,ZapSt,IdSifre