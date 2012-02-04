SELECT DISTINCT 
    PT_s.pt_mid
FROM 
    [RPE].[dbo].PT_s WITH (NOLOCK)
WHERE 
    PT_s.pt_mid is not null AND
    PT_s.pt_id = ? AND
    PT_s.pt_ime = CAST(? AS VARCHAR)

