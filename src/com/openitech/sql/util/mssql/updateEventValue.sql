UPDATE
    <%ChangeLog%>.[dbo].[EventValues] WITH (ROWLOCK)
    SET
    [ValueId]         = ?
WHERE
    [EventId]             = ?
    and [IdPolja]       = ?
    and [FieldValueIndex] = ?