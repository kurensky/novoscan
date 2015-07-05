-- Function: get_mod_id(character varying, bigint)

-- DROP FUNCTION get_mod_id(character varying, bigint);

CREATE OR REPLACE FUNCTION get_mod_id(i_uid character varying, i_type bigint)
  RETURNS bigint AS
$BODY$
DECLARE
  o_spmd_id int8;
  mviews RECORD;
BEGIN
  o_spmd_id := NULL;
  FOR mviews IN
  SELECT spmd_id
    FROM sprv_modules
   WHERE spmd_uid::varchar = i_uid
     AND spmd_spmt_id = i_type
     ORDER BY spmd_id
  LOOP
    o_spmd_id := mviews.spmd_id;
    EXIT;
  END LOOP;
  RETURN o_spmd_id;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION get_mod_id(character varying, bigint) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_mod_id(character varying, bigint) TO public;
GRANT EXECUTE ON FUNCTION get_mod_id(character varying, bigint) TO owner_track;
GRANT EXECUTE ON FUNCTION get_mod_id(character varying, bigint) TO track_server_all;
COMMENT ON FUNCTION get_mod_id(character varying, bigint) IS 'Процедура получения идентификатора блока по UID модуля и типу';
