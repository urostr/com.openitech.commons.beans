IF OBJECT_ID('tempdb..#T_EVENT_VALUES') IS NULL 
BEGIN
CREATE TABLE #T_EVENT_VALUES (
	[EventId] [int] NOT NULL,
	[IdPolja] [int] NOT NULL,
	[FieldValueIndex] [int] NOT NULL,
	[ValueId] [bigint] NULL,
	[FieldType] [int] NOT NULL,
	[IntValue] [bigint] NULL,
	[RealValue] [real] NULL,
	[StringValue] [varchar](108) NULL,
	[DateValue] [datetime] NULL,
	[ObjectValue] [varbinary](max) NULL,
	[ClobValue] [varchar](max) NULL,
	PRIMARY KEY CLUSTERED
(
	[EventId] ASC,
	[IdPolja] ASC,
	[FieldValueIndex] ASC
)
)
END