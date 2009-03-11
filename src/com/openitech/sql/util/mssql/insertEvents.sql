INSERT 
INTO 
    [Events] 
    (
        
        [IdSifranta], 
        [IdSifre], 
        [Datum], 
        [Opomba], 
        [SessionUser]
    ) 
    VALUES 
    (
        
        ?, 
        ?, 
        ?,
        ?, 
        SYSTEM_USER
    )