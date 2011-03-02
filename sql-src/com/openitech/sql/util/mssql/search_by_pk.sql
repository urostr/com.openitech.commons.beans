SELECT
  COUNT(*)
FROM
  ChangeLog.dbo.Events
WHERE
  [IdSifranta] = ?
AND [IdSifre] = ?
AND valid = 1
AND NOT EXISTS
  (
    SELECT
      Id
    FROM
      ChangeLog.dbo.EventsPK
    WHERE
      EventsPK.EventId=Events.Id
  )