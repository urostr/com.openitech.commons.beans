INSERT
INTO
    <%ChangeLog%>.[dbo].[EventsOpombe]
    (
        [EventId],
        [OpombaId],
        [Datum],
        [SessionUser]
    )
    VALUES
    (
        ?,
        ?,
        GETDATE(),
        SYSTEM_USER
    )