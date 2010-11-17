UPDATE
    <%ChangeLog%>.[dbo].[EventLookupKeys]
    SET 
    [VersionId]       = ?,
    [IdSifranta]      = ?,
    [IdSifre]         = ?,
    [PrimaryKey]      = ?
WHERE
    [EventId]             = ?
    and [IdPolja]         = ?
    and [FieldValueIndex] = ?
    