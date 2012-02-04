SELECT
    [Id],
    [FieldType],
    [IntValue],
    [RealValue],
    [StringValue],
    [DateValue],
    [ObjectValue],
    [ClobValue],
    [CClobValue]
FROM
    <%ChangeLog%>.[dbo].[VariousValues]
WHERE
    Id = ?