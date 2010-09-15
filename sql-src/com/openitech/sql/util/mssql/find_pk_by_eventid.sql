SELECT
    Events.Id,
    Events.IdSifranta,
    Events.IdSifre,
    EventValues.IdPolja,
    EventValues.FieldValueIndex,
    EventValues.ValueId
FROM
    [Events]
LEFT OUTER JOIN
    EventValues
    ON
    EventValues.EventId = Events.id
INNER JOIN
    SifrantiPolja
    ON
    SifrantiPolja.IdSifranta = Events.IdSifranta
    AND SifrantiPolja.IdSifre = Events.IdSifre
    AND SifrantiPolja.IdPolja = EventValues.IdPolja
    AND SifrantiPolja.PrimaryKey = 1
WHERE Events.Id = ?