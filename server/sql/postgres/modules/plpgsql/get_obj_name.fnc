-- Function: get_obj_name(bigint)

-- DROP FUNCTION get_obj_name(bigint);

CREATE OR REPLACE FUNCTION get_obj_name(i_spob_id bigint)
  RETURNS text AS
$BODY$
DECLARE
  t_name text;
BEGIN
  SELECT spob_name INTO t_name FROM sprv_objects WHERE spob_id = i_spob_id;
  RETURN t_name;
END;
$BODY$
  LANGUAGE 'plpgsql' STABLE SECURITY DEFINER;
ALTER FUNCTION get_obj_name(bigint) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_obj_name(bigint) TO public;
GRANT EXECUTE ON FUNCTION get_obj_name(bigint) TO owner_track;
GRANT EXECUTE ON FUNCTION get_obj_name(bigint) TO track_server_all;
COMMENT ON FUNCTION get_obj_name(bigint) IS 'Процедура получения наименования объекта';

