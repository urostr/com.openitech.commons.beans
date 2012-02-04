INSERT INTO <%ChangeLog%>.[dbo].[AppLog]
           ([Application]
           ,[Operation]
           ,[Level]
           ,[Message])
     VALUES
           (?
           ,?
           ,?
           ,SUBSTRING(?, 1, 100))

