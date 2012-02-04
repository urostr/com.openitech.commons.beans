USE [master]
if exists(select * from sys.database_principals where name = '<%username%>')
BEGIN
  ALTER LOGIN [<%username%>] WITH PASSWORD=N'<%password%>', DEFAULT_DATABASE=[ChangeLog], CHECK_EXPIRATION=OFF, CHECK_POLICY=ON
END