SELECT TOP 1 Id FROM <%ChangeLog%>.[dbo].[VariousValues]
WHERE [CClobValue]=RTRIM(LEFT(?,900)) COLLATE SQL_Slovenian_CP1250_CS_AS
      AND ClobValue = ?  COLLATE SQL_Slovenian_CP1250_CS_AS