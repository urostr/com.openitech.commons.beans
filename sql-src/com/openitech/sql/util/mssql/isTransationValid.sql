SELECT CASE WHEN @@TRANCOUNT>0 
            THEN XACT_STATE()
            ELSE -1
       END