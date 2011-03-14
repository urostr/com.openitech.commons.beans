SELECT ev.*, --versioned secondary
      CAST(<%ev_version_filter%> as int) as VersionId
FROM
  <%ChangeLog%>.[dbo].[Events] ev WITH (NOLOCK)
WHERE ev.id IN (SELECT eventid
                FROM <%ChangeLog%>.[dbo].[EventVersions]
                WHERE versionid = <%ev_version_filter%>) AND
<%ev_type_filter%> <%ev_source_filter%> <%ev_date_filter%> <%ev_version_pk_filter%>