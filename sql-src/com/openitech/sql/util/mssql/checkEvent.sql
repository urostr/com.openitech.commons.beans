SELECT COUNT(*)
FROM
    <%ChangeLog%>.[DBO].[Events]
WHERE
    [Id] = ? AND valid = 1