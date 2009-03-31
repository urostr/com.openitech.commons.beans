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
    Sifranti 
INNER JOIN SeznamSifrantov ON (
    SeznamSifrantov.Id=Sifranti.IdSifranta
    <%DbSifrantModelFilter%> AND
    SeznamSifrantov.validFrom<=CURRENT_TIMESTAMP AND
    (SeznamSifrantov.validTo IS NULL OR SeznamSifrantov.validTo>=CURRENT_TIMESTAMP)
)
WHERE     
    Sifranti.validFrom<=CURRENT_TIMESTAMP AND
    (Sifranti.validTo IS NULL OR Sifranti.validTo>=CURRENT_TIMESTAMP)

))  Sifranti