UPDATE
  <%MVIEWCACHE%>.[DBO].[CACHED:EVENT:OBJECTS]
SET
  [Valid]=0
WHERE
  [IdSifranta] = ? AND
  [IdSifre] = ?
  