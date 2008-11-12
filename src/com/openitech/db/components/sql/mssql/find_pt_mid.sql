SELECT DISTINCT 
    PT_s.pt_mid
FROM 
    PT_s 
WHERE 
    PT_s.pt_mid is not null AND
    PT_s.pt_id = ? AND
    PT_s.pt_ime = ?

