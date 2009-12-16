INSERT 
INTO 
    <%ChangeLog%>.[dbo].[Events]
    (
        
        [IdSifranta], 
        [IdSifre],
        [IdEventSource],
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
        ?, 
        SYSTEM_USER
    )