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
        GETDATE(),
        ?, 
        SYSTEM_USER
    )