UPDATE
  <%MVIEWCACHE%>.[DBO].[CACHED:EVENT:OBJECTS]
SET
  [Valid]=?
WHERE
  [IdSifranta] = ? AND
  [IdSifre] = ?
  