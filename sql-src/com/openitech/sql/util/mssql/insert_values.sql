INSERT INTO <%ChangeLog%>.[dbo].[VariousValues] WITH (ROWLOCK)
           ([FieldType]
           ,[IntValue]
           ,[RealValue]
           ,[StringValue]
           ,[DateValue]
           ,[ObjectValue]
           ,[ClobValue])
     VALUES
           (?
           ,?
           ,?
           ,?
           ,?
           ,?
           ,?)