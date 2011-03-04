DELETE FROM
  <%MVIEWCACHE%>.[DBO].[CACHED:EVENT:OBJECTS]
WHERE
    [IdSifranta] = ? AND
    [IdSifre] = ? AND
    [Object] = ?