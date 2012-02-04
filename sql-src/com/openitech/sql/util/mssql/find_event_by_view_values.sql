/****** Script for SelectTopNRows command from SSMS  ******/
SELECT <%ev_result_limit%>
       [Id]
      ,[EventId]
      ,[IdSifranta]
      ,[IdSifre]
      ,[IdEventSource]
      ,[Version]
      ,[Datum]
      ,[Opomba]
      ,[SessionUser]
      ,[valid]
      ,[validFrom]
      ,[validTo]
      ,[ChangedOn]
      ,[ChangedBy]
      ,[DatumSpremembe]
      ,[ID_BLOKADE]
      ,[BLOKADA_DATUM]
      ,[BLOKADA_STATUS]
      ,[BLOKADA_TIP]
      ,[BLOKADA_VSEBINA]
      ,[RPP_BLOKADA_RAZLOG]
      ,[BLOKADA_NIVO]
      ,[BLOKADA_OPOMBE]
  FROM [ChangeLog].[dbo].[E_269_BL01]
  WHERE ID_BLOKADE = CAST('AAA000000038' AS VARCHAR)

SELECT <%ev_result_limit%>
    ev.[Id],
    ev.[Id] as [EventId],
    ev.[IdSifranta],
    ev.[IdSifre],
    ev.[IdEventSource],
    ev.[VersionId],
    ev.[Version],
    ev.[Datum],
    ev.[DatumSpremembe]<%ev_field_results%>
FROM (<%ev_events_subquery%>) ev
<%ev_values_filter%>
<%ev_valid_filter%>
