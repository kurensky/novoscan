-- Function: owner_track.get_login_name(bigint)

-- DROP FUNCTION owner_track.get_login_name(bigint);

CREATE OR REPLACE FUNCTION owner_track.get_login_name(loginid bigint)
  RETURNS character varying AS
$BODY$
DECLARE
  l_name character varying(30);
  lrl CURSOR(n_id BIGINT)
    IS
    SELECT r.acct_login
      FROM accounts r
     WHERE r.acct_id = n_id
     ;

BEGIN
  OPEN lrl(loginid);
  FETCH lrl INTO l_name;
  CLOSE lrl;
  RETURN l_name;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE SECURITY DEFINER
  COST 100;
ALTER FUNCTION owner_track.get_login_name(bigint)
  OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION owner_track.get_login_name(bigint) TO public;
GRANT EXECUTE ON FUNCTION owner_track.get_login_name(bigint) TO owner_track;
GRANT EXECUTE ON FUNCTION owner_track.get_login_name(bigint) TO track_server_all;
COMMENT ON FUNCTION owner_track.get_login_name(bigint) IS 'Логин по ИД аккаунта';
