DECLARE @EventValues T_EVENT_VALUES

INSERT INTO @EventValues([EventId]
           ,[IdPolja]
           ,[FieldValueIndex]
           ,[ValueId]
           ,[FieldType]
           ,[IntValue]
           ,[RealValue]
           ,[StringValue]
           ,[DateValue]
           ,[ObjectValue]
           ,[ClobValue])
SELECT * FROM #T_EVENT_VALUES

EXECUTE [dbo].[StoreEventValues]
 @EventValues

