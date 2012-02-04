SELECT
  [Id],
  [IdSifranta],
  [IdSifre],
  [Object],
  [Valid],
  [CacheOnUpdate]
FROM
  <%MVIEWCACHE%>.[DBO].[CACHED:EVENT:OBJECTS] WITH (NOLOCK)
WHERE IdSifranta = ?
AND IdSifre = ?