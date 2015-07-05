-- Function: get_obj_depa_name(bigint)

-- DROP FUNCTION get_obj_depa_name(bigint);

CREATE OR REPLACE FUNCTION get_obj_depa_name(i_spob_id bigint)
  RETURNS text AS
$BODY$
DECLARE
  t_name text;
BEGIN
  SELECT spdp_name INTO t_name 
    FROM sprv_objects, sprv_clients, sprv_departs 
   WHERE spob_spcl_id = spcl_id 
     AND spob_id = i_spob_id
     AND spdp_id = spcl_spdp_id;
  RETURN t_name;
END;
$BODY$
  LANGUAGE 'plpgsql' STABLE SECURITY DEFINER;
ALTER FUNCTION get_obj_depa_name(bigint) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_obj_depa_name(bigint) TO public;
GRANT EXECUTE ON FUNCTION get_obj_depa_name(bigint) TO owner_track;
GRANT EXECUTE ON FUNCTION get_obj_depa_name(bigint) TO track_server_all;
COMMENT ON FUNCTION get_obj_depa_name(bigint) IS 'Процедура получения наименования фирмы объекта';
