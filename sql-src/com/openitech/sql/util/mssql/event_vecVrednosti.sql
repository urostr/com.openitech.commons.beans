SELECT COUNT(*)
FROM
    <%ChangeLog%>.[dbo].[SifrantiPolja]
LEFT OUTER JOIN
<%ChangeLog%>.dbo.SifrantVnosnihPolj
ON
SifrantVnosnihPolj.id = SifrantiPolja.IDPolja
WHERE SifrantiPolja.Idsifranta = ? AND SifrantiPolja.IdSifre = ? AND SifrantVnosnihPolj.VecVrednosti=1