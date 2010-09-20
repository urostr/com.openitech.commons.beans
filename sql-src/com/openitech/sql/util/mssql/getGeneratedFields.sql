SELECT
    *
FROM
    <%GENERATED_FIELDS%> as GeneratedFields
WHERE
    GeneratedFields.IdSifranta = ?
    AND (1=? OR GeneratedFields.IdSifre= ?)
    AND (1=? OR GeneratedFields.Hidden = 0)
    AND ((? IS NULL AND GeneratedFields.[ActivityId] IS NULL) OR
         (? IS NOT NULL AND GeneratedFields.[ActivityId]=?))
ORDER BY
    -- CountTabNames.MinZapSt,
 GeneratedFields.ZapSt, GeneratedFields.[Id]