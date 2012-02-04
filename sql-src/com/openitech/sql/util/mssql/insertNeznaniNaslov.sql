INSERT INTO [RPE].[dbo].[HS_neznane]
           (
            [pt_mid]
           ,[pt_id]
           ,[pt_ime]
           ,[pt_uime]
           ,[na_mid]
           ,[na_ime]
           ,[na_uime]
           ,[ul_mid]
           ,[ul_ime]
           ,[ul_uime]           
           ,[hs]
           ,[hd]
           ,[izvor]
           ,[valid_from]
            )
           
     VALUES
           (
           
           ?,
           ?,
           ?,
           ?,
           ?,
           ?,
           ?,
           ?,
           ?,
           ?,
           ?,                      
           ?,
           ?,
           GETDATE()      
           
        )