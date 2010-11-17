DELETE
FROM
    <%ChangeLog%>.[dbo].[EventValues]
WHERE
    [EventId]             = ?
    and [IdPolja]         = ?
    and [FieldValueIndex] = ?