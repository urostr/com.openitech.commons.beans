INSERT 
INTO 
    <%ChangeLog%>.[dbo].[Events] WITH (ROWLOCK)
    (
        
        [IdSifranta], 
        [IdSifre],
        [IdEventSource],
        [Datum], 
        
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