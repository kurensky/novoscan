-- Function: owner_track.get_role_name(bigint)

-- DROP FUNCTION owner_track.get_role_name(bigint);

CREATE OR REPLACE FUNCTION owner_track.get_role_name(roleid bigint)
  RETURNS character varying AS
$BODY$
DECLARE
  r_name character varying(30);
  grl CURSOR(n_id BIGINT)
    IS
    SELECT r.role_name
      FROM roles r
     WHERE r.role_id = n_id
     ;

BEGIN
  OPEN grl(roleid);
  FETCH grl INTO r_name;
  CLOSE grl;
  RETURN r_name;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE SECURITY DEFINER
  COST 100;
ALTER FUNCTION owner_track.get_role_name(bigint)
  OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION owner_track.get_role_name(bigint) TO public;
GRANT EXECUTE ON FUNCTION owner_track.get_role_name(bigint) TO owner_track;
GRANT EXECUTE ON FUNCTION owner_track.get_role_name(bigint) TO track_server_all;
COMMENT ON FUNCTION owner_track.get_role_name(bigint) IS 'Имя роли';

