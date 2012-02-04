
SELECT DISTINCT
    Events.[Id],
    Events.[IdSifranta],
    Events.[IdSifre],
    EventValues.*,
    SifrantVnosnihPolj.ImePolja,
    SifrantVnosnihPolj.TipPolja
FROM
    [dbo].[Events]
LEFT OUTER JOIN
    EventValues
    ON
    EventValues.eventId = events.id
INNER JOIN
    SifrantiPolja
    ON
    SifrantiPolja.IdPolja        = eventvalues.idpolja
    AND ((0=?) OR SifrantiPolja.primaryKey = 1)
INNER JOIN
    SifrantVnosnihPolj
    ON
    SifrantVnosnihPolj.id = eventvalues.idpolja 
WHERE
    events.id = ?
ORDER BY EventValues.IdPolja ASC, EventValues.FieldValueIndex ASC
