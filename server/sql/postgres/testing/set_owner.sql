select 'ALTER TABLE '||schemaname||'.'||tablename||' OWNER TO '||schemaname||';' from pg_tables 
where tableowner != schemaname
and schemaname in ('pgrouting', 'osm_russia', 'owner_track');



SELECT  'ALTER FUNCTION '||n.nspname||'.'||proname||' OWNER TO '||n.nspname||';', p.*
FROM    pg_catalog.pg_proc p
JOIN    pg_authid a ON a.oid = p.proowner
JOIN    pg_namespace n ON p.pronamespace = n.oid
WHERE   n.nspname in ('pgrouting', 'osm_russia', 'owner_track')
AND n.nspname != rolname;
