INSERT
INTO
    [ChangeLog]
    (
        [Application],
        [Database],
        [Table],
        [SessionUser],
        [Operation],
        [OperationDate]
    )
    VALUES
    (
        ?,
        ?,
        ?,
        SYSTEM_USER,
        ?,
        GETDATE()
    )