INSERT
  INTO
    <%ChangeLog%>.[DBO].[Scheduler]
    (
        [EventId] ,
        [ActionTime] ,
        [ActionType]
    )
 (
    SELECT
      Events.Id ,
      <%ChangeLog%>.dbo.sumDateTime(Events.ValidFrom, ?),
      1 AS [ActionType]
    FROM <%ChangeLog%>.[DBO].Events
    WHERE Id = ?
)