SELECT DISTINCT [VersionId] FROM <%ChangeLog%>.[dbo].[EventVersions]
  WHERE [EventId] IN (<%EVENTS_LIST%>)
