USE [ChangeLog]
if exists(select * from sys.database_principals where name = '<%username%>')
BEGIN
  DROP USER [<%username%>]
END

USE [MViewCache]
if exists(select * from sys.database_principals where name = '<%username%>')
BEGIN
  DROP USER [<%username%>]
END

USE [ProcessDDB]

if exists(select * from sys.database_principals where name = '<%username%>')
BEGIN
  DROP USER [<%username%>]
END
USE [master]

if exists(select * from sys.server_principals where name = '<%username%>')
BEGIN
  DROP LOGIN [<%username%>]
END