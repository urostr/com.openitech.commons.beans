SELECT TOP 1
    [WorkAreaXML]
FROM
    ChangeLog.[dbo].[WorkArea]
WHERE validTo is null
<%Filter%>

ORDER BY ZapSt