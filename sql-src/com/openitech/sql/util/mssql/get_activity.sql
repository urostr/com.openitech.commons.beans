SELECT
    Activity.[id],
    Activity.[Opis]
FROM
    ChangeLog.[dbo].[WorkArea]
INNER JOIN ChangeLog.[dbo].[Activity]
ON Activity.Id = WorkArea.ActivityId AND Activity.validTo is null

WHERE WorkArea.validTo is null
AND WorkArea.Id = ?