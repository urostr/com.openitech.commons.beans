INSERT INTO <%ChangeLog%>.[dbo].[AppLog]
           ([Application]
           ,[Operation]
           ,[Severe]
           ,[Message])
     VALUES
           (?
           ,?
           ,?
           ,SUBSTRING(?, 1, 1400))

