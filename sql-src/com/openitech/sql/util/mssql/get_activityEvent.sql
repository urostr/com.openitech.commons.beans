SELECT TOP 1
    [ActivityId],
    [IdSifranta],
    [IdSifre]
FROM
    ChangeLog.[dbo].[ActivityEvents]
WHERE ActivityId = ?