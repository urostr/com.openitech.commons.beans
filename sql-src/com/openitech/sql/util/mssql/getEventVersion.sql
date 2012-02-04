SELECT [VersionId],COUNT(*) FROM <%ChangeLog%>.[dbo].[EventVersions]
  WHERE [EventId] IN (<%EVENTS_LIST%>)
GROUP BY [VersionId]
HAVING COUNT(*) = <%EVENT_LIST_SIZE%>
