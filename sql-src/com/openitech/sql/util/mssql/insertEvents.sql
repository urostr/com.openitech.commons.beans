INSERT 
INTO 
    <%ChangeLog%>.[dbo].[Events] WITH (ROWLOCK)
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