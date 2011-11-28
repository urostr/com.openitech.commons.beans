SELECT
    SifrantVnosnihPolj.Id AS IdPolja,
    SifrantVnosnihPolj.ImePolja,
    SifrantVnosnihPolj.TipPolja,
    [FieldValueIndex]
FROM
    ChangeLog.[dbo].[SifrantiPolja] WITH (NOLOCK)
INNER JOIN ChangeLog.[dbo].SifrantVnosnihPolj WITH (NOLOCK)
ON SifrantVnosnihPolj.Id = SifrantiPolja.IdPolja

WHERE IdSifranta = ?
AND IdSifre = CAST(? AS VARCHAR(700))
AND PrimaryKey = 1
