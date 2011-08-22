UPDATE
    <%ChangeLog%>.[dbo].[EventValues] 
    SET
    [ValueId]         = ?
WHERE
    [EventId]             = ?
    and [IdPolja]       = ?
    and [FieldValueIndex] = ?