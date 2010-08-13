SELECT
    TOP 1 EventsOpombe.Id,
    (
    SELECT
        VariousValues.ClobValue
    FROM
        <%ChangeLog%>.dbo.VariousValues
    WHERE
        VariousValues.id = EventsOpombe.OpombaId
    ) AS ClobValue
FROM
    <%ChangeLog%>.[dbo].[EventsOpombe]
WHERE
    EventID = ?
ORDER BY
    EventsOpombe.Datum DESC