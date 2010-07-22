INSERT 
INTO 
    <%ChangeLog%>.[dbo].[EventValues] WITH (ROWLOCK)
    (
        [EventId], 
        [IdPolja],
        [FieldValueIndex], 
        [ValueId]
    ) 
    VALUES 
    (
        ?, 
        ?, 
        ?, 
        ?
    )