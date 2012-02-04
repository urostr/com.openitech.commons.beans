USE [master]
if not exists(select * from sys.server_principals where name = '<%username%>')
BEGIN
  CREATE LOGIN [<%username%>] WITH PASSWORD=N'<%password%>', DEFAULT_DATABASE=[ChangeLog], CHECK_EXPIRATION=OFF, CHECK_POLICY=ON
END

USE [ChangeLog]
if not exists(select * from sys.database_principals where name = '<%username%>')
BEGIN
  CREATE USER [<%username%>] FOR LOGIN [<%username%>]

  EXEC sp_addrolemember N'db_owner', N'<%username%>'
  
END


USE [MViewCache]
if not exists(select * from sys.database_principals where name = '<%username%>')
BEGIN
  CREATE USER [<%username%>] FOR LOGIN [<%username%>]

  EXEC sp_addrolemember N'db_owner', N'<%username%>'
 
END

USE [ProcessDDB]
if not exists(select * from sys.database_principals where name = '<%username%>')
BEGIN
  CREATE USER [<%username%>] FOR LOGIN [<%username%>]


  EXEC sp_addrolemember N'db_owner', N'<%username%>'
END
