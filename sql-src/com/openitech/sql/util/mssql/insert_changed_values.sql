INSERT INTO <%ChangeLog%>.[dbo].[ChangeLogValues] WITH (ROWLOCK)
           ([ChangeId]
           ,[FieldName]
           ,[NewValueId]
           ,[OldValueId])
     VALUES
           (?
           ,?
           ,?
           ,?)